package com.lemon.framework.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * 名称：监控器<p>
 * 描述：<p>
 * 用于前端监控系统核心指标
 *
 * @author hai-zhang
 * @since 2020/5/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface Monitor {
    @AliasFor(
            annotation = Controller.class
    )
    String value() default "";
}
