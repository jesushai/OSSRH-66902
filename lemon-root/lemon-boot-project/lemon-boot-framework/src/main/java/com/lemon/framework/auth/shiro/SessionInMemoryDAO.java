package com.lemon.framework.auth.shiro;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
public abstract class SessionInMemoryDAO extends AbstractSessionDAO {

    /**
     * session保留在内存中的时间（秒）<br/>
     * 为避免频繁访问redis，可以在内存中操作1秒钟（默认）
     */
    private long sessionInMemoryTimeout = 1L;
    /**
     * session是否保留在内存中，（默认：true）
     */
    private boolean sessionInMemoryEnabled = true;

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

    /**
     * 将Session保存到本地内存中
     * 同时移除过期的session
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
     * 尝试从本地内存获取Session
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
