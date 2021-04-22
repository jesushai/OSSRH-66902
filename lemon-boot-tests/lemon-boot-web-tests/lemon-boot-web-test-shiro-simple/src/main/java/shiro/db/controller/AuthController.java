package shiro.db.controller;

import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.shiro.TenantUserAuthorizingRealm;
import com.lemon.framework.auth.shiro.TenantUsernamePasswordToken;
import com.lemon.framework.core.annotation.ApiLimit;
import com.lemon.framework.core.enums.LimitType;
import com.lemon.framework.exception.AuthorizationException;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.IpUtils;
import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.TimestampUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import shiro.db.entity.SysAdmin;
import shiro.db.service.ISysAdminService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Resource(name = "userService")
    private ISysAdminService adminService;

    @Resource(name = "roleService")
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private TenantUserAuthorizingRealm tenantUserAuthorizingRealm;

    /*
     *  { username : value, password : value, tenantId: value }
     */
    @ApiLimit(key = "login", period = 60, count = 2, name = "登录请求", prefix = "limit", limitType = LimitType.CUSTOMER)
    @PostMapping("/login")
    public Object login(@RequestBody String body, HttpServletRequest request) {
        String username = JacksonUtils.parseString(body, "username");
        String password = JacksonUtils.parseString(body, "password");
        Long tenantId = JacksonUtils.parseLong(body, "tenantId");
        if (tenantId == null) {
            tenantId = 0L;
        }

        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            throw new com.lemon.framework.exception.AuthenticationException();
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new TenantUsernamePasswordToken(username, password, tenantId));
        } catch (AuthenticationException e) {
//            throw new com.lemon.framework.exception.AuthenticationException();
            throw e;
        }

        currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();
        admin.setLastIp(IpUtils.getRequestHost(request));
        admin.setLastLogin(TimestampUtils.now());

        LoggerUtils.info(log, "用户{}登录，最后登录IP={}，时间={}", admin.getUsername(), admin.getLastIp(), admin.getLastLogin());

        // userInfo
        Map<String, Object> adminInfo = new HashMap<>();
        adminInfo.put("nickName", admin.getUsername());
        adminInfo.put("avatar", admin.getAvatar());

        Map<Object, Object> result = new HashMap<>();
        result.put("token", currentUser.getSession().getId());
        result.put("adminInfo", adminInfo);
        return result;
    }

    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();
        LoggerUtils.info(log, "退出");
        // 清除Principal缓存
        tenantUserAuthorizingRealm.clearCache();
        // 登录Subject
        currentUser.logout();
        return Result.ok();
    }

    @RequiresAuthentication
    @GetMapping("/info")
    @ApiLimit(key = "info", period = 10, count = 2, name = "登录信息请求", prefix = "limit", limitType = LimitType.PRINCIPAL_KEY)
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        Long[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.getNamesByIds(roleIds);
        Set<String> permissions = permissionService.getPermissionsByRoleIds(roleIds, admin.getTenant());
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toApi(permissions, admin.getTenant()));
        return data;
    }

    @GetMapping("/index")
    public Object pageIndex() {
        return Result.ok();
    }

    @GetMapping("/401")
    public Object page401() {
        return Result.error(new com.lemon.framework.exception.AuthenticationException());
    }

    @GetMapping("/403")
    public Object page403() {
        return Result.error(new AuthorizationException());
    }

    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toApi(Set<String> permissions, Long tenantId) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "shiro";
            List<Permission> systemPermissions = permissionService.getPermissions(basicPackage, tenantId);
            for (Permission permission : systemPermissions) {
                String perm = permission.getId()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }

        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            if (api != null) {
                apis.add(api);
            }

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
                //                return systemPermissionsMap.values();
            }
        }
        return apis;
    }

}
