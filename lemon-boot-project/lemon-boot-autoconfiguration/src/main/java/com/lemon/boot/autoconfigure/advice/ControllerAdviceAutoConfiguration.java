package com.lemon.boot.autoconfigure.advice;

import com.lemon.framework.core.advice.ResponseByResultAdviceProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ResponseHandleAdvice.class, RequestHandleAdvice.class})
@Import({
        ResponseByResultAdviceProcessor.class,
        RequestHandleAdvice.class,
        ResponseHandleAdvice.class
})
public class ControllerAdviceAutoConfiguration {

}
