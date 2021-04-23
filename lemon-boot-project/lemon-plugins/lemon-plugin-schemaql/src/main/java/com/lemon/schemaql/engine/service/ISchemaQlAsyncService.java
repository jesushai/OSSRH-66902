package com.lemon.schemaql.engine.service;

import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.engine.parser.input.AbstractInput;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public interface ISchemaQlAsyncService<T extends AbstractInput> extends ISchemaQlService<T> {

    /**
     * 异步调用统一入口
     *
     * @param input 输入项
     * @param callback 回调
     * @return 结果
     */
    @SchemaQlDS
    Object entranceAsync(T input, ListenableFutureCallback<Object> callback);

}
