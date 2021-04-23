package com.lemon.framework.core.advice;

import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.JacksonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;

/**
 * 名称：包壳子<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019-10-10
 */
public class ResponseByResultAdviceProcessor implements ResponseHandleAdviceProcessor {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            @NonNull Class<? extends HttpMessageConverter<?>> aClass) {

        if (methodParameter.getMethod() != null) {
            // WebFlux不处理
            Class<?> returnType = methodParameter.getMethod().getReturnType();

            if (returnType != null) {
                String typeName = returnType.getSimpleName();
                return !"Flux".equals(typeName) && !"Mono".equals(typeName);
            }
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object objectResult,
                                  @NonNull MethodParameter methodParameter,
                                  @NonNull MediaType mediaType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> aClass,
                                  @NonNull ServerHttpRequest serverHttpRequest,
                                  @NonNull ServerHttpResponse serverHttpResponse) {
        Result result;
        if (objectResult != null) {
            if (objectResult instanceof Result) {
                result = (Result) objectResult;
            } else {
                result = Result.ok().data(objectResult);
                // 字符串需要特殊处理一下，否则会报Result无法转为String的异常
                // 这里将最终返回的结果提前转为json字符串
                if (objectResult instanceof String) {
                    return JacksonUtils.toJson(result);
                }
            }
        } else {
            result = Result.ok();
        }
        return result;
    }

    /**
     * 最低优先级执行
     *
     * @return LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
