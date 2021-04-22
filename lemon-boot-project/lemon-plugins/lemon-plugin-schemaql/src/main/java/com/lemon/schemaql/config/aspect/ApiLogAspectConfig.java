package com.lemon.schemaql.config.aspect;

import com.lemon.framework.log.LogTypeEnum;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * 名称：日志切片配置<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@EqualsAndHashCode(callSuper = true, of = {"bizName"})
@Data
public class ApiLogAspectConfig extends DynamicAspectConfig {

    public ApiLogAspectConfig() {
        super();
    }

    public ApiLogAspectConfig(OperationTypeEnum[] operationTypes) {
        super("ApiLogAspect",
                "自动日志切片",
                "com.lemon.schemaql.config.aspect",
                operationTypes,
                Ordered.LOWEST_PRECEDENCE - 10000);
    }

    /**
     * 日志类型：SYSTEM|BUSINESS
     */
    private LogTypeEnum type;

    /**
     * 业务名称，支持国际化
     */
    private String bizName;

    /**
     * 日志描述（配合args支持表达式），支持国际化
     */
    private String description;

    /**
     * 日志描述的参数：支持Spel表达式
     */
    private String[] args;
}
