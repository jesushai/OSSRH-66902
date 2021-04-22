package com.lemon.schemaql;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
@Data
@ConfigurationProperties(prefix = "zh.plugin.schemaql")
public class SchemaQlProperties {

    /**
     * 工程Json资源文件的根路径
     */
    private String projectJsonFile = "schema/json";

    /**
     * 是否启用动态数据源
     */
    private boolean dynamicDatasource = false;

    /**
     * SchemaQl查询超时时间
     */
    private Duration queryTimeout = Duration.ofSeconds(10);

    /**
     * 验证消息的国际化资源文件
     * 例如：i18n/validation/validation-message
     */
    private String validationMessages = "";

}
