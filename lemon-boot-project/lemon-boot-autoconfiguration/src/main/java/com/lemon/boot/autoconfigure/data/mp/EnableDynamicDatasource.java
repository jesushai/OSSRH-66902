package com.lemon.boot.autoconfigure.data.mp;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <b>名称：开启动态数据源支持</b><br/>
 * <b>描述：</b><br/>
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
