package com.lemon.framework.auth.shiro;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.Subject;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.auth.model.support.SessionSupport;
import com.lemon.framework.auth.model.support.SubjectSupport;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.springframework.stereotype.Component;

/**
 * 名称：Shiro身份服务<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/15
 */
@Component(BeanNameConstants.AUTHENTICATION_SERVICE)
public class ShiroAuthenticationServiceImpl implements AuthenticationService {

    @Override
    public Subject doGetSubject() {
        org.apache.shiro.subject.Subject shiroSubject;
        try {
            shiroSubject = SecurityUtils.getSubject();
        } catch (UnavailableSecurityManagerException e) {
            return null;
        }
        SubjectSupport mySubject = new SubjectSupport();

        org.apache.shiro.session.Session shiroSession = shiroSubject.getSession();

        if (null != shiroSession && null != shiroSession.getId()) {
            SessionSupport mySession = new SessionSupport();
            mySession.setId(shiroSession.getId().toString());

            if (StringUtils.isEmpty(shiroSession.getHost())) {
                mySession.setHost(IpUtils.getRequestHost());
            } else {
                mySession.setHost(shiroSession.getHost());
            }

            mySubject.setSession(mySession);
        }

        mySubject.setPrincipal((User) shiroSubject.getPrincipal());
        return mySubject;
    }

}
