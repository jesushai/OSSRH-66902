package com.lemon.framework.core.annotation;

import com.lemon.framework.log.LogTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDescription {

    /**
     * 日志类型
     */
    LogTypeEnum type() default LogTypeEnum.BUSINESS;

    /**
     * 业务名称
     */
    String bizName() default StringUtils.EMPTY;

    /**
     * 日志描述（配合args支持表达式）<p/>
     * 例子：<br/>
     * <code>@ApiDescription(description = "biz-desc-admin-user", args = "#user.display")</code>
     */
    String description() default StringUtils.EMPTY;

    /**
     * 日志描述表达式的变量
     */
    String[] args() default {};

    /**
     * 资源的类型
     */
    String resourceType() default StringUtils.EMPTY;

    /**
     * 资源ID（支持表达式）
     */
    String resourceId() default StringUtils.EMPTY;
}
