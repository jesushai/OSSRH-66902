package com.lemon.framework.cache.redisson;

import com.lemon.framework.core.aspect.AbstractAspect;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

/**
 * <b>名称：分布式锁切片</b><br/>
 * <b>描述：</b><br/>
 * 顺序是最后执行，避免锁时间过长
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAspect extends AbstractAspect implements Ordered {

    public DistributedLockAspect(DistributedLocker redisLocker) {
        this.redisLocker = redisLocker;
        LoggerUtils.debug(log, "DistributedLockAspect ready.");
    }

    @Pointcut("@annotation(com.lemon.framework.cache.redisson.DistributedLock)")
    public void distributedLock() {
    }

    private final DistributedLocker redisLocker;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        LoggerUtils.debug(log, "[Start] Perform RedisLock around notification to obtain redis distributed lock.");

        // 获取锁名称
        String resourceName = computeResourceName(joinPoint, distributedLock);
        // 获取超时时间，默认十秒
        int releaseSeconds = distributedLock.releaseTime();

        Object result = null;
        try {
            result = redisLocker.lock(resourceName, () -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable throwable) {
                    throw new Exception(throwable);
                }
            }, releaseSeconds);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            new ExceptionBuilder()
                    .code("DISTRIBUTED-LOCK-ACQUIRE-FAILED")
                    .args(resourceName)
                    .messageTemplate("分布式锁获取失败 - 资源[{0}]！")
                    .throwIt();
        }

        LoggerUtils.debug(log, "[Finished] Perform RedisLock around notification.");
        return result;
    }

    private String computeResourceName(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(distributedLock.expression());
        return getValueExpression(joinPoint, expression);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
