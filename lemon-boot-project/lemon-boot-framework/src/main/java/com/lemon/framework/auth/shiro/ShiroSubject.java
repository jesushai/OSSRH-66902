package com.lemon.framework.auth.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/25
 */
public class ShiroSubject extends WebDelegatingSubject {

    /**
     * 为了鉴权
     */
    private transient boolean forAuthenticate;

    public boolean isForAuthenticate() {
        return forAuthenticate;
    }

    public void setForAuthenticate(boolean forAuthenticate) {
        this.forAuthenticate = forAuthenticate;
    }

    public ShiroSubject(PrincipalCollection principals, boolean authenticated, String host, Session session, ServletRequest request, ServletResponse response, SecurityManager securityManager) {
        super(principals, authenticated, host, session, request, response, securityManager);
    }

    public ShiroSubject(PrincipalCollection principals, boolean authenticated, String host, Session session, boolean sessionEnabled, ServletRequest request, ServletResponse response, SecurityManager securityManager) {
        super(principals, authenticated, host, session, sessionEnabled, request, response, securityManager);
    }

    /**
     * @return true - 不是验证的用户，也不是正在验证的用户（正在登录）
     */
    public static boolean isAnonAccess() {
        Subject subject = SecurityUtils.getSubject();
        return !subject.isAuthenticated()
                && subject instanceof ShiroSubject
                && !((ShiroSubject) subject).isForAuthenticate();
    }
}
