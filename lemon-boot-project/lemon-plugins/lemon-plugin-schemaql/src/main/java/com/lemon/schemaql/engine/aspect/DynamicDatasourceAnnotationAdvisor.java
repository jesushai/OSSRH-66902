package com.lemon.schemaql.engine.aspect;

import com.lemon.schemaql.annotation.SchemaQlDS;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.NonNull;

/**
 * <b>名称：SchemaQl动态数据源切片</b><br/>
 * <b>描述：</b><br/>
 * 对注解 <code>@SchemaQlDS</code> 进行拦截
 * <p/>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public class DynamicDatasourceAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Advice advice;
    private Pointcut pointcut;

    public DynamicDatasourceAnnotationAdvisor(DynamicDatasourceAnnotationInterceptor interceptor) {
        if (null == interceptor) {
            throw new NullPointerException("DynamicDatasourceAnnotationInterceptor");
        }
        this.advice = interceptor;
        this.pointcut = buildPointcut();
    }

    @Override
    @NonNull
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    @NonNull
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }

    private Pointcut buildPointcut() {
        Pointcut cpc = new AnnotationMatchingPointcut(SchemaQlDS.class, true);
        Pointcut mpc = new AnnotationMatchingPointcut(null, SchemaQlDS.class, true);
        return new ComposablePointcut(cpc).union(mpc);
    }
}
