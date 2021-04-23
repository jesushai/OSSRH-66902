package com.lemon.framework.auth;

import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public interface TenantService {

    /**
     * 获取所有可用的系统租户
     *
     * @return 所有可用的系统租户
     */
    List<Long> getAllTenantId();

    /**
     * 获取租户拥有的客户端模块列表
     *
     * @param tenantId 租户ID
     * @return 租户下的模块名
     */
    String[] getModules(Long tenantId);

    /**
     * 获取租户的名称
     *
     * @param tenantId 租户ID
     * @return 租户名
     */
    String getTenantName(Long tenantId);
}
