package shiro.db.service.impl;

import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.model.PermissionTreeNode;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysPermission;
import shiro.db.service.ISysPermissionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Service("permissionService")
public class SysPermissionServiceImpl implements ISysPermissionService, PermissionService {

    @Override
    public Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
        Set<String> result = new HashSet<>();
        findPermissionsByRoleIds(roleIds, tenantId).stream()
                .map(SysPermission::getPermission)
                .forEach(result::add);
        return result;
    }

    @Override
    public Set<Permission> getAllPermission(Long tenantId) {
        return new HashSet<Permission>(2) {{
           add(new Permission().setId(new String[]{"admin:permission:tree"}));
           add(new Permission().setId(new String[]{"admin:permission:list"}));
        }};
    }

    @Override
    public List<PermissionTreeNode> getPermissionTree(String basicPackage, Long tenantId) {
        return PermissionService.super.getPermissionTree(basicPackage, tenantId);
//        HashOperations
    }

    public List<SysPermission> findPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
        return new ArrayList<SysPermission>(2) {{
           add(new SysPermission().setId(1L).setTenant(0L).setRoleId(1L).setPermission("admin:permission:tree").setDeleted(false));
           add(new SysPermission().setId(1L).setTenant(0L).setRoleId(1L).setPermission("admin:permission:list").setDeleted(false));
        }};
    }
}
