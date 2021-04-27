package com.lemon.schemaql.meta;

import com.lemon.schemaql.config.Schema;
import com.lemon.schemaql.config.support.CachingConfig;
import com.lemon.schemaql.config.support.OptionsConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 名称：实体元数据<p>
 * 描述：<p>
 * 用于描述实体的架构，面向前端
 *
 * @param <F> 包含的字段属性元数据类型
 * @author hai-zhang
 * @since 2020/7/25
 */
@EqualsAndHashCode(callSuper = false, of = {"entityName"})
@Data
@Accessors(chain = true)
public class EntityMeta<F extends FieldMeta> extends Schema implements Meta<EntityMeta> {

    /**
     * 实体名
     */
    private String entityName;

    /**
     * 中文名
     */
    private String display;

    /**
     * 表头高度
     */
    private double headerHeight;

    /**
     * 记录行高度
     */
    private double rowHeight;

    /**
     * 包含的字段
     */
    private Set<F> fields;

    /**
     * 缓存配置
     */
    private CachingConfig caching;

    /**
     * 其他配置选项
     */
    private OptionsConfig options;

    @Override
    public EntityMeta toMeta() {
        return this;
    }
}
