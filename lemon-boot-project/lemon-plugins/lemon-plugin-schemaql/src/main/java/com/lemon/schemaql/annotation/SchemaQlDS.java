package com.lemon.schemaql.annotation;

import java.lang.annotation.*;

/**
 * <b>名称：SchemaQl动态数据源</b><br/>
 * <b>描述：</b><br/>
 * 实现自动切换数据源
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SchemaQlDS {

}
