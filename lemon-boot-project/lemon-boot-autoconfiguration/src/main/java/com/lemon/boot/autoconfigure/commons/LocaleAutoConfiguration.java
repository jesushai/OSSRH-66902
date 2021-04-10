package com.lemon.boot.autoconfigure.commons;

import com.lemon.boot.autoconfigure.commons.properties.AcceptLanguageProperties;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * <b>名称：国际化拦截</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AcceptLanguageProperties.class)
public class LocaleAutoConfiguration {

    @Value(value = "${spring.messages.basename}")
    private String basename;

    @Primary
    @Bean(name = "messageSource")
    @ConditionalOnMissingBean(name = "messageSource")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);

        MessageSourceHandler.buildMessageSourceHandler(
                BeanNameConstants.MESSAGE_SOURCE_HANDLER, messageSource);

        LoggerUtils.debug(log,
                "ResourceBundleMessageSource in applicationContext, name is [{}]",
                BeanNameConstants.MESSAGE_SOURCE_HANDLER);

        return messageSource;
    }

    /**
     * 默认解析器 其中locale表示默认语言
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zh.http.accept-language", name = "type", havingValue = "request-param-lang")
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        log.info("=========================================");
        log.info("= 通过http请求参数控制国际化，默认zh-CN =");
        log.info("=========================================");
        return localeResolver;
    }

    /**
     * 默认拦截器 其中lang表示切换语言的参数名
     */
    @Bean
    @ConditionalOnProperty(prefix = "zh.http.accept-language", name = "type", havingValue = "request-param-lang")
    public WebMvcConfigurer localeInterceptor() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
                localeInterceptor.setParamName("lang");
                registry.addInterceptor(localeInterceptor);
            }
        };
    }
}
