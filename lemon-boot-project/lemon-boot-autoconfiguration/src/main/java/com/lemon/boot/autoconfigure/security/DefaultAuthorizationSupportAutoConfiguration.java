package com.lemon.boot.autoconfigure.security;

import com.lemon.framework.auth.*;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 名称：默认的授权服务<p>
 * 描述：<p>
 * 请覆盖！
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@Configuration
public class DefaultAuthorizationSupportAutoConfiguration {

    @Bean(BeanNameConstants.USER_SERVICE)
    @ConditionalOnMissingBean(name = BeanNameConstants.USER_SERVICE)
    public UserService userService() {
        return new UserServiceDefaultImpl();
    }

    @Bean(BeanNameConstants.ROLE_SERVICE)
    @ConditionalOnMissingBean(name = BeanNameConstants.ROLE_SERVICE)
    public RoleService roleService() {
        return new RoleServiceDefaultImpl();
    }

    @Bean(BeanNameConstants.PERMISSION_SERVICE)
    @ConditionalOnMissingBean(name = BeanNameConstants.PERMISSION_SERVICE)
    public PermissionService permissionService() {
        return new PermissionServiceDefaultImpl();
    }

    @Bean(BeanNameConstants.TENANT_SERVICE)
    @ConditionalOnMissingBean(name = BeanNameConstants.TENANT_SERVICE)
    public TenantService tenantService() {
        return new TenantServiceDefaultImpl();
    }

}
