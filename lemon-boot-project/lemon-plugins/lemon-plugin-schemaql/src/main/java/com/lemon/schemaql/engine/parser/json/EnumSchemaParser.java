package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.EnumSchemaConfig;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public class EnumSchemaParser extends AbstractJsonResourceParser<EnumSchemaConfig> {

    public EnumSchemaParser(String rootPath, String moduleName, String enumName) {
        super(rootPath + '/' + moduleName + "/enums/" + enumName + ".json");
    }

    @Override
    public EnumSchemaConfig parse() {
        return super.parse();
    }
}
