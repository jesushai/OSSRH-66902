package com.lemon.schemaql.test;

import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;
import org.junit.jupiter.api.Test;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/27
 */
class SchemaParserTest {

    @Test
    void testParserRoot() {
        String rootPath = "E:/_FrameWork/Java/github/jesushai/lemon-boot/schemaql/src/main/resources/schema/json";
        ProjectSchemaHelper.load(rootPath);
    }
}
