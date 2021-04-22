package com.lemon.framework.auth.shiro;

import com.lemon.framework.auth.RequiresPermissionsHelper;
import com.lemon.framework.core.annotation.Helper;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
@Helper("requiresPermissionsHelper")
public class ShiroRequiresPermissionsHelper implements RequiresPermissionsHelper {

    @Override
    public List<Method> getMethodsWithRequiresPermissions(Class<?> controllerClass) {
        return MethodUtils.getMethodsListWithAnnotation(controllerClass, RequiresPermissions.class);
    }

    @Override
    public String[] getRequiresPermissionsId(Method method) {

        RequiresPermissions requiresPermissions = AnnotationUtils.getAnnotation(method,
                RequiresPermissions.class);

        if (requiresPermissions == null) {
            return null;
        } else {
            return requiresPermissions.value();
        }
    }
}
