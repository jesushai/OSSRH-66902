package com.lemon.schemaql.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.schemaql.config.support.i18n.I18NConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false, of = {"projectName"})
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
     * 国际化全局配置
     */
    private Set<I18NConfig> i18n;

    /**
     * 默认的国际化
     */
    private String i18nDefault;

    /**
     * 工程内的模块结构
     */
    @JsonIgnore
    @ToString.Exclude
    private Set<ModuleSchemaConfig> moduleSchemas;

}
