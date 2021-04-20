package shiro.db.controller;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.model.PermissionTreeNode;
import com.lemon.framework.core.annotation.PermissionDescription;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限表 前端控制器
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@RestController
@RequestMapping("/permission")
public class SysPermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequiresPermissions("admin:permission:tree")
    @PermissionDescription(menu = {"SYS-ADMIN", "SYS-ADMIN-PERMISSION"}, action = "SELECT-ALL")
    @GetMapping("/tree")
    public List<PermissionTreeNode> getAllPermission() {
        Long tenantId = authenticationService.getPrincipal().getTenant();
        return permissionService.getPermissionTree("shiro", tenantId);
    }

    @RequiresPermissions("admin:permission:list")
    @PermissionDescription(menu = {"SYS-ADMIN", "SYS-ADMIN-PERMISSION"}, action = "SELECT-ALL")
    @GetMapping("/list")
    public Set<Permission> getAllPermissionCode() {
        Long tenantId = authenticationService.getPrincipal().getTenant();
        return permissionService.getAllPermission(tenantId);
    }
}
