package com.lemon.schemaql.config;

import com.lemon.schemaql.meta.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <b>名称：值对象配置</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@EqualsAndHashCode(of = {"voName"}, callSuper = true)
@Data
@Accessors(chain = true)
public class ValueObjectSchemaConfig extends Schema implements Meta {

    /**
     * 值对象名称
     */
    private String voName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 属性
     */
    private Set<FieldSchemaConfig> fields;

    @Override
    public Meta toMeta() {
        return null;
    }
}
