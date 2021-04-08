package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.auth.shiro.SessionInMemoryDAO;
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
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@Slf4j
public class RedissonSessionDAO extends SessionInMemoryDAO {

    private static final String SESSION_INFO_KEY_PREFIX = "session:info:";
    private static final String SESSION_ATTR_KEY_PREFIX = "session:attr:";

    /**
     * Session id 生成器，这里采用snowflake
     */
    private final SequenceGenerator sequenceGenerator;

    private final RedissonClient redisson;
    private Codec codec = new JsonJacksonCodec();

    // TODO: 需要测试RedissonScript是否线程安全
    private static RedissonScript script = null;

    public RedissonSessionDAO(SequenceGenerator sequenceGenerator, RedissonClient redisson) {
        this.sequenceGenerator = sequenceGenerator;
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
        if (session == null) {
            throw new UnknownSessionException("Session is null.");
        } else {
            // 分配ID并写入Session中
            Serializable sessionId = this.generateSessionId(session);
            this.assignSessionId(session, sessionId);
            LoggerUtils.debug(log, "Generate session id and assign to the new session: id={}", sessionId);
            // 缓存Session
            this.saveSession((RedissonSession) session);
            return sessionId;
        }
    }

    /**
     * 实现自己的SessionId生成规则
     */
    @Override
    protected Serializable generateSessionId(Session session) {
        if (sequenceGenerator == null) {
            throw new IllegalStateException("SequenceGenerator attribute has not been configured.");
        } else {
            return "" + sequenceGenerator.nextId();
        }
    }

    /**
     * 读取Session
     * 如果内存中已经缓存从内存中读取，否则从redis获取
     *
     * @return 参数为null则返回null
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (null == sessionId) {
            LoggerUtils.warn(log, "Session id is null.");
            return null;
        } else {
            Session session = null;

            // 先从本地内存获取session
            if (this.isSessionInMemoryEnabled()) {
                session = this.getSessionFromThreadLocal(sessionId);
                if (null != session) {
                    LoggerUtils.debug(log, "Read session from thread local, id is {}, session type is {}.", sessionId, session.getClass().getSimpleName());
                    return session;
                }
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
    }

    /**
     * SessionManager的onChange方法会触发更新保存<p/>
     * <p>
     * 因为RedissonSession内部封装了刷新session的方法，所以这里不做任何操作！<br/>
     * 改变session的操作有：
     * <li>刷新redis key的有效期</li>
     * <li>刷新session当前过期时间</li>
     * <li>增删改session的attribute</li>
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        RedissonSession redissonSession = (RedissonSession) session;
        saveSession(redissonSession);
        // 临时缓存到内存中，如果允许的话
        if (this.isSessionInMemoryEnabled()) {
            this.setSessionToThreadLocal(session);
        }
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
     * 保存新的session
     */
    private void saveSession(RedissonSession session) throws UnknownSessionException {
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
