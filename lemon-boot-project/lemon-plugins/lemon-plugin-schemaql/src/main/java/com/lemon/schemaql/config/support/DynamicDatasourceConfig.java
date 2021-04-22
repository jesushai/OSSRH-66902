package com.lemon.schemaql.config.support;

import com.lemon.schemaql.enums.InputTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：动态数据源配置<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
@Data
@Accessors(chain = true)
public class DynamicDatasourceConfig {

    /**
     * 输入类型: query|mutation
     */
    private InputTypeEnum type;

    /**
     * 动态数据源，支持多租户高级动态
     */
    private String datasource;
}
