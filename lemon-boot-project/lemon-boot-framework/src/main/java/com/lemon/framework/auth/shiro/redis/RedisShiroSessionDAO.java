package com.lemon.framework.auth.shiro.redis;

import com.lemon.framework.auth.shiro.SessionInMemoryDAO;
import com.lemon.framework.auth.shiro.ShiroSession;
import com.lemon.framework.cache.redis.serializer.ObjectSerializer;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * 名称：Redis Shiro Session DAO<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/22
 */
@Slf4j
public class RedisShiroSessionDAO extends SessionInMemoryDAO {

    /**
     * session key前缀
     */
    private String keyPrefix = "shiro-session:";
    /**
     * session在redis中的过期时间：-2即session过期时间（默认）；-1永不过期；>0过期（单位秒）
     */
    private int expire = -2;

    private final RedisShiroManager redisManager;

    private final ObjectSerializer objectSerializer = new ObjectSerializer();

    public RedisShiroSessionDAO(RedisShiroManager redisManager, SequenceGenerator sequenceGenerator) {
        super(sequenceGenerator);
        this.redisManager = redisManager;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = super.doReadSession(sessionId);

        if (null != session) {
            return session;
        }

        // 缓存没有则从Redis获取session
        try {
            session = (Session) objectSerializer.deserialize(
                    (byte[]) this.redisManager.get(this.getRedisSessionKey(sessionId)));
            if (this.isSessionInMemoryEnabled() && session != null) {
                // 再存本地一份
                this.setSessionToThreadLocal(session);
            }
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new UnknownSessionException(e);
        }

        return session;
    }

    /**
     * 移除redis session缓存
     *
     * @param session 要删除的Session
     */
    @Override
    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            try {
                this.redisManager.del(this.getRedisSessionKey(session.getId()));
            } catch (Exception e) {
                LoggerUtils.error(log, "Delete session error: session id={}, exception={}", session.getId(), e.getMessage());
            }
        } else {
            LoggerUtils.error(log, "Delete session error: Session or session id is null");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Session> getActiveSessions() {
        // 因为性能的考虑，不支持此方法的实现。
        return Collections.EMPTY_LIST;
    }

    /**
     * 根据规则生成保存到Redis中的Session Key
     *
     * @param sessionId Session id
     * @return 缓存的Key
     */
    private String getRedisSessionKey(Serializable sessionId) {
        return this.keyPrefix + sessionId;
    }

    @Override
    protected void saveSession(ShiroSession session) throws UnknownSessionException {

        if (session != null && session.getId() != null) {
            // 不是验证的用户，也不是正在验证的用户（正在登录）则忽略保存Session
            if ((Boolean) session.getAttribute(ShiroSession.IS_ANON)) {
                return;
            }

            String key = this.getRedisSessionKey(session.getId());
            LoggerUtils.debug(log, "Refresh session expire, sessionId={}", session.getId());

            // 暂时只支持-2，即与session时效同步！
            if (this.expire == -2) {
                // redis缓存时限即session的有效时限
                this.redisManager.set(key, objectSerializer.serialize(session), (int) (session.getTimeout() / 1000L));
            } else {
                // redis缓存时限大于0小于session的时限这里会有警告
                // redis缓存时限等于0永久保存
                if (this.expire != -1 && (long) (this.expire * 1000) < session.getTimeout()) {
                    LoggerUtils.warn(log,
                            "Redis session expire time: "
                                    + this.expire * 1000 + " is less than Session timeout: "
                                    + session.getTimeout() + " . It may cause some problems.");
                }
                this.redisManager.set(key, objectSerializer.serialize(session), this.expire);
            }
        } else {
            throw new UnknownSessionException("session or session id is null");
        }
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

}
