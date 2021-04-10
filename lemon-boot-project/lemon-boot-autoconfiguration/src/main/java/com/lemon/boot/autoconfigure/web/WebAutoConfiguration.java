package com.lemon.boot.autoconfigure.web;

import com.lemon.boot.autoconfigure.web.properties.CorsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
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
