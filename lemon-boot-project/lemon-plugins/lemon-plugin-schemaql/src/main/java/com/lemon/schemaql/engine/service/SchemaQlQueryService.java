package com.lemon.schemaql.engine.service;

import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.engine.parser.input.QueryInput;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.Future;

/**
 * 名称：SchemaQl查询服务<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
@Service
public class SchemaQlQueryService implements ISchemaQlAsyncService<QueryInput> {

    /**
     * 同步查询
     *
     * @param input 输入项
     */
    @Transactional(readOnly = true)
    @SchemaQlDS
    @Override
    public Object entrance(QueryInput input) {
        return null;
    }

    /**
     * 异步查询
     */
    @Transactional(readOnly = true)
    @SchemaQlDS
    @Async(BeanNameConstants.CORE_ASYNC_POOL)
    public Future<Object> entranceAsync(QueryInput input, ListenableFutureCallback<Object> callback) {
        AsyncResult<Object> asyncResult = new AsyncResult<>(entrance(input));
        if (null != callback) {
            asyncResult.addCallback(callback);
        }
        return asyncResult;
    }

}
