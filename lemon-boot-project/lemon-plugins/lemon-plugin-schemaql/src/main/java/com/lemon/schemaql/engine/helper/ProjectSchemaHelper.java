package com.lemon.schemaql.engine.helper;

import com.lemon.schemaql.config.CanInput;
import com.lemon.schemaql.config.DTOSchemaConfig;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.engine.parser.json.ProjectSchemaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;
import static com.lemon.schemaql.engine.SchemaQlContext.projectSchemaConfig;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ProjectSchemaHelper {

    private static Map<String, CanInput> cacheInputs = new ConcurrentHashMap<>();

    /**
     * 装载工程配置，包含其内部的所有子对象
     *
     * @param jsonRootPath 根路径
     */
    public static void load(@NonNull String jsonRootPath) {
        jsonRootPath(jsonRootPath);
        projectSchemaConfig(new ProjectSchemaRepository().parse());
    }

    /**
     * 获取指定的实体结构配置
     *
     * @param entityName 实体名
     * @return 未找到返回null
     */
    public static EntitySchemaConfig getEntitySchema(String entityName) {
        EntitySchemaConfig entitySchema = null;
        if (CollectionUtils.isNotEmpty(projectSchemaConfig().getModuleSchemas())) {
            for (ModuleSchemaConfig module : projectSchemaConfig().getModuleSchemas()) {
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
        if (CollectionUtils.isNotEmpty(projectSchemaConfig().getModuleSchemas())) {
            for (ModuleSchemaConfig module : projectSchemaConfig().getModuleSchemas()) {
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
