package com.lemon.schemaql.config;

import com.lemon.schemaql.config.support.DynamicDatasourceConfig;
import com.lemon.schemaql.meta.DTOMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/27
 */
@EqualsAndHashCode(callSuper = true, of = {"dtoName"})
@Data
@Accessors(chain = true)
public class DTOSchemaConfig extends DTOMeta<FieldSchemaConfig> implements CanInput {

    /**
     * DTO名
     */
    private String dtoName;

    /**
     * 动态数据源
     */
    private DynamicDatasourceConfig[] dynamicDataSources;

}
