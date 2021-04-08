package com.lemon.framework.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <b>名称：组件辅助类</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Helper {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
