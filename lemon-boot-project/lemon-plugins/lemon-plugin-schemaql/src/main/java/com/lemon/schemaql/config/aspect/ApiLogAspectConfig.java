package com.lemon.schemaql.config.aspect;

import com.lemon.framework.log.LogTypeEnum;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * 名称：日志切片配置<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true, of = {"type", "bizName", "description"})
public class ApiLogAspectConfig extends DynamicAspectConfig {

    public ApiLogAspectConfig() {
        super();
    }

    /**
     * 日志切片只有当前api的操作类型可以修改
     *
     * @param operationTypes api操作类型
     */
    public ApiLogAspectConfig(OperationTypeEnum[] operationTypes) {
        super("ApiLogAspect",
                "自动日志切片",
                "com.lemon.schemaql.config.aspect",
                operationTypes,
                Ordered.LOWEST_PRECEDENCE - 10000);
    }

    /**
     * 日志类型：SYSTEM|BUSINESS|SQL|ERROR
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
