package com.lemon.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 名称：权限描述<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionDescription {

    String[] menu();

    String action();
}
