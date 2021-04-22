package com.lemon.boot.autoconfigure.commons;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

/**
 * 名称：开启异步线程池执行器<br/>
 * 描述：<br/>
 * 同时会开启@EnableAsync
 *
 * @author hai-zhang
 * @since 2020-5-19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableAsync
@Import(ThreadPoolTaskExecutorAutoConfiguration.class)
public @interface EnableAsyncExecutor {
    @AliasFor(annotation = EnableAsync.class)
    Class<? extends Annotation> annotation() default Annotation.class;

    @AliasFor(annotation = EnableAsync.class)
    boolean proxyTargetClass() default false;

    @AliasFor(annotation = EnableAsync.class)
    AdviceMode mode() default AdviceMode.PROXY;

    @AliasFor(annotation = EnableAsync.class)
    int order() default 2147483647;
}
