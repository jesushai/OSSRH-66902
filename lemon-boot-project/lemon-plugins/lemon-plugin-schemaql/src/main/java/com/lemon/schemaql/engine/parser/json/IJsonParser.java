package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.Schema;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public interface IJsonParser<T extends Schema> {

    T parse();
}
