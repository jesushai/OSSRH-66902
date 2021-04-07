package com.lemon.framework.util.spring;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * <b>名称：Spring上下文核心工具</b><br/>
 * <b>描述：</b><br/>
 * 在非容器托管类中调用托管bean
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@SuppressWarnings("unused")
@Slf4j
public class SpringContextUtils implements ApplicationContextAware {

    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

//    public SpringContextUtils(ApplicationContext applicationContext) {
//        SpringContextUtils.applicationContext = applicationContext;
//    }

    /**
     * 注入ApplicationContext
     */
    public static void setContext(ApplicationContext context) {
        SpringContextUtils.applicationContext = context;
    }

    /**
     * 实现父类的setApplicationContext方法注入ApplicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        LoggerUtils.debug(log, "SpringContextUtil init applicationContext");
        SpringContextUtils.setContext(context);
    }

    /**
     * 获取ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        checkContext();
        return applicationContext;
    }

    /**
     * 判断是否加载ApplicationContext
     */
    private static void checkContext() {
        if (applicationContext == null) {
            log.error("SpringContextUtils not inject to spring boot.");
            throw new IllegalStateException("SpringContextUtils not inject to spring boot.");
        }
    }

    /**
     * 是否包含名称为name的bean
     */
    public static boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }

    /**
     * 获取Bean
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 通过class获取Bean.
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clazz) {
        return getApplicationContext().getBeansWithAnnotation(clazz);
    }
}
