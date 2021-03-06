package com.lemon.schemaql.config.support;

import com.lemon.schemaql.enums.FetchTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 名称：外键配置<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class ForeignKeyConfig {

    /**
     * 外键关联的实体
     */
    private String entity;

    /**
     * 是否自动查询挂载
     */
    private FetchTypeEnum fetchType = FetchTypeEnum.LAZY;

    /**
     * 当以此属性过滤的时候，输入查询的值，决定作用在外联对象的哪些属性字段上
     */
    private String searchFields;

    /**
     * 需要返回的属性列表，逗号分隔，空则全部返回
     */
    private String fetchFields;

    /**
     * 可以显示的列
     */
    private String displayFields;

    /**
     * 回显表达式，优先级高于displayFields<p>
     * 例子：'['+#{code}+'] '+#{name}<p>
     * 结果大概长这样：'[code] name'
     */
    private String displayExpression;

    /**
     * 是否允许同时返回所有的外键对象
     */
    private Boolean fetchAll;
}
