package com.lemon.boot.autoconfigure.advice;

import com.lemon.framework.core.advice.ResponseByResultAdviceProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 名称：<br/>
 * 描述：<br/>
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
