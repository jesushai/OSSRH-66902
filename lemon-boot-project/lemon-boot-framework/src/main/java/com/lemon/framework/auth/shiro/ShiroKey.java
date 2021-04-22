package com.lemon.framework.auth.shiro;

public interface ShiroKey {

    /**
     * rememberMe cookie 加密的密钥
     *
     * @return 加密的密钥
     */
    String rememberMeEncryptKey();

    /**
     * @return Token key
     */
    String loginTokenKey();

    String referencedSessionIdSource();
}
