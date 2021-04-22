package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.ValueObjectSchemaConfig;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ValueObjectSchemaParser extends AbstractJsonResourceParser<ValueObjectSchemaConfig> {

    public final static String JSON_FILE_NAME = "${name}.json";

    protected ValueObjectSchemaParser(String rootPath, String moduleName, String voName) {
        super(rootPath + '/' + moduleName + "/vo/" + voName + ".json");
    }
}
