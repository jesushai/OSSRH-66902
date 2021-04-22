package com.lemon.framework.auth.shiro;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@SuppressWarnings("unused")
public class TenantAuthorizationInfo extends SimpleAuthorizationInfo {

    /**
     * Tenant id
     */
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
