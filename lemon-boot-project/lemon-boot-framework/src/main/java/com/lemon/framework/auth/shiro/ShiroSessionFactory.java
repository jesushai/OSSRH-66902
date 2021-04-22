package com.lemon.framework.auth.shiro;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SimpleSessionFactory;

/**
 * 名称：Shiro Session工厂<br/>
 * 描述：<br/>
 * 首先Shiro会通过这个工厂生产ShiroSession
 * 随后会生成ID并更新timeout等
 * 接着会将session的各种属性保存到redis中
 *
 * @author hai-zhang
 * @since 2020/5/26
 */
@Slf4j
public class ShiroSessionFactory extends SimpleSessionFactory {

    public Session createSession(SessionContext initData) {

        ShiroSession session = null;

        if (initData != null) {
            String host = initData.getHost();

            if (host != null) {
                session = new ShiroSession(host);
            }
        }

        if (session == null) {
            session = new ShiroSession();
        }

        if (ShiroSubject.isAnonAccess()) {
            session.setAttribute(ShiroSession.IS_ANON, true);
            LoggerUtils.debug(log, "Factory production a anonymous session.");
        } else {
            LoggerUtils.debug(log, "Factory production a authenticated session.");
        }

        return session;
    }

}
