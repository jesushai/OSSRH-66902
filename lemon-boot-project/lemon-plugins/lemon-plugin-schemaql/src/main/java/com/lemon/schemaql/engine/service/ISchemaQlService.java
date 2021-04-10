package com.lemon.schemaql.engine.service;

import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.engine.parser.input.AbstractInput;

/**
 * <b>名称：SchemaQl核心</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public interface ISchemaQlService<T extends AbstractInput> {

    /**
     * 统一入口
     *
     * @param input 输入项
     */
    @SchemaQlDS
    Object entrance(T input);

}
