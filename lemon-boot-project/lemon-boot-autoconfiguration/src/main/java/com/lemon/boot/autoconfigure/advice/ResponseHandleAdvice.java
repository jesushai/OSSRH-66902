package com.lemon.boot.autoconfigure.advice;

import com.lemon.framework.core.advice.ResponseHandleAdviceProcessor;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/10/8
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ResponseHandleAdvice implements ResponseBodyAdvice<Object> {

//    @Resource
//    private DefaultEventPublisher eventPublisher;

    private final Collection<ResponseHandleAdviceProcessor> processors;

    @Autowired
    public ResponseHandleAdvice(Collection<ResponseHandleAdviceProcessor> processors) {
        if (CollectionUtils.isNotEmpty(processors)) {
            LoggerUtils.debug(log, "Loading ResponseHandleAdvice: {}.", processors.size());
            this.processors = processors.stream()
                    .sorted(Comparator.comparing(ResponseHandleAdviceProcessor::getOrder))
                    .collect(Collectors.toList());
        } else {
            this.processors = CollectionUtils.emptyCollection();
            LoggerUtils.debug(log, "No ResponseHandleAdvice loading.");
        }
    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object objectResult, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class<? extends HttpMessageConverter<?>> aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
        for (ResponseHandleAdviceProcessor processor : this.processors) {
            if (processor.supports(methodParameter, aClass)) {
                objectResult = processor.beforeBodyWrite(objectResult, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
            }
        }
        // 发布调用完结事件
//        eventPublisher.publish(new DomainEventSupport(result, ""));
        return objectResult;
    }

}
