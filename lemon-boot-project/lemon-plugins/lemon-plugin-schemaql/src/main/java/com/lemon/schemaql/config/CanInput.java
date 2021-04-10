package com.lemon.schemaql.config;

import com.lemon.schemaql.config.support.DynamicDatasourceConfig;

/**
 * <b>名称：标示接口</b><br/>
 * <b>描述：</b><br/>
 * 作为输入参数
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public interface CanInput {

    /**
     * 指定数据源，用于多数据源动态切换
     */
    DynamicDatasourceConfig[] getDynamicDataSources();
}
