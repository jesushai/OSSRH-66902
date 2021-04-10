package com.lemon.schemaql.engine.service;

import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.engine.parser.input.AbstractInput;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public interface ISchemaQlAsyncService<T extends AbstractInput> extends ISchemaQlService<T> {

    /**
     * 异步调用统一入口
     *
     * @param input 输入项
     */
    @SchemaQlDS
    Object entranceAsync(T input, ListenableFutureCallback<Object> callback);

}
