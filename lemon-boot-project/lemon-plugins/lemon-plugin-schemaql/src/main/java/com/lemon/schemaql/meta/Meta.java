package com.lemon.schemaql.meta;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
public interface Meta<T extends Meta> {

    T toMeta();
}
