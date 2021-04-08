package com.lemon.framework.core.annotation;

import com.lemon.framework.core.enums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>名称：接口限流</b><br/>
 * <b>描述：</b><br/>
 * 需要配合redis使用
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLimit {

    /**
     * 资源名称，用于描述接口功能
     */
    String name() default "";

    /**
     * Redis资源key
     */
    String key() default "";

    /**
     * Redis资源key前缀
     */
    String prefix() default "";

    /**
     * 时间范围，单位秒
     */
    int period();

    /**
     * 限制访问次数
     */
    int count();

    /**
     * 限制类型
     */
    LimitType limitType() default LimitType.CUSTOMER;

}
