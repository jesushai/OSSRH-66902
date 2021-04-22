package com.lemon.boot.autoconfigure.web;

import com.lemon.boot.autoconfigure.web.properties.CorsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/15
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CorsProperties.class)
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "zh.http.cors.enable", havingValue = "true")
    public CorsFilter corsFilter(CorsProperties properties) {
        return new CorsFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public AppContextFilter appContextFilter() {
        return new AppContextFilter();
    }
}
