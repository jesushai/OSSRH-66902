package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.meta.FieldMeta;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class EntitySchemaParser extends AbstractJsonResourceParser<EntitySchemaConfig> {

    public static final String JSON_FILE_NAME = "${name}.json";

    protected EntitySchemaParser(String rootPath, String moduleName, String entityName) {
        super(rootPath + '/' + moduleName + "/entity/" + entityName + ".json");
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
