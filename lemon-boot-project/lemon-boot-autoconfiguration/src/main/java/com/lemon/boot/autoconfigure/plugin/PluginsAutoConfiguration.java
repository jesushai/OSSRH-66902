package com.lemon.boot.autoconfigure.plugin;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.lemon.boot.autoconfigure.commons.LocaleAutoConfiguration;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.SchemaQlProperties;
import com.lemon.schemaql.engine.SchemaQlController;
import com.lemon.schemaql.engine.SchemaQlEngineConfiguration;
import com.lemon.schemaql.engine.aspect.DynamicDatasourceAnnotationAdvisor;
import com.lemon.schemaql.engine.aspect.DynamicDatasourceAnnotationInterceptor;
import com.lemon.schemaql.engine.cache.EntityCacheManager;
import com.lemon.schemaql.engine.cache.EntityCacheRedisson;
import com.lemon.schemaql.engine.service.SchemaQlMutationService;
import com.lemon.schemaql.engine.service.SchemaQlQueryService;
import com.lemon.schemaql.engine.validation.InputValidator;
import com.lemon.schemaql.engine.validation.SimpleValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ConfigurationState;

/**
 * 名称：自动装载插件<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/28
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class PluginsAutoConfiguration {

    @Configuration
    @ConditionalOnClass(SchemaQlEngineConfiguration.class)
    @EnableConfigurationProperties(SchemaQlProperties.class)
    @Import({SchemaQlController.class, SchemaQlQueryService.class, SchemaQlMutationService.class})
    @RequiredArgsConstructor
    public static class SchemaQlPluginAutoConfiguration {

        private final SchemaQlProperties schemaQlProperties;

        @Bean(initMethod = "init")
        @ConditionalOnMissingBean
        public SchemaQlEngineConfiguration schemaQlEngineConfiguration() {
            return new SchemaQlEngineConfiguration(schemaQlProperties.getProjectJsonFile());
        }

        /**
         * 动态数据源切片
         *
         * @param dsProcessor 动态数据源处理链
         * @return DynamicDatasourceAnnotationAdvisor
         */
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(DsProcessor.class)
        @ConditionalOnProperty(prefix = "zh.plugin.schemaql", name = "dynamic-datasource", havingValue = "true")
        public DynamicDatasourceAnnotationAdvisor dynamicDatasourceAnnotationAdvisor(DsProcessor dsProcessor) {
            DynamicDatasourceAnnotationInterceptor interceptor = new DynamicDatasourceAnnotationInterceptor();
            interceptor.setDsProcessor(dsProcessor);
            DynamicDatasourceAnnotationAdvisor advisor = new DynamicDatasourceAnnotationAdvisor(interceptor);
            advisor.setOrder(Integer.MIN_VALUE);
            return advisor;
        }


        /**
         * 国际化验证
         */
        @Configuration
        @AutoConfigureAfter(LocaleAutoConfiguration.class)
        @ConditionalOnBean(name = "messageSource")
        public static class LocaleValidationAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean
            public ValidatorFactory validatorFactory(SchemaQlProperties schemaQlProperties) {
                LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
                validatorFactoryBean.setValidationMessageSource(validationMessageSource(schemaQlProperties));
                return validatorFactoryBean;
            }

            private ResourceBundleMessageSource validationMessageSource(SchemaQlProperties schemaQlProperties) {
                if (!StringUtils.hasText(schemaQlProperties.getValidationMessages())) {
                    throw new RuntimeException("Validation message source configuration must not be null.");
                }

                ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
                messageSource.setBasename(schemaQlProperties.getValidationMessages());

                // 注册到处理器中，非primary消息源
                MessageSourceHandler.buildMessageSourceHandler(
                        BeanNameConstants.VALIDATION_MESSAGE_SOURCE, messageSource);

                LoggerUtils.debug(log,
                        "Validation ResourceBundleMessageSource in applicationContext, name is [{}]",
                        BeanNameConstants.VALIDATION_MESSAGE_SOURCE);

                return messageSource;
            }
        }

        /**
         * 非国际化验证
         */
        @Configuration
        @AutoConfigureAfter(LocaleAutoConfiguration.class)
        @ConditionalOnMissingBean(name = "messageSource")
        public static class SimpleValidationAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean
            public ValidatorFactory validatorFactory() {
                javax.validation.Configuration<?> configuration = Validation.byDefaultProvider().configure();
                return new SimpleValidatorFactory((ConfigurationState) configuration);
            }
        }

        /**
         * 自定义验证器
         */
        @Configuration
        @AutoConfigureAfter({LocaleValidationAutoConfiguration.class, SimpleValidationAutoConfiguration.class})
//    @DependsOn("validatorFactory")
        public static class InputValidatorAutoConfiguration {

            @Bean
            @ConditionalOnMissingBean
            @ConditionalOnBean(name = "messageSource")
            public InputValidator inputValidator(ValidatorFactory validatorFactory) {
                return new InputValidator(validatorFactory);
            }
        }

        @Configuration
        @ConditionalOnBean(RedissonClient.class)
        public static class SchemaQlCacheAutoConfiguration {

            @Bean
            public EntityCacheRedisson entityCacheRedisson(RedissonClient redisson) {
                return new EntityCacheRedisson(redisson);
            }

            @Bean
            public EntityCacheManager entityCacheManager(RedissonClient redisson) {
                return new EntityCacheManager(entityCacheRedisson(redisson));
            }
        }
    }

}
