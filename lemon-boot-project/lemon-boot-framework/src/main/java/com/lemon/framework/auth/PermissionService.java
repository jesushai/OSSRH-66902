package com.lemon.framework.auth;

import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.model.PermissionTreeNode;
import com.lemon.framework.core.annotation.PermissionDescription;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 名称：系统权限服务接口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public interface PermissionService {

    /**
     * 获取角色所拥有的权限代码
     *
     * @param roleIds  角色ID列表
     * @param tenantId 租户
     * @return 指定租户与角色列表包含的许可名
     */
    Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId);

    /**
     * 获取租户下的所有权限
     *
     * @param tenantId 租户ID
     * @return 租户下的所有权限许可
     */
    Set<Permission> getAllPermission(Long tenantId);

    default Set<String> getAllPermission(String basePackage) {
        Set<String> result = new HashSet<>();
        List<Permission> permissions = getPermissions(basePackage);
        permissions.forEach(p -> Collections.addAll(result, p.getId()));
        return result;
    }

    default List<PermissionTreeNode> getPermissionTree(List<Permission> permissions) {
        List<PermissionTreeNode> root = new ArrayList<>();
        permissions.forEach(permission -> PermissionTreeNode.addTreeNode(root, permission));
        return root;
    }

    default List<PermissionTreeNode> getPermissionTree(String basicPackage, Long tenantId) {
        return getPermissionTree(getPermissions(basicPackage, tenantId));
    }

    default List<PermissionTreeNode> getPermissionTree(String basicPackage) {
        return getPermissionTree(getPermissions(basicPackage));
    }

    /**
     * 从指定的包路径下获取所有Controller带权限注解的许可内容
     *
     * @param basicPackage 仅扫描指定包下的Controller
     * @param tenantId     按租户过滤（空则不过滤）
     * @return 扫描到的租户内的所有权限
     */
    default List<Permission> getPermissions(String basicPackage, Long tenantId) {

        // 租户内已经定义好的角色内包含的所有授权（不含上级菜单）
        Set<Permission> permissionsInTenant = null;
        if (tenantId != null) {
            permissionsInTenant = getAllPermission(tenantId);
        }

        if (CollectionUtils.isEmpty(permissionsInTenant)) {
            return new ArrayList<>();
        } else {
            // 包下的系统内的所有授权
            List<Permission> permissionsDefs = getPermissions(basicPackage);
            List<String> permissionIds = new ArrayList<>();

            permissionsInTenant.stream()
                    .map(Permission::getId)
                    .forEach(p -> {
                        if (p.length == 1) {
                            permissionIds.add(p[0]);
                        } else if (p.length > 1) {
                            permissionIds.addAll(Arrays.asList(p));
                        }
                    });

            for (int i = permissionsDefs.size() - 1; i >= 0; i--) {
                Permission permissionDef = permissionsDefs.get(i);

                if (permissionDef.getId().length == 1) {
                    if (!permissionIds.contains(permissionDef.getId()[0])) {
                        permissionsDefs.remove(i);
                    }
//                if (permissionDef.getId().length == 1) {
//                    Permission permission = permissionsInTenant.stream()
//                            .filter(p -> Arrays.equals(p.getId(), permissionDef.getId()))
//                            .findFirst().orElse(null);
//                    if (null == permission) {
//                        permissionsDefs.remove(i);
//                    }
                } else {
                    // 一个注解多个授权，不推荐
                    StringBuilder buf = new StringBuilder();
                    int n = 0;
                    for (int j = 0; j < permissionDef.getId().length; j++) {
                        if (permissionIds.contains(permissionDef.getId()[j])) {
                            buf.append(permissionDef.getId()[j]).append(',');
                            n++;
                        }
                    }
                    if (n == 0) {
                        // 租户没这个功能授权
                        permissionsDefs.remove(i);
                    } else if (n < permissionDef.getId().length) {
                        // 租户只有这个授权的部分id，所以需要更新id
                        permissionDef.setId(StringUtils.split(buf.toString(), ','));
                    }
                }
            }
            return permissionsDefs;
        }
    }

    /**
     * 从指定的包路径下获取所有Controller带权限注解的许可内容
     *
     * @param basicPackage 仅扫描指定包下的Controller
     * @return 扫描到的所有权限
     */
    default List<Permission> getPermissions(String basicPackage) {
        Map<String, Object> controllers = SpringContextUtils.getBeansWithAnnotation(Controller.class);
        List<Permission> permissions = new ArrayList<>();

        if (!SpringContextUtils.containsBean("requiresPermissionsHelper")) {
            return permissions;
        }
        RequiresPermissionsHelper requiresPermissionsHelper =
                (RequiresPermissionsHelper) SpringContextUtils.getBean("requiresPermissionsHelper");

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object bean = entry.getValue();
            if (!StringUtils.contains(ClassUtils.getPackageName(bean.getClass()), basicPackage)) {
                continue;
            }

            Class<?> clazz = bean.getClass();
            Class<?> controllerClass = clazz.getSuperclass();
            RequestMapping clazzRequestMapping = AnnotationUtils.findAnnotation(controllerClass, RequestMapping.class);
            List<Method> methods = requiresPermissionsHelper.getMethodsWithRequiresPermissions(controllerClass);

            for (Method method : methods) {
                String[] permissionIds = requiresPermissionsHelper.getRequiresPermissionsId(method);
                PermissionDescription permissionDescription = AnnotationUtils.getAnnotation(method,
                        PermissionDescription.class);

                if (permissionIds == null || permissionDescription == null || permissionIds.length <= 0) {
                    continue;
                }

                String api = "";
                if (clazzRequestMapping != null) {
                    api = clazzRequestMapping.value().length <= 0 ? StringUtils.EMPTY : clazzRequestMapping.value()[0];
                }

                boolean found = false;
                PostMapping postMapping = AnnotationUtils.getAnnotation(method, PostMapping.class);
                if (postMapping != null) {
                    api = "POST " + api + (postMapping.value().length <= 0 ? StringUtils.EMPTY : postMapping.value()[0]);
                    found = true;
                }

                if (!found) {
                    GetMapping getMapping = AnnotationUtils.getAnnotation(method, GetMapping.class);
                    if (getMapping != null) {
                        api = "GET " + api + (getMapping.value().length <= 0 ? StringUtils.EMPTY : getMapping.value()[0]);
                        found = true;
                    }
                }

                if (!found) {
                    DeleteMapping deleteMapping = AnnotationUtils.getAnnotation(method, DeleteMapping.class);
                    if (deleteMapping != null) {
                        api = "DELETE " + api + (deleteMapping.value().length <= 0 ? StringUtils.EMPTY : deleteMapping.value()[0]);
                        found = true;
                    }
                }

                if (!found) {
                    PutMapping putMapping = AnnotationUtils.getAnnotation(method, PutMapping.class);
                    if (putMapping != null) {
                        api = "PUT " + api + (putMapping.value().length <= 0 ? StringUtils.EMPTY : putMapping.value()[0]);
                        found = true;
                    }
                }

                if (!found) {
                    RequestMapping requestMapping = AnnotationUtils.getAnnotation(method, RequestMapping.class);
                    if (requestMapping != null) {
                        String mappingPath = (requestMapping.value().length <= 0 ? StringUtils.EMPTY : requestMapping.value()[0]);
                        if (requestMapping.method().length == 1) {
                            api = requestMapping.method()[0].name() + " " + api + mappingPath;
                        } else {
                            StringBuilder buf = new StringBuilder();
                            for (RequestMethod rm : requestMapping.method()) {
                                buf.append(rm.name()).append('|');
                            }
                            api = buf.deleteCharAt(buf.length() - 1)
                                    .append(' ')
                                    .append(api)
                                    .append(mappingPath)
                                    .toString();
                        }
                        found = true;
                    }
                }

                if (found) {
                    Permission permission = new Permission();
                    permission.setId(permissionIds);
                    permission.setDescription(permissionDescription);
                    permission.setApi(api);
                    permissions.add(permission);
                    continue;
                }

                // 只支持这几种Mapping，应该足够了
                throw new RuntimeException(
                        "Permission management only allows the following annotations: " +
                                "GetMapping, PostMapping, DeleteMapping, PutMapping and RequestMapping.");
            }
        }
        return permissions;
    }

}
