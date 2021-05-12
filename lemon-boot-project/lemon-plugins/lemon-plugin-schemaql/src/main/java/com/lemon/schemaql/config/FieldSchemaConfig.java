package com.lemon.schemaql.config;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.schemaql.config.support.JsonFormatConfig;
import com.lemon.schemaql.config.support.TypeHandlerConfig;
import com.lemon.schemaql.meta.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * 名称：字段概要数据<p>
 * 描述：<p>
 * 含实体部分
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
    private boolean transientFlag = false;

    /**
     * 类型转换器
     */
    private String typeHandler;

    /**
     * 是否忽视json序列化
     */
    private boolean jsonIgnore = false;

    /**
     * json格式化
     */
    private JsonFormatConfig jsonFormat;

    /**
     * 填充字段，用于代码生成
     */
    @JsonIgnore
    private FieldFill fill = null;

    @JsonIgnore
    @ToString.Exclude
    private TypeHandlerConfig typeHandlerConfig;

    @Override
    public FieldMeta toMeta() {
        FieldMeta meta = new FieldMeta();
        BeanUtils.copyProperties(this, meta);
        return meta;
    }
}
