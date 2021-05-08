package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.EnumSchemaConfig;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public class EnumSchemaRepository extends AbstractSchemaRepository<EnumSchemaConfig> {

    public EnumSchemaRepository(String moduleName, String enumName) {
        super(FileUtil.mergePath(jsonRootPath(), moduleName, "enums", enumName + ".json"));
    }

    @Override
    public EnumSchemaConfig parse() {
        return super.parse();
    }
}
