package com.lemon.framework.auth;

import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public interface TenantService {

    /**
     * 获取所有可用的系统租户
     */
    List<Long> getAllTenantId();

    /**
     * 获取租户拥有的客户端模块列表
     */
    String[] getModules(Long tenantId);

    /**
     * 获取租户的名称
     */
    String getTenantName(Long tenantId);
}
