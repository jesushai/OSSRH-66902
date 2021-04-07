package com.lemon.framework.auth.shiro;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.WebSubjectContext;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/25
 */
@Slf4j
public class ShiroSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {
        boolean isNotBasedOnWebSubject = context.getSubject() != null && !(context.getSubject() instanceof WebSubject);
        if (context instanceof WebSubjectContext && !isNotBasedOnWebSubject) {
            WebSubjectContext webSubjectContext = (WebSubjectContext) context;
            SecurityManager securityManager = webSubjectContext.resolveSecurityManager();
            Session session = webSubjectContext.resolveSession();
            boolean sessionEnabled = webSubjectContext.isSessionCreationEnabled();
            PrincipalCollection principals = webSubjectContext.resolvePrincipals();
            boolean authenticated = webSubjectContext.resolveAuthenticated();
            String host = webSubjectContext.resolveHost();
            ServletRequest request = webSubjectContext.resolveServletRequest();
            ServletResponse response = webSubjectContext.resolveServletResponse();

            if (log.isDebugEnabled()) {
                LoggerUtils.debug(log, "Create a new subject:");
                LoggerUtils.debug(log, "  |-- Session({}), , enabled is {}, host is {}.", null == session ? "null" : session.getId(), sessionEnabled, host);
                LoggerUtils.debug(log, "  |-- Principal is {}", null == principals ? null : principals.getPrimaryPrincipal());
                LoggerUtils.debug(log, "  |-- Authenticated is {}, not forAuthenticate.", authenticated);
            }

            return new ShiroSubject(principals, authenticated, host, session, sessionEnabled, request, response, securityManager);
        } else {
            LoggerUtils.debug(log, "Not WebSubject, call super create a subject.");
            return super.createSubject(context);

//            SecurityManager securityManager = context.resolveSecurityManager();
//            Session session = context.resolveSession();
//            boolean sessionCreationEnabled = context.isSessionCreationEnabled();
//            PrincipalCollection principals = context.resolvePrincipals();
//            boolean authenticated = context.resolveAuthenticated();
//            String host = context.resolveHost();
//            return new DelegatingSubject(principals, authenticated, host, session, sessionCreationEnabled, securityManager);
        }
    }
}
