package com.lemon.framework.auth.shiro;

/**
 * 名称：默认的SkiroKey<br/>
 * 描述：<br/>
 * 建议实现你自己的ShiroKey实现类
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
