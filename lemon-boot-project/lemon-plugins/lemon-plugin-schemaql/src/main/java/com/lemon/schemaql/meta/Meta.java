package com.lemon.schemaql.meta;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
public interface Meta<T extends Meta> {

    T toMeta();
}
