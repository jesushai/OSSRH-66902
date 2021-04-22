package com.lemon.schemaql.meta;

import com.lemon.schemaql.config.Schema;
import com.lemon.schemaql.config.support.ForeignKeyConfig;
import com.lemon.schemaql.config.support.FrontSummaryConfig;
import com.lemon.schemaql.config.support.ValidatorConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：字段属性元数据<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@EqualsAndHashCode(callSuper = false, of = "name")
public class FieldMeta extends Schema implements Meta<FieldMeta> {

    /**
     * 属性名
     */
    private String name;

    /**
     * 显示文本，支持国际化（以%开头）
     */
    private String display;

    /**
     * 是否主键ID
     */
    private Boolean idFlag = false;

    /**
     * 数据类型，特殊类型[Enum,ValueObject]
     */
    private String type;

    /**
     * 集合数据类型，可选范围：List, Array, Set
     */
    private String collectionType;

    /**
     * 是否允许修改编辑
     */
    private Boolean editable = true;

    /**
     * 编辑格式，根据不同类型输入
     */
    private String editFormat;

    /**
     * 是否允许为空
     */
    private Boolean allowNull = true;

    /**
     * allowNull=true时可以指定新建实体的默认属性值
     */
    private String defaultVale;

    /**
     * 验证器，前端编辑的允许范围也出自这里
     */
    private ValidatorConfig[] validators;

    /**
     * 是否允许过滤
     */
    private Boolean allowFilter = true;

    /**
     * 字段可过滤的操作符，allowFilter=true有效
     */
    private String[] filterOperators;

    /**
     * 字段对应其他实体
     */
    private ForeignKeyConfig foreignKey;

    /**
     * 允许前端计算的聚合方式
     */
    private FrontSummaryConfig[] summary;

    @Override
    public FieldMeta toMeta() {
        return this;
    }
}
