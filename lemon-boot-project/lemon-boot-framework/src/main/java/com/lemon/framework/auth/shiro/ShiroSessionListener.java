package com.lemon.framework.auth.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 名称：Session监听<p>
 * 描述：<p>
 * 统计监听Session数量
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
public class ShiroSessionListener implements SessionListener {

    private final AtomicLong sessionCount = new AtomicLong(0L);

    public AtomicLong getSessionCount() {
        return sessionCount;
    }

    @Override
    public void onStart(Session session) {
        if (isAuthorized(session)) {
            sessionCount.incrementAndGet();
        }
    }

    @Override
    public void onStop(Session session) {
        if (isAuthorized(session)) {
            sessionCount.decrementAndGet();
        }
    }

    @Override
    public void onExpiration(Session session) {
        if (isAuthorized(session)) {
            sessionCount.decrementAndGet();
        }
    }

    private boolean isAuthorized(Session session) {
        Boolean b = (Boolean) session.getAttribute(ShiroSession.IS_ANON);
        return b == null || !b;
    }
}
