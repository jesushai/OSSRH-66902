package com.lemon.framework.auth;

import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.model.PermissionTreeNode;

import java.util.List;
import java.util.Set;

/**
 * 名称：默认的权限许可服务<br/>
 * 描述：<br/>
 * 请覆盖它
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class PermissionServiceDefaultImpl implements PermissionService {

    @Override
    public Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
        throw new RuntimeException("Unimplemented PermissionService.");
    }

    @Override
    public Set<Permission> getAllPermission(Long tenantId) {
        throw new RuntimeException("Unimplemented PermissionService.");
    }

    @Override
    public List<PermissionTreeNode> getPermissionTree(List<Permission> permissions) {
        throw new RuntimeException("Unimplemented PermissionService.");
    }

    @Override
    public List<PermissionTreeNode> getPermissionTree(String basicPackage, Long tenantId) {
        throw new RuntimeException("Unimplemented PermissionService.");
    }

    @Override
    public List<Permission> getPermissions(String basicPackage, Long tenantId) {
        throw new RuntimeException("Unimplemented PermissionService.");
    }
}
