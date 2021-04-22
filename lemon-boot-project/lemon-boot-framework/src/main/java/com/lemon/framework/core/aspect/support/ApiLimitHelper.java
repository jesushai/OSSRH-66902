package com.lemon.framework.core.aspect.support;

import com.lemon.framework.core.annotation.ApiLimit;
import com.lemon.framework.core.annotation.Helper;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/8
 */
@Helper
public class ApiLimitHelper {

    private static final Map<String, List<ApiLimit>> cache = new ConcurrentHashMap<>();

    public ApiLimit getApiLimit(String basicPackage, String key) {
        List<ApiLimit> apiLimits = getApiLimits(basicPackage);
        for (ApiLimit apiLimit : apiLimits) {
            if (apiLimit.key().equals(key))
                return apiLimit;
        }
        return null;
    }

    public List<ApiLimit> getApiLimits(String basicPackage) {
        List<ApiLimit> apiLimits = cache.get(basicPackage);

        if (null != apiLimits) {
            return apiLimits;
        }

        apiLimits = new ArrayList<>();
        Map<String, Object> controllers = SpringContextUtils.getBeansWithAnnotation(Controller.class);

        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            Object bean = entry.getValue();
            if (!StringUtils.contains(ClassUtils.getPackageName(bean.getClass()), basicPackage)) {
                continue;
            }

            List<Method> methods = getMethodsWithApiLimit(bean.getClass());
            for (Method method : methods) {
                ApiLimit apiLimit = AnnotationUtils.getAnnotation(method, ApiLimit.class);

                if (null != apiLimit) {
                    apiLimits.add(apiLimit);
                }
            }
        }

        cache.put(basicPackage, apiLimits);
        return apiLimits;
    }

    private List<Method> getMethodsWithApiLimit(Class<?> controllerClass) {
        return MethodUtils.getMethodsListWithAnnotation(controllerClass.getSuperclass(), ApiLimit.class);
    }

}
