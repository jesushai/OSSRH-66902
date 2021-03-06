package com.lemon.framework.auth.shiro;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Slf4j
public class ShiroWebSessionManager extends DefaultWebSessionManager {

    private final String loginTokenKey;
    private final String referencedSessionIdSource;

    public static final String DEFAULT_SESSION_REFRESH_POLICY = "Near";
    private final String sessionRefreshPolicy;

    public static final int DEFAULT_SESSION_REFRESH_NEAR = 600;
    private final long sessionRefreshNear;

    public static final int DEFAULT_SESSION_MAX_LIVE_TIME = 86400;
    private final long sessionMaxLiveTime;

    private final long sessionTimeout;

    public ShiroWebSessionManager(String loginTokenKey,
                                  String referencedSessionIdSource,
                                  int sessionTimeout,
                                  SessionDAO sessionDAO,
                                  SessionFactory sessionFactory,
                                  Collection<SessionListener> listeners,
                                  String sessionRefreshPolicy,
                                  int sessionRefreshNear,
                                  int sessionMaxLiveTime) {
        // Session token key
        this.loginTokenKey = loginTokenKey;
        this.referencedSessionIdSource = referencedSessionIdSource;
        this.sessionRefreshNear = sessionRefreshNear;
        this.sessionRefreshPolicy = sessionRefreshPolicy;
        this.sessionMaxLiveTime = sessionMaxLiveTime;
        // Session???????????????????????????
        this.sessionTimeout = sessionTimeout;
        this.setGlobalSessionTimeout(sessionTimeout);
        // ??????Session DAO
        if (sessionDAO != null) {
            this.setSessionDAO(sessionDAO);
        }
        // ??????Session?????????
        if (CollectionUtils.isNotEmpty(listeners)) {
            this.setSessionListeners(listeners);
        }
        // shiro??????URL??????
        this.setSessionIdUrlRewritingEnabled(false);
        // ?????????????????????cookie
        this.setSessionIdCookieEnabled(false);
        // ?????????Session??????
        this.setSessionFactory(sessionFactory);

        LoggerUtils.debug(log, "Register shiro web session manager:");
        LoggerUtils.debug(log, "  |-- Login Token Key is [{}], Referenced Session Id Source is [{}]", loginTokenKey, referencedSessionIdSource);
        LoggerUtils.debug(log, "  |-- Refresh session policy is [{}], near in [{}]ms", sessionRefreshPolicy, sessionRefreshNear);
        LoggerUtils.debug(log, "  |-- Session timeout is [{}]ms, max-live-time is [{}]ms", sessionMaxLiveTime, sessionMaxLiveTime);
        LoggerUtils.debug(log, "  |-- SessionDAO is [{}], SessionFactory is [{}]",
                null == sessionDAO ? "null" : sessionDAO.getClass().getSimpleName(),
                sessionFactory.getClass().getSimpleName());
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(loginTokenKey);
        if (StringUtils.isNotEmpty(id)) {
            LoggerUtils.debug(log, "Read session id from web request header: header.{}={}", loginTokenKey, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, referencedSessionIdSource);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            LoggerUtils.debug(log, "Call super getSessionId().");
            return super.getSessionId(request, response);
        }
    }

    @Override
    protected Session createSession(SessionContext context) throws AuthorizationException {
        LoggerUtils.debug(log, "Create session by the session factory ->");
        ShiroSession session = (ShiroSession) super.createSession(context);
        Date expireTimestamp = new Date(session.getStartTimestamp().getTime() + sessionTimeout);
        session.setExpireTimestamp(expireTimestamp);
        return session;
    }

    /**
     * ??????????????????????????????session?????????<p>
     * (1) ??????SessionManager???touch???????????????????????????onChange?????????????????????session???touch
     *
     * @param key Session key
     */
    @Override
    public void touch(SessionKey key) throws InvalidSessionException {
        LoggerUtils.debug(log, "Touch session, lookup session by sessionDAO.doReadSession() ->");
        ShiroSession s = (ShiroSession) this.doGetSession(key);
        Date touchTime = new Date();
        long touchTimeMillis = touchTime.getTime();

        if (DEFAULT_SESSION_REFRESH_POLICY.equals(sessionRefreshPolicy) && sessionRefreshNear > 0) {
            // ??????????????????Session???????????????????????????Redis???????????????????????????
            long liveTime = touchTimeMillis - s.getStartTimestamp().getTime();
            Date expireTimestamp = s.getExpireTimestamp();

            if (expireTimestamp.getTime() - touchTimeMillis <= sessionRefreshNear
                    && (sessionMaxLiveTime <= 0 || liveTime + s.getTimeout() <= sessionMaxLiveTime)) {

                // ??????expireTimestamp
                expireTimestamp = new Date(touchTimeMillis + s.getTimeout());
                s.setExpireTimestamp(expireTimestamp);

                LoggerUtils.debug(log, "Do session object touch, refresh session's live time and renewal expire.");
                s.touch(touchTime); // (1)
                this.onChange(s);
            }
        } else {
            // ???????????????????????????????????????
            LoggerUtils.warn(log, "Real time refresh session is not recommended!");
            s.touch(touchTime); // (1)
            this.onChange(s);
        }
    }

    /**
     * ????????????Session??????
     *
     * @param sessionKey   Session key
     * @param attributeKey Attribute key
     * @param value        New attribute value
     */
    @Override
    public void setAttribute(SessionKey sessionKey, Object attributeKey, Object value) throws InvalidSessionException {
        super.setAttribute(sessionKey, attributeKey, value);
    }

}
