package com.lemon.schemaql.config.support;

import com.lemon.schemaql.config.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, of = "name")
public class TypeHandlerConfig extends Schema {

    /**
     * 类型转换器的名字
     */
    private String name;

    /**
     * 注释
     */
    private String comment;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 类型转换目标泛型类型
     */
    private String convertType;
}
