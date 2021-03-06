package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.auth.shiro.SessionInMemoryDAO;
import com.lemon.framework.auth.shiro.ShiroSession;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.*;
import org.apache.shiro.session.mgt.AbstractSessionManager;
import org.redisson.RedissonScript;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.io.Serializable;
import java.util.*;

import static com.lemon.framework.auth.shiro.redisson.RedissonSessionScript.*;

/**
 * 名称：实现Redission下的Shiro Session DAO<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@Slf4j
public class RedissonSessionDAO extends SessionInMemoryDAO {

    private static final String SESSION_INFO_KEY_PREFIX = "session:info:";
    private static final String SESSION_ATTR_KEY_PREFIX = "session:attr:";

    private final RedissonClient redisson;
    private Codec codec = new JsonJacksonCodec();

    private static RedissonScript script = null;

    public RedissonSessionDAO(SequenceGenerator sequenceGenerator, RedissonClient redisson) {
        super(sequenceGenerator);
        this.redisson = redisson;
        if (script == null) {
            script = (RedissonScript) redisson.getScript(codec);
        }
    }

    /**
     * 为Session分配ID，并缓存
     *
     * @param session 通过SessionFactory创建的Session
     * @return 分配的ID
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        LoggerUtils.debug(log, "Generate session id and assign to the new session: id={}", sessionId);
        // 缓存Session到Redis中，不通过父类的通用方法
        this.saveSessionToRedis((RedissonSession) session);
        return sessionId;
    }

    /**
     * 实现自己的SessionId生成规则
     *
     * @param session Session
     * @return 生成Snowflake id，类型是String
     */
    @Override
    protected Serializable generateSessionId(Session session) {
        return super.generateSessionId(session).toString();
    }

    /**
     * 优先从内存中读取，内存中没有则从redis获取
     * <p>
     * 从redis中获取后，再缓存到本地一份，本地仅保留极短的时间。
     * <p>
     * 这样做的原因是shiro在一个交互期内会频繁的读取session属性，
     * 为了避免没有必要的网络开销，通过极短时间的本地缓存来拦截对redis的频繁访问。
     *
     * @return 参数为null则返回null
     * @throws UnknownSessionException 参数为null
     * @throws ExpiredSessionException session已过期
     * @throws StoppedSessionException session已停用
     * @throws InvalidSessionException session已不可用
     * @throws RedisException          发送了redis异常
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = super.doReadSession(sessionId);

        if (null != session) {
            LoggerUtils.debug(log, "Read session from thread local, id is {}, session type is {}.", sessionId, session.getClass().getSimpleName());
            return session;
        }

        // 从Redis获取session
        try {
            String key = getSessionInfoKey(sessionId);
            List<Object> keys = new ArrayList<>(1);
            keys.add(key);

            List<Object> list = script.eval(
                    key, RScript.Mode.READ_ONLY, READ_INFO_SCRIPT, RScript.ReturnType.MULTI, keys);
            // 创建session对象，并缓存到内存中，避免频繁调用redis
            session = new RedissonSession(
                    redisson, codec, sessionId,
                    (Date) list.get(0), (Date) list.get(1), (Long) list.get(2), (String) list.get(3), (Date) list.get(4));
            if (this.isSessionInMemoryEnabled()) {
                this.setSessionToThreadLocal(session);
            }
            LoggerUtils.debug(log, "Read session from redis: key={}, type={}", key, session.getClass().getSimpleName());
        } catch (RedisException e) {
            // session过期抛出异常
            convertException(e);
        }

        return session;
    }

    /**
     * SessionManager的onChange方法会触发更新保存
     * <p>
     * 因为RedissonSession内部封装了刷新session的方法，所以这里不做任何操作！<p>
     * 改变session的操作有：<p>
     * 刷新redis key的有效期<p>
     * 刷新session当前过期时间<p>
     * 增删改session的attribute
     *
     * @param session Session
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        super.update(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null || "".equals(session.getId())) {
            return;
        }
        Serializable sessionId = session.getId();
        String infoKey = getSessionInfoKey(sessionId);
        String attrKey = getSessionAttrKey(sessionId);
        List<Object> keys = new ArrayList<>(2);
        keys.add(infoKey);
        keys.add(attrKey);

        script.eval(infoKey, RScript.Mode.READ_WRITE, RedissonSessionScript.DELETE_SCRIPT, RScript.ReturnType.VALUE, keys);
        LoggerUtils.debug(log, "Delete redis session, id is {}", session.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Session> getActiveSessions() {
        // 因为性能的考虑，不支持此方法的实现。
        return Collections.EMPTY_LIST;
    }

    /**
     * 同update方法，redisson不需要保存方法
     *
     * @param session Session
     */
    @Override
    protected void saveSession(ShiroSession session) {
        // do nothing.
    }

    /**
     * 保存新的session
     *
     * @param session Session
     */
    private void saveSessionToRedis(RedissonSession session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            // 不是验证的用户，也不是正在验证的用户（正在登录）则忽略保存Session
            if (session.isAnon()) {
                LoggerUtils.debug(log, "Anonymous session does not need to be saved, id={}.", session.getId());
                return;
            }

            // 如果保存过redis则跳过，不需要重复保存
            // 因为session的超时时间、过期时间、session属性等都单独更新
            if (session.isSavedToRedis()) {
                return;
            }

            String key = getSessionInfoKey(session.getId());
            // session超时时间（毫秒）
            final long timeout = session.getTimeout() > 0 ? session.getTimeout() :
                    AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
            // 创建session的时间
            Date startTimeStamp = null == session.getStartTimestamp() ? new Date() : session.getStartTimestamp();
            String host = null == session.getHost() ? "" : session.getHost();
            // 当前session到期的时间
            Date expireTimestamp = new Date(startTimeStamp.getTime() + timeout);
            // 执行脚本需要的key
            List<Object> keys = new ArrayList<>(1);
            keys.add(key);
            // 执行脚本创建session到redis中
            // 参数顺序见脚本注释
            script.eval(key, RScript.Mode.READ_WRITE, INIT_SCRIPT, RScript.ReturnType.VALUE, keys,
                    session.getId(), timeout, startTimeStamp, host, expireTimestamp);
            // 保存到内存中
            setSessionToThreadLocal(new RedissonSession(redisson, codec, session.getId(),
                    startTimeStamp, startTimeStamp, timeout, host, expireTimestamp));

            // 标记为已经保存
            session.setSavedToRedis(true);

            LoggerUtils.debug(log, "Save session to redis: id={}, key={}, timeout={}, host={}, expire time={}",
                    session.getId(), key, timeout, host, expireTimestamp);
        } else {
            throw new UnknownSessionException("session or session id is null");
        }
    }

    static String getSessionInfoKey(Serializable sessionId) {
        return SESSION_INFO_KEY_PREFIX + '{' + sessionId + '}';
    }

    static String getSessionAttrKey(Serializable sessionId) {
        return SESSION_ATTR_KEY_PREFIX + '{' + sessionId + '}';
    }

    static void convertException(RedisException e) {
        String errMsg = e.getMessage();
        if (errMsg.startsWith(RETURN_CODE_EXPIRED + '.')) {
            throw new ExpiredSessionException();
        } else if (errMsg.startsWith(RETURN_CODE_STOPPED + '.')) {
            throw new StoppedSessionException();
        } else if (errMsg.startsWith(RETURN_CODE_INVALID + '.')) {
            throw new InvalidSessionException();
        } else {
            throw e;
        }
    }

    static RedissonScript getRedissonScript() {
        return script;
    }

}
