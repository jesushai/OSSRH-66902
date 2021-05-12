package com.lemon.schemaql.config.aspect;

import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.Ordered;

/**
 * 名称：分布式锁切片配置<p>
 * 描述：<p>
 * 分布式锁切片优先级最低，仅在具体业务之前上锁，保证锁的期限最短
 * <p>
 * 锁的自动释放时间默认是10秒
 * <p>
 * 锁的key支持表达式
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true, of = {"lockResourceName"})
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
