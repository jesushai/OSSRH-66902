package com.lemon.framework.auth.shiro;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@SuppressWarnings("unused")
public class TenantAuthorizationInfo extends SimpleAuthorizationInfo {

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
