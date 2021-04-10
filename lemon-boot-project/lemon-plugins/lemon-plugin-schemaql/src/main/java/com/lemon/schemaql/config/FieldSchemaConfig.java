package com.lemon.schemaql.config;

import com.lemon.schemaql.config.support.JsonFormatConfig;
import com.lemon.schemaql.meta.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true, of = "columnName")
public class FieldSchemaConfig extends FieldMeta {

    /**
     * 表字段名
     */
    private String columnName;

    /**
     * 表数据类型
     */
    private String columnType;

    /**
     * 字段注释
     */
    private String comment;

    /**
     * 临时属性，没这个表字段
     */
    private Boolean transientFlag = false;

    /**
     * 类型转换器
     */
    private String typeHandler;

    /**
     * 是否json序列化
     */
    private Boolean jsonIgnore = true;

    /**
     * json格式化
     */
    private JsonFormatConfig jsonFormat;

    @Override
    public FieldMeta toMeta() {
        FieldMeta meta = new FieldMeta();
        BeanUtils.copyProperties(this, meta);
        return meta;
    }
}