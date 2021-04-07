package com.lemon.framework.cache.redisson;

import java.lang.annotation.*;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
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
     */
    String expression() default "";

    /**
     * 锁自动释放的时间，默认10秒
     */
    int releaseTime() default 10;
}
