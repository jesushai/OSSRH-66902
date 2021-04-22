package com.lemon.framework.auth.shiro;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lemon.framework.util.sequence.SequenceGenerator;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
public abstract class SessionInMemoryDAO extends AbstractSessionDAO {

    /**
     * Session id 生成器，这里采用snowflake
     */
    private final SequenceGenerator sequenceGenerator;

    /**
     * session保留在内存中的时间（秒）<br/>
     * 为避免频繁访问redis，可以在内存中操作1秒钟（默认）
     */
    private long sessionInMemoryTimeout = 1L;
    /**
     * session是否保留在内存中，（默认：true）
     */
    private boolean sessionInMemoryEnabled = true;

    protected SessionInMemoryDAO(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    /**
     * 实现自己的SessionId生成规则
     *
     * @param session Session
     * @return 生成Snowflake id，类型是Long
     */
    @Override
    protected Serializable generateSessionId(Session session) {
        if (sequenceGenerator == null) {
            String msg = "SequenceGenerator attribute has not been configured.";
            throw new IllegalStateException(msg);
        } else {
            return sequenceGenerator.nextId();
        }
    }

    public long getSessionInMemoryTimeout() {
        return sessionInMemoryTimeout;
    }

    public void setSessionInMemoryTimeout(long sessionInMemoryTimeout) {
        this.sessionInMemoryTimeout = sessionInMemoryTimeout;
    }

    public boolean isSessionInMemoryEnabled() {
        return sessionInMemoryEnabled;
    }

    public void setSessionInMemoryEnabled(boolean sessionInMemoryEnabled) {
        this.sessionInMemoryEnabled = sessionInMemoryEnabled;
    }

    private static ThreadLocal<Map<Serializable, SessionInMemory>> sessionsInThread = new TransmittableThreadLocal<>();

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            throw new UnknownSessionException("Session is null.");
        } else {
            // 分配ID并写入Session中
            Serializable sessionId = this.generateSessionId(session);
            this.assignSessionId(session, sessionId);
            this.saveSession((ShiroSession) session);
            return sessionId;
        }
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (null == sessionId) {
            throw new UnknownSessionException("Session is null.");
        } else {
            Session session = null;
            // 从本地内存获取session
            if (this.isSessionInMemoryEnabled()) {
                session = this.getSessionFromThreadLocal(sessionId);
            }
            return session;
        }
    }

    /**
     * 保存或更新Session
     *
     * @param session Session
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession((ShiroSession) session);
        if (this.isSessionInMemoryEnabled()) {
            this.setSessionToThreadLocal(session);
        }
    }

    protected abstract void saveSession(ShiroSession session);

    /**
     * 将Session保存到本地内存中
     * 同时移除过期的session
     *
     * @param session Session
     */
    protected void setSessionToThreadLocal(Session session) {
        Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
        if (null == sessionMap) {
            sessionMap = new HashMap<>();
            sessionsInThread.set(sessionMap);
        }

        this.removeExpiredSessionInMemory(sessionMap);
        sessionMap.put(session.getId(), new SessionInMemory(session, new Date()));
    }

    /**
     * 尝试从本地内存获取指定id的Session
     * <p>
     * 同时判断此Session是否过期，过期则自动移除同时返回null
     *
     * @param sessionId 指定的session id
     * @return 本地的Session，没有找到或已过期则返回null
     */
    protected Session getSessionFromThreadLocal(Serializable sessionId) {
        if (sessionsInThread.get() == null) {
            return null;
        } else {
            Map<Serializable, SessionInMemory> sessionMap = sessionsInThread.get();
            SessionInMemory sessionInMemory = sessionMap.get(sessionId);
            if (null == sessionInMemory) {
                return null;
            } else {
                long liveTime = this.getSessionInMemoryLiveTime(sessionInMemory, new Date().getTime());
                if (liveTime >= this.sessionInMemoryTimeout * 1000L) {
                    sessionMap.remove(sessionId);
                    return null;
                } else {
                    return sessionInMemory.getSession();
                }
            }
        }
    }

    private void removeExpiredSessionInMemory(Map<Serializable, SessionInMemory> sessionMap) {
        long now = new Date().getTime();
        long mt = this.sessionInMemoryTimeout * 1000L;

        Iterator<Serializable> it = sessionMap.keySet().iterator();
        while (it.hasNext()) {
            Serializable sessionId = it.next();
            SessionInMemory sessionInMemory = sessionMap.get(sessionId);
            if (null == sessionInMemory) {
                it.remove();
            } else {
                long liveTime = this.getSessionInMemoryLiveTime(sessionInMemory, now);
                if (liveTime >= mt) {
                    it.remove();
                }
            }
        }
    }

    private long getSessionInMemoryLiveTime(SessionInMemory sessionInMemory, long nowTimeInMillis) {
        return nowTimeInMillis - sessionInMemory.getCreateTime().getTime();
    }

    static class SessionInMemory {
        private final Session session;
        private final Date createTime;

        SessionInMemory(Session session, Date createTime) {
            this.session = session;
            this.createTime = createTime;
        }

        Session getSession() {
            return this.session;
        }

        Date getCreateTime() {
            return this.createTime;
        }
    }

}
