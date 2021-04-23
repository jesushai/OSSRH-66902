package com.lemon.schemaql.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 名称：工程结构<p>
 * 描述：<p>
 * 根
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
@EqualsAndHashCode(callSuper = false, of = {"projectName"})
@Data
@Accessors(chain = true)
public class ProjectSchemaConfig extends Schema {

    /**
     * 工程名称
     */
    private String projectName;

    /**
     * 工程根路径
     */
    private String projectPath;

    /**
     * 实体全局配置
     */
    private EntitySetting entitySettings;

    /**
     * 预定义好工程使用的所有数据源
     */
    private Set<DataSourceSetting> dataSources;

    /**
     * 工程内的模块
     */
    private Set<String> modules;

    /**
     * 工程内的模块结构
     */
    @JsonIgnore
    private Set<ModuleSchemaConfig> moduleSchemas;
}
