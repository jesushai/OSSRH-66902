package com.lemon.boot.autoconfigure.commons;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 名称：开启国际化<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(LocaleAutoConfiguration.class)
public @interface EnableLocaleMessage {
}
