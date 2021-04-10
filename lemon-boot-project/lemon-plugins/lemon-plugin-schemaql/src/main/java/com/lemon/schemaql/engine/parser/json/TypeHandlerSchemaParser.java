package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.support.TypeHandlerConfig;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class TypeHandlerSchemaParser extends AbstractJsonResourceParser<TypeHandlerConfig> {

    public static final String JSON_FILE_NAME = "${name}.json";

    /**
     * TypeHandler路径：root/module/TypeHandlerX.json
     *
     * @param rootPath        json资源文件根路径
     * @param moduleName      所属模块
     * @param typeHandlerName 转换器名称
     */
    public TypeHandlerSchemaParser(String rootPath, String moduleName, String typeHandlerName) {
        super(rootPath + '/' + moduleName + "/type-handler/" + typeHandlerName + ".json");
    }
}
