package com.lemon.schemaql.engine.aspect;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.lemon.schemaql.config.CanInput;
import com.lemon.schemaql.config.support.DynamicDatasourceConfig;
import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;
import com.lemon.schemaql.engine.parser.input.AbstractInput;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * 名称：SchemaQl动态数据源拦截器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public class DynamicDatasourceAnnotationInterceptor implements MethodInterceptor {

    private DsProcessor dsProcessor;

    @Override
    public Object invoke(@NonNull MethodInvocation invocation) throws Throwable {
        Object result;
        try {
            DynamicDataSourceContextHolder.push(this.determineDatasource(invocation));
            result = invocation.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll();
        }
        return result;
    }

    private String determineDatasource(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();

        // 只有一个参数，或是List<Input>或是Input
        // Mutation不支持多数据源
        if (ArrayUtils.isEmpty(arguments) || null == arguments[0]) {
            throw new Exception("SchemaQl arguments must not be empty.");
        }

        // 以第一个input为代表
        AbstractInput represent;
        if (arguments[0] instanceof Collection) {
            Collection<?> collection = (Collection<?>) arguments[0];
            if (collection.isEmpty()) {
                throw new Exception("SchemaQl arguments must not be empty.");
            }

            Class<?> clazz = (Class<?>) ((ParameterizedType) collection.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (!AbstractInput.class.isAssignableFrom(clazz)) {
                throw new Exception("SchemaQl arguments must inherit from AbstractInput class.");
            }

            represent = (AbstractInput) collection.stream().findFirst().orElse(null);
            if (null == represent) {
                throw new Exception("SchemaQl arguments must not be null.");
            }
        } else {
            if (!AbstractInput.class.isAssignableFrom(arguments[0].getClass())) {
                throw new Exception("SchemaQl arguments must inherit from AbstractInput class.");
            }
            represent = (AbstractInput) arguments[0];
        }

        // 获取输入的结构模型中定义的数据源
        // 没有定义则返回null，使用默认数据源
        CanInput input = ProjectSchemaHelper.getInputSchema(represent.getTarget());
        DynamicDatasourceConfig datasourceConfig = null;
        if (null != input && null != input.getDynamicDataSources()) {
            for (DynamicDatasourceConfig ds : input.getDynamicDataSources()) {
                if (ds.getType() == represent.getType()) {
                    datasourceConfig = ds;
                    break;
                }
            }
        }

        String key = StringUtils.EMPTY;
        if (null != datasourceConfig) {
            key = datasourceConfig.getDatasource();
        }

        return !key.isEmpty() && key.startsWith("#") ? this.dsProcessor.determineDatasource(invocation, key) : key;
    }

    public void setDsProcessor(DsProcessor dsProcessor) {
        this.dsProcessor = dsProcessor;
    }
}
