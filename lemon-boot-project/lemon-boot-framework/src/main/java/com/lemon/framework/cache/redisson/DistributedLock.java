package com.lemon.framework.cache.redisson;

import java.lang.annotation.*;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DistributedLock {

    /**
     * 分布式锁的资源名，支持Spel表达式<br/>
     * 例子：
     * <li>'BusinessCode'</li>
     * <li>'BusinessPrefix'+#paramName</li>
     * <li>'BusinessPrefix'+#paramObject.fieldName</li>
     *
     * @return expression
     */
    String expression() default "";

    /**
     * 锁自动释放的时间，默认10秒
     *
     * @return releaseTime
     */
    int releaseTime() default 10;
}
