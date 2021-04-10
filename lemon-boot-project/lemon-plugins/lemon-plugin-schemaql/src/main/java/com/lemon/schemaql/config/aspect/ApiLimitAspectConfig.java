package com.lemon.schemaql.config.aspect;

import com.lemon.framework.core.enums.LimitType;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * <b>名称：API限流切片配置</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@EqualsAndHashCode(callSuper = true, of = {"key"})
@Data
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
