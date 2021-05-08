package com.lemon.schemaql.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：数据源配置信息<p>
 * 描述：<p>
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
     * 驱动类
     */
    private String driverName;

    /**
     * 加密的数据库地址
     */
    private String url;

    /**
     * 加密的用户名
     */
    private String username;

    /**
     * 加密的密码
     */
    private String password;

    /**
     * 解密的数据库地址
     */
    @JsonIgnore
    private String decryptUrl;

    /**
     * 解密的用户名
     */
    @JsonIgnore
    private String decryptUsername;

    /**
     * 解密的密码
     */
    @JsonIgnore
    private String decryptPassword;
}
