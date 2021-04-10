package com.lemon.boot.autoconfigure.security;

import com.lemon.framework.auth.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@Configuration
public class DefaultAuthorizationSupportAutoConfiguration {

    @Bean("userService")
    @ConditionalOnMissingBean
    public UserService sysUserService() {
        return new UserServiceDefaultImpl();
    }

    @Bean("roleService")
    @ConditionalOnMissingBean
    public RoleService sysRoleService() {
        return new RoleServiceDefaultImpl();
    }

    @Bean("permissionService")
    @ConditionalOnMissingBean
    public PermissionService sysPermissionService() {
        return new PermissionServiceDefaultImpl();
    }

    @Bean("tenantService")
    @ConditionalOnMissingBean
    public TenantService tenantService() {
        return new TenantServiceDefaultImpl();
    }

}
