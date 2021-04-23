package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.Schema;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public interface IJsonParser<T extends Schema> {

    T parse();
}
