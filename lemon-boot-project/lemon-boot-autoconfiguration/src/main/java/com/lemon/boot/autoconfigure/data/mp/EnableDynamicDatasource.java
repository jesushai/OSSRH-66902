package com.lemon.boot.autoconfigure.data.mp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 名称：开启动态数据源支持<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({DynamicDatasourceMultiTenantAutoConfiguration.class})
public @interface EnableDynamicDatasource {
}
