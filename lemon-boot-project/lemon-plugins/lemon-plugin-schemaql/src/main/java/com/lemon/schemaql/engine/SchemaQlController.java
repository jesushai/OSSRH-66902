package com.lemon.schemaql.engine;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.SchemaQlProperties;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.Schema;
import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;
import com.lemon.schemaql.engine.parser.input.MutationInput;
import com.lemon.schemaql.engine.parser.input.QueryInput;
import com.lemon.schemaql.engine.service.SchemaQlMutationService;
import com.lemon.schemaql.engine.service.SchemaQlQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 名称：SchemaQl请求入口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/28
 */
@Slf4j
@RestController
@RequestMapping("/schemaql")
@RequiredArgsConstructor
public class SchemaQlController {

    private final SchemaQlMutationService mutationService;
    private final SchemaQlQueryService queryService;
    private final SchemaQlProperties properties;

    /**
     * 获取结构
     *
     * @param target 目标
     * @return 实体或DTO的Schema
     */
    @GetMapping("/{target}")
    public Schema getSchema(@PathVariable("target") String target) {
        EntitySchemaConfig entitySchemaConfig = ProjectSchemaHelper.getEntitySchema(target);
        if (null == entitySchemaConfig) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1001")
                    .args(target)
                    .throwIt();
        }
        return entitySchemaConfig.toMeta();
    }

    /**
     * 执行单一查询
     *
     * @param input 输入
     * @return 结果
     */
    @PostMapping("/query")
    public Object query(@RequestBody QueryInput input) {
        if (null == input) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1002")
                    .throwIt();
        }
        return queryService.entrance(input);
    }

    /**
     * 执行多个查询，查询间没有任何关系是完全独立的<p>
     * 因此可以开启多线程并可以跨多个数据源<p>
     * 每个查询都会对应自己的结果，即使没有也会返回null
     *
     * @param inputs 输入的内容
     * @return 返回的结果
     */
    @PostMapping("/queries")
    public Object[] queries(@RequestBody LinkedList<QueryInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1002")
                    .throwIt();
        }

        // 多个查询的结果，会按照input的顺序返回，null也会返回
        Object[] results = new Object[inputs.size()];
        // 开启N个线程异步执行查询
        Future[] futures = new Future[inputs.size()];

        // 复合查询，开启多个线程
        for (int i = 0; i < inputs.size(); i++) {
            // 监控查询
            StopWatch watch = new StopWatch();
            // 监控起个名字
            watch.start("Query " + inputs.get(i).getTarget());
            // 异步执行
            futures[i] = queryService.entranceAsync(inputs.get(i),
                    // 在回调中输出监控的日志，供分析用
                    new ListenableFutureCallback<Object>() {
                        @Override
                        public void onFailure(@NonNull Throwable throwable) {
                            watch.stop();
                            LoggerUtils.error(log, throwable.getMessage());
                            new ExceptionBuilder<>()
                                    .code("SCHEMAQL-1003")
                                    .args(throwable.getMessage())
                                    .throwIt();
                        }

                        @Override
                        public void onSuccess(Object o) {
                            watch.stop();
                            LoggerUtils.debug(log, watch.prettyPrint());
                        }
                    });
        }

        // 等待所有的结果返回
        for (int i = 0; i < futures.length; i++) {
            try {
                if (properties.getQueryTimeout().getSeconds() <= 0) {
                    results[i] = futures[i].get();
                } else {
                    // 可以设置超时时间，默认10秒，可配置
                    results[i] = futures[i].get(properties.getQueryTimeout().getSeconds(), TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LoggerUtils.error(log, e);
                new ExceptionBuilder<>()
                        .code("SCHEMAQL-1003")
                        .args(e.getMessage())
                        .throwIt();
            }
        }
        return results;
    }

    /**
     * 改变单一资源实体
     *
     * @param input 输入的内容
     * @return 返回的结果
     */
    @PostMapping("/mutation")
    public Object mutation(@RequestBody MutationInput input) {
        if (null == input) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1002")
                    .throwIt();
        }
        return mutationService.entrance(input);
    }

    /**
     * 同时改变多个实体，保持事务原子性即操作顺序
     *
     * @param inputs 输入的内容
     * @return 返回的结果
     */
    @PostMapping("/mutations")
    public Object mutations(@RequestBody LinkedList<MutationInput> inputs) {
        if (CollectionUtils.isEmpty(inputs)) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1002")
                    .throwIt();
        }
        // 根据顺序执行改变操作，保证在一个数据源一个事务内
        return mutationService.entrance(inputs);
    }

}
