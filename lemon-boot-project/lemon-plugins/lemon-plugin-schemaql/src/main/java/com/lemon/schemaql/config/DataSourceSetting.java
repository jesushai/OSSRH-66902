package com.lemon.schemaql.config;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：数据源配置信息<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
@Data
@Accessors(chain = true)
public class DataSourceSetting {
    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据库地址
     */
    private String url;

    /**
     * 驱动类
     */
    private String driverName;

    private String username;

    private String password;
}
