package com.lemon.boot.autoconfigure.advice;

import com.lemon.framework.core.advice.RequestHandleAdviceProcessor;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019/10/10
 */
@Slf4j
@ControllerAdvice
public class RequestHandleAdvice implements RequestBodyAdvice {

    private Collection<RequestHandleAdviceProcessor> processors;

    @Autowired
    public RequestHandleAdvice(Collection<RequestHandleAdviceProcessor> processors) {
        if (CollectionUtils.isNotEmpty(processors)) {
            LoggerUtils.debug(log, "加载RequestHandleAdvice：{}个", processors.size());
            this.processors = processors.stream()
                    .sorted(Comparator.comparing(RequestHandleAdviceProcessor::getOrder))
                    .collect(Collectors.toList());
        } else {
            this.processors = CollectionUtils.emptyCollection();
            LoggerUtils.debug(log, "无RequestHandleAdvice加载");
        }
    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type type, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        return CollectionUtils.isNotEmpty(processors);
    }

    @Override
    @NonNull
    public HttpInputMessage beforeBodyRead(@NonNull HttpInputMessage httpInputMessage, @NonNull MethodParameter methodParameter, @NonNull Type type, @NonNull Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        for (RequestHandleAdviceProcessor processor : this.processors) {
            if (processor.before() && processor.supports(methodParameter, type, aClass)) {
                processor.beforeBodyRead(httpInputMessage, methodParameter, type, aClass);
            }
        }
        return httpInputMessage;
    }

    @Override
    @NonNull
    public Object afterBodyRead(@NonNull Object o, @NonNull HttpInputMessage httpInputMessage, @NonNull MethodParameter methodParameter, @NonNull Type type, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        for (RequestHandleAdviceProcessor processor : this.processors) {
            if (processor.after() && processor.supports(methodParameter, type, aClass)) {
                processor.afterBodyRead(o, httpInputMessage, methodParameter, type, aClass);
            }
        }
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, @NonNull HttpInputMessage httpInputMessage, @NonNull MethodParameter methodParameter, @NonNull Type type, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        for (RequestHandleAdviceProcessor processor : this.processors) {
            if (processor.empty() && processor.supports(methodParameter, type, aClass)) {
                processor.handleEmptyBody(o, httpInputMessage, methodParameter, type, aClass);
            }
        }
        return o;
    }
}
