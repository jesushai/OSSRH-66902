package com.lemon.schemaql.engine.service;

import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.engine.parser.input.AbstractInput;

/**
 * 名称：SchemaQl核心<br/>
 * 描述：<br/>
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
