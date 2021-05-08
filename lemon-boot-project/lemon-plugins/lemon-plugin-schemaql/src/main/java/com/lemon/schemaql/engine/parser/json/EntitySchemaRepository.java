package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.meta.FieldMeta;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class EntitySchemaRepository extends AbstractSchemaRepository<EntitySchemaConfig> {

    protected EntitySchemaRepository(String moduleName, String entityName) {
        super(FileUtil.mergePath(
                jsonRootPath(),
                moduleName,
                "entity",
                entityName + ".json"));
    }

    @Override
    public EntitySchemaConfig parse() {
        EntitySchemaConfig entitySchemaConfig = super.parse();
        // 设置主键
        entitySchemaConfig.setKeyField(entitySchemaConfig.getFields().stream()
                .filter(FieldMeta::getIdFlag)
                .findFirst()
                .orElseThrow(() -> new ExceptionBuilder<>()
                        .code("SCHEMAQL-1010")
                        .args(entitySchemaConfig.getEntityClassName())
                        .build()));
        return entitySchemaConfig;
    }
}
