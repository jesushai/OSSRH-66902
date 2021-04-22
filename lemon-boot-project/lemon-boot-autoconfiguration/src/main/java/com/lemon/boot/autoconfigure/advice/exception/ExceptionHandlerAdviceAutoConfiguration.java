package com.lemon.boot.autoconfigure.advice.exception;

import com.lemon.boot.autoconfigure.commons.LocaleAutoConfiguration;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.context.request.WebRequest;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ExceptionHandlerAdviceAutoConfiguration {

    @Configuration
    @ConditionalOnClass(WebRequest.class)
    @ConditionalOnMissingClass("org.springframework.orm.ObjectOptimisticLockingFailureException")
    @AutoConfigureAfter(LocaleAutoConfiguration.class)
    public static class WebRequestExceptionHandlerAdviceAutoConfiguration {

        @Bean("exceptionHandlerAdvice")
        @ConditionalOnMissingBean
        public ExceptionHandlerAdvice exceptionHandlerAdvice() {
            LoggerUtils.debug(log, "ExceptionHandlerAdvice init applicationContext");
            return new ExceptionHandlerAdvice();
        }
    }

    @Configuration
    @ConditionalOnClass(ObjectOptimisticLockingFailureException.class)
    @ConditionalOnMissingBean(name = "exceptionHandlerAdvice")
    @AutoConfigureAfter(LocaleAutoConfiguration.class)
    public static class JpaExceptionHandlerAdviceAutoConfiguration {

        @Bean("exceptionHandlerAdvice")
        @ConditionalOnMissingBean
        public ExceptionHandlerJpaAdvice exceptionHandlerJpaAdvice() {
            LoggerUtils.debug(log, "ExceptionHandlerJpaAdvice init applicationContext");
            return new ExceptionHandlerJpaAdvice();
        }
    }
}
