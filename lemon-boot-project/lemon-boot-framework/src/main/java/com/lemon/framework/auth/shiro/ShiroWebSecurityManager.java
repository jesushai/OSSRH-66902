package com.lemon.framework.auth.shiro;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

/**
 * 名称：扩展shiro的安全管理器<p>
 * 描述：<p>
 * 需要配合ShiroSubjectFactory使用<p>
 * 如果是登录进来产生的subject，需要做个扩展标记，
 * 后面根据它的标记来决定是否缓存Session。
 *
 * @author hai-zhang
 * @since 2020/5/25
 */
@Slf4j
public class ShiroWebSecurityManager extends DefaultWebSecurityManager {

    public ShiroWebSecurityManager(ShiroWebSessionManager webSessionManager, TenantUserAuthorizingRealm realm) {
        super();
        // 使用自定义的Subject
        this.setSubjectFactory(new ShiroSubjectFactory());
        this.setSessionManager(webSessionManager);
        this.setRealm(realm);
        // 多租户Realm选择验证器
//        this.setAuthenticator(new MultiTenantModularRealmAuthenticator());
        // Realms
//        List<Realm> realms = realm();
//        if (realms.size() == 1) {
//            this.setRealm(realms.get(0));
//        } else {
//            this.setRealms(realms);
//        }
    }

    @Override
    public Subject login(Subject subject, AuthenticationToken token) throws AuthenticationException {
        if (subject instanceof ShiroSubject) {
            // 正在执行身份验证
            LoggerUtils.debug(log, "Do login: principal=[{}], session=[{}], authenticated=[{}], forAuthenticate=true.",
                    subject.getPrincipal(),
                    null == subject.getSession() ? null : subject.getSession().getId(),
                    subject.isAuthenticated());
            ((ShiroSubject) subject).setForAuthenticate(true);
            // 改变Session的匿名状态
            subject.getSession().setAttribute(ShiroSession.IS_ANON, false);
        }
        return super.login(subject, token);
    }

}
