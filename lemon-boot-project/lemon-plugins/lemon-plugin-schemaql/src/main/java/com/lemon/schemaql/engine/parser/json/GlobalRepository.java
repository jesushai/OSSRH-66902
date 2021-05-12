package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.GlobalConfig;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/5/10
 */
public class GlobalRepository extends AbstractSchemaRepository<GlobalConfig> {

    private static final String JSON_FILE_NAME = "global.json";

    public GlobalRepository() {
        super(FileUtil.mergePath(jsonRootPath(), JSON_FILE_NAME));
    }
}
