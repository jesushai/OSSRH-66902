package com.lemon.schemaql.engine.helper;

import com.lemon.schemaql.config.*;
import com.lemon.schemaql.engine.parser.json.ProjectSchemaParser;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ProjectSchemaHelper {

    /**
     * 当前工程的SchemaQl配置
     */
    private static ProjectSchemaConfig projectSchemaConfig;

    private static Map<String, CanInput> cacheInputs = new ConcurrentHashMap<>();

    /**
     * 装载工程配置，包含其内部的所有子对象
     *
     * @param jsonRootPath 根路径
     */
    public static void load(String jsonRootPath) {
        setProjectSchemaConfig(new ProjectSchemaParser(jsonRootPath).parse());
        I18NHelper.setI18NConfig(projectSchemaConfig.getI18n());
    }

    public static void setProjectSchemaConfig(ProjectSchemaConfig config) {
        projectSchemaConfig = config;
    }

    public static ProjectSchemaConfig getProjectSchemaConfig() {
        return projectSchemaConfig;
    }

    /**
     * 获取指定的实体结构配置
     *
     * @param entityName 实体名
     * @return 未找到返回null
     */
    public static EntitySchemaConfig getEntitySchema(String entityName) {
        EntitySchemaConfig entitySchema = null;
        if (null != projectSchemaConfig && CollectionUtils.isNotEmpty(projectSchemaConfig.getModuleSchemas())) {
            for (ModuleSchemaConfig module : projectSchemaConfig.getModuleSchemas()) {
                if (CollectionUtils.isNotEmpty(module.getEntitySchemas())) {
                    entitySchema = module.getEntitySchemas().stream()
                            .filter(e -> e.getEntityName().equals(entityName))
                            .findFirst()
                            .orElse(null);
                }

                if (null != entitySchema) {
                    break;
                }
            }
        }
        return entitySchema;
    }

    /**
     * 获取指定的实体结构配置
     *
     * @param dtoName 实体名
     * @return 未找到返回null
     */
    public static DTOSchemaConfig getDTOSchema(String dtoName) {
        DTOSchemaConfig dtoSchemaConfig = null;
        if (null != projectSchemaConfig && CollectionUtils.isNotEmpty(projectSchemaConfig.getModuleSchemas())) {
            for (ModuleSchemaConfig module : projectSchemaConfig.getModuleSchemas()) {
                if (CollectionUtils.isNotEmpty(module.getDtoObjectSchemas())) {
                    dtoSchemaConfig = module.getDtoObjectSchemas().stream()
                            .filter(e -> e.getDtoName().equals(dtoName))
                            .findFirst()
                            .orElse(null);
                }

                if (null != dtoSchemaConfig) {
                    break;
                }
            }
        }
        return dtoSchemaConfig;
    }

    public static CanInput getInputSchema(String name) {
        // cache first.
        CanInput input = cacheInputs.get(name);
        if (null != input) {
            return input;
        }

        // find dto.
        input = getDTOSchema(name);
        if (null == input) {
            // find entity.
            input = getEntitySchema(name);
        }

        // cache it.
        if (null != input) {
            cacheInputs.put(name, input);
        }

        return input;
    }
}
