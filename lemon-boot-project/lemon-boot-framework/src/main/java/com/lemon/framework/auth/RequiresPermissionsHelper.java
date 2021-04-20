package com.lemon.framework.auth;

import java.lang.reflect.Method;
import java.util.List;

/**
 * <b>名称：需要获取授权许可的标示接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
public interface RequiresPermissionsHelper {

    /**
     * 获取Controller类里所有带权限许可注解的方法
     *
     * @param controllerClass Controller
     */
    List<Method> getMethodsWithRequiresPermissions(Class<?> controllerClass);

    /**
     * 获取方法上的权限许可ID
     */
    String[] getRequiresPermissionsId(Method method);
}
