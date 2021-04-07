package com.lemon.framework.auth.shiro;

public interface ShiroKey {

    /**
     * rememberMe cookie 加密的密钥
     */
    String rememberMeEncryptKey();

    /**
     * Token key
     */
    String loginTokenKey();

    String referencedSessionIdSource();
}
