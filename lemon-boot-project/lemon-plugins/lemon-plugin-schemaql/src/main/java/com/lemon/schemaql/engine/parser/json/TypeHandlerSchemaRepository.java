package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.support.TypeHandlerConfig;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class TypeHandlerSchemaRepository extends AbstractSchemaRepository<TypeHandlerConfig> {

    /**
     * TypeHandler路径：root/module/TypeHandlerX.json
     *
     * @param moduleName      所属模块
     * @param typeHandlerName 转换器名称
     */
    public TypeHandlerSchemaRepository(String moduleName, String typeHandlerName) {
        super(FileUtil.mergePath(
                jsonRootPath(),
                moduleName,
                "type-handler",
                typeHandlerName+".json"));
    }
}
