package com.lemon.schemaql.engine;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.schemaql.config.GlobalConfig;
import com.lemon.schemaql.config.ProjectSchemaConfig;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/5/7
 */
public class SchemaQlContext {

    /**
     * 当前工程的SchemaQl配置
     */
    private static ProjectSchemaConfig projectSchemaConfig;

    private static GlobalConfig globalConfig;

    private static String jsonRootPath;

    public static GlobalConfig globalConfig() {
        return globalConfig;
    }

    public static void globalConfig(GlobalConfig globalConfig) {
        SchemaQlContext.globalConfig = globalConfig;
    }

    public static ProjectSchemaConfig projectSchemaConfig() {
        if (null == projectSchemaConfig) {
            new ExceptionBuilder<>(SystemException.class)
                    .messageTemplate("Please load project schema json.")
                    .throwIt();
        }
        return projectSchemaConfig;
    }

    public static void projectSchemaConfig(ProjectSchemaConfig projectSchemaConfig) {
        SchemaQlContext.projectSchemaConfig = projectSchemaConfig;
    }

    public static String jsonRootPath() {
        return jsonRootPath;
    }

    public static void jsonRootPath(String jsonRootPath) {
        SchemaQlContext.jsonRootPath = jsonRootPath;
    }
}
