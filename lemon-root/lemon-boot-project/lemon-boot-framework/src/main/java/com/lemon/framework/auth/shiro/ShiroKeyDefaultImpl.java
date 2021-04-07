package com.lemon.framework.auth.shiro;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class ShiroKeyDefaultImpl implements ShiroKey {

    @Override
    public String rememberMeEncryptKey() {
        return "default_remember_me";
    }

    @Override
    public String loginTokenKey() {
        return "Authorization";
    }

    @Override
    public String referencedSessionIdSource() {
        return "header";
    }
}
