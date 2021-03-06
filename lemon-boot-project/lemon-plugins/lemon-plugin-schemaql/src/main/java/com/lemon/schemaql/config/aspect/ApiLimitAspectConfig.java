package com.lemon.schemaql.config.aspect;

import com.lemon.framework.core.enums.LimitType;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * 名称：API限流切片配置<p>
 * 描述：<p>
 * 限流切片的优先级最高
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true, of = {"key"})
public class ApiLimitAspectConfig extends DynamicAspectConfig {

    public ApiLimitAspectConfig(OperationTypeEnum[] operationTypes) {
        super("ApiLimitAspect",
                "API限流切片",
                "com.lemon.schemaql.config.aspect",
                operationTypes,
                Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 唯一的标识
     */
    private String key;

    /**
     * api资源名称，用于描述接口的功能，支持国际化
     */
    private String name;

    /**
     * redis的key前缀，默认'limit'
     */
    private String prefix = "limit";

    /**
     * 范围时间内，单位秒
     */
    private int period;

    /**
     * 范围时间内最多访问次数
     */
    private int count;

    /**
     * 限流的类型
     */
    private LimitType limitType;

}
