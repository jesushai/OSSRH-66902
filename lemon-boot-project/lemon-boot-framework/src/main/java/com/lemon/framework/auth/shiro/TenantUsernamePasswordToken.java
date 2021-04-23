package com.lemon.framework.auth.shiro;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 名称：多租户用户密码Token<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
@Data
public class TenantUsernamePasswordToken extends UsernamePasswordToken {

    private long tenantId;

    public TenantUsernamePasswordToken(String username, char[] password, long tenantId) {
        super(username, password);
        this.tenantId = tenantId;
    }

    public TenantUsernamePasswordToken(String username, String password, long tenantId) {
        super(username, password);
        this.tenantId = tenantId;
    }

    public TenantUsernamePasswordToken(String username, String password, boolean rememberMe, long tenantId) {
        super(username, password, rememberMe);
        this.tenantId = tenantId;
    }

    public TenantUsernamePasswordToken(String username, char[] password, boolean rememberMe, long tenantId) {
        super(username, password, rememberMe);
        this.tenantId = tenantId;
    }
}
