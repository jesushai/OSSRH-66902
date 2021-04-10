package com.lemon.schemaql.engine;

import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>名称：SchemaQl引擎配置</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@Slf4j
public class SchemaQlEngineConfiguration {

    private final String jsonRootPath;

    public SchemaQlEngineConfiguration(String jsonRootPath) {
        this.jsonRootPath = jsonRootPath;
    }

    public void init() throws Exception {
        // 装载工程配置，包含其内部的所有子对象
        ProjectSchemaHelper.load(jsonRootPath);
    }
}
