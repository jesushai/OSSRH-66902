package com.lemon.framework.auth.shiro;

import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.crypto.password.PasswordEncoderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * <b>名称：多租户用户认证与授权realm</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class TenantUserAuthorizingRealm extends AuthorizingRealm {

    private UserService userService;
    private RoleService roleService;
    private PermissionService permissionService;

    public TenantUserAuthorizingRealm(UserService userService, RoleService roleService, PermissionService permissionService) {
        super();
        this.userService = userService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        if (!(token instanceof TenantUsernamePasswordToken)) {
            return false;
        }
        return super.supports(token);
    }

    /**
     * realm的名字定制化<br/>
     * {@code TenantUserAuthorizingRealm}
     */
    @Override
    public void setName(String name) {
        super.setName("TenantUserAuthorizingRealm");
    }

    /**
     * cacheName定制化<br/>
     * {@code TenantUserAuthorizingRealm.authorizationCache}
     */
    @Override
    public String getAuthorizationCacheName() {
        return "AuthorizationRealm";
    }

    /**
     * cacheName定制化<br/>
     * {@code TenantUserAuthorizingRealm.authenticationCache}
     */
    @Override
    public String getAuthenticationCacheName() {
        return "AuthorizingRealm";
    }

    /**
     * 定制自己的CacheKey生成规则
     */
    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        return getCacheKey(principals);
    }

    /**
     * 定制自己的CacheKey生成规则
     */
    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        return getCacheKey(principals);
    }

    private String getCacheKey(PrincipalCollection principals) {
        // 获取当前Realm中的主Principal
        User user = (User) getAvailablePrincipal(principals);
        return user.getId().toString();
    }

    /**
     * 附加授权信息
     *
     * @param principals 已经认证的用户
     * @return 添加了授权信息的结果
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        User user = (User) getAvailablePrincipal(principals);
        LoggerUtils.debug(log, "Get authorization info: t({}) {}, roles=[{}]",
                user.getTenant(), user.getUsername(), user.getRoleIds());
        Long[] roleIds = user.getRoleIds();

        // 角色与菜单权限
        Set<String> roles = roleService.getNamesByIds(roleIds);
        Set<String> permissions = permissionService.getPermissionsByRoleIds(roleIds, user.getTenant());

        // 缓存租户ID及授权信息
        TenantAuthorizationInfo info = new TenantAuthorizationInfo();
        info.setRoles(roles);
        // 暂时不需要放入完整的permission对象，仅缓存permission名称即可
        info.setStringPermissions(permissions);
//        info.setObjectPermissions();
        info.setTenantId(user.getTenant());
        return info;
    }

    /**
     * 执行认证逻辑
     *
     * @param token 认证token
     * @return 认证的用户
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        TenantUsernamePasswordToken upToken = (TenantUsernamePasswordToken) token;
        String username = upToken.getUsername();
        String password = new String(upToken.getPassword());

        if (!StringUtils.hasLength(username)) {
            throw new AccountException("Username cannot be empty.");
        }
        if (!StringUtils.hasLength(password)) {
            throw new AccountException("Password cannot be empty.");
        }

        List<User> users = userService.getUserByIdentificationAndTenant(username, upToken.getTenantId());
        if (users.size() == 0) {
            throw new UnknownAccountException("User [" + username + "] not found in tenant[" + upToken.getTenantId() + "].");
        } else if (users.size() > 1) {
            throw new AccountException("Two accounts exist for the same user name [" + username + "] in tenant[" + upToken.getTenantId() + "].");
        }

        User user = users.get(0);
        if (!PasswordEncoderUtil.matches(password, user.getPassword())) {
            throw new UnknownAccountException("Account [" + username + "] not found.");
        }
        if (!user.isValid()) {
            throw new LockedAccountException("Account [" + username + "] not available.");
        }

        // TODO: 可以加入部门等权限关联信息到user中，并自动缓存。

        LoggerUtils.debug(log, "Authentication success: t({}) {}",
                user.getTenant(), user.getUsername());

        return new SimpleAuthenticationInfo(user, password, getName());
    }

    /**
     * 清除当前用户权限缓存
     * 使用方法：在需要清除用户权限的地方注入 TenantUserAuthorizingRealm,
     * 然后调用其 clearCache方法。
     */
    public void clearCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        LoggerUtils.debug(log, "Clear principal cache: {}", principals);
        super.clearCache(principals);
    }
}
