package com.lemon.schemaql.config.aspect;

import lombok.Data;

import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/9/7
 */
@Data
public class AspectConfig {

    /**
     * 自动日志切片
     */
    private ApiLogAspectConfig[] apiLogs;

    /**
     * API限流切片
     */
    private Set<ApiLimitAspectConfig> apiLimits;

    /**
     * 分布式锁切片
     */
    private Set<DistributedLockAspectConfig> distributedLocks;

    /**
     * 其他所有动态切片
     */
    private Set<DynamicAspectConfig> dynamicAspects;
}
