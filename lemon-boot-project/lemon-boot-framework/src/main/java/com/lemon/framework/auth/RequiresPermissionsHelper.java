package com.lemon.framework.auth;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 名称：需要获取授权许可的标示接口<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
public interface RequiresPermissionsHelper {

    /**
     * 获取Controller类里所有带权限许可注解的方法
     *
     * @param controllerClass Controller
     * @return 类里带许可的方法
     */
    List<Method> getMethodsWithRequiresPermissions(Class<?> controllerClass);

    /**
     * 获取方法上的权限许可ID
     *
     * @param method 方法
     * @return 方法允许的授权许可
     */
    String[] getRequiresPermissionsId(Method method);
}
