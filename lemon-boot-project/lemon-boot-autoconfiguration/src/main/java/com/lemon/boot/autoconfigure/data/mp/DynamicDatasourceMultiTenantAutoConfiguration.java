package com.lemon.boot.autoconfigure.data.mp;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.lemon.framework.db.mp.dynamic.DsHeaderTenantProcessor;
import com.lemon.framework.db.mp.dynamic.DsPrincipalTenantProcessor;
import com.lemon.framework.db.mp.dynamic.DsSessionTenantProcessor;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 名称：多租户多数据源自动配置<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020-5-12
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class DynamicDatasourceMultiTenantAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(DsProcessor.class)
    public static class DynamicDatasourceProcessorAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DsProcessor dsProcessor() {
            DsPrincipalTenantProcessor principalTenantProcessor = new DsPrincipalTenantProcessor();
            DsHeaderTenantProcessor headerTenantProcessor = new DsHeaderTenantProcessor();
            DsSessionTenantProcessor sessionProcessor = new DsSessionTenantProcessor();
            DsSpelExpressionProcessor spelExpressionProcessor = new DsSpelExpressionProcessor();

            // 顺序：principal->header->session->expression
            principalTenantProcessor.setNextProcessor(headerTenantProcessor);
            headerTenantProcessor.setNextProcessor(sessionProcessor);
            sessionProcessor.setNextProcessor(spelExpressionProcessor);

            LoggerUtils.debug(log, "DsProcessor in applicationContext.");
            return principalTenantProcessor;
        }
    }
}
