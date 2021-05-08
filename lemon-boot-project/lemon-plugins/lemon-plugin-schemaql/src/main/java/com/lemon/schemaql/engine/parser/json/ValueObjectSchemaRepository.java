package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.ValueObjectSchemaConfig;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ValueObjectSchemaRepository extends AbstractSchemaRepository<ValueObjectSchemaConfig> {

    protected ValueObjectSchemaRepository(String moduleName, String voName) {
        super(FileUtil.mergePath(jsonRootPath(), moduleName, "vo", voName + ".json"));
    }
}
