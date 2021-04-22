package com.lemon.framework.core.annotation;

import com.lemon.framework.log.LogTypeEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiDescription {

    /**
     * @return 日志类型（默认BUSINESS）
     */
    LogTypeEnum type() default LogTypeEnum.BUSINESS;

    /**
     * @return 业务名称
     */
    String bizName() default StringUtils.EMPTY;

    /**
     * 例子：<br/><p/>
     * <code>@ApiDescription(description = "biz-desc-admin-user", args = "#user.display")</code>
     *
     * @return 日志描述（配合args支持表达式）
     */
    String description() default StringUtils.EMPTY;

    /**
     * @return 日志描述表达式的变量
     */
    String[] args() default {};

    /**
     * @return 资源的类型
     */
    String resourceType() default StringUtils.EMPTY;

    /**
     * @return 资源ID（支持表达式）
     */
    String resourceId() default StringUtils.EMPTY;
}
