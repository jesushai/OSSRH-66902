package com.lemon.schemaql.config.aspect;

import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * <b>名称：分布式锁切片配置</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@EqualsAndHashCode(callSuper = true, of = {"lockResourceName"})
@Data
public class DistributedLockAspectConfig extends DynamicAspectConfig {

    public DistributedLockAspectConfig(OperationTypeEnum[] operationTypes) {
        super("DistributedLockAspect",
                "分布式锁切片",
                "com.lemon.framework.cache.redisson",
                operationTypes,
                Ordered.LOWEST_PRECEDENCE);
    }

    /**
     * 分布式锁的资源名，支持Spel表达式
     */
    private String lockResourceName;

    /**
     * 锁自动释放的时间，默认10秒
     */
    private int releaseTime = 10;

}
