package com.lemon.boot.autoconfigure.sequence;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.lemon.boot.autoconfigure.sequence.properties.SequenceProperties;
import com.lemon.framework.db.sequence.MybatisPlusCustomIdGenerator;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * <b>名称：ID自动生成器配置类</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SequenceProperties.class)
public class SequenceGeneratorAutoConfiguration {

    @Resource
    private SequenceProperties sequenceProperties;

    @Bean
    public SequenceGenerator sequenceGenerator() {
        LoggerUtils.debug(log, "SequenceGenerator in applicationContext");

        int nodeId = sequenceProperties.getNodeId();
        if (nodeId < 0)
            return new SequenceGenerator();
        else
            return new SequenceGenerator(sequenceProperties.getNodeId());
    }

    /**
     * JPA自定义ID生成器配置类
     */
//    @ConditionalOnClass(IdentityGenerator.class)
//    public static class JpaCustomIdGeneratorAutoConfiguration {
//
//        @Autowired
//        private SequenceGenerator sequenceGenerator;
//
//        @Bean
//        public IdentityGenerator identityGenerator() {
//            return new JpaCustomIdGenerator(sequenceGenerator);
//        }
//    }

    /**
     * MybatisPlus自定义ID生成器配置类
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(IdentifierGenerator.class)
    public static class MybatisPlusCustomIdGeneratorAutoConfiguration {

        private final SequenceGenerator sequenceGenerator;

        public MybatisPlusCustomIdGeneratorAutoConfiguration(SequenceGenerator sequenceGenerator) {
            this.sequenceGenerator = sequenceGenerator;
        }

        /**
         * Mybatis-Plus的ID生成器自动注入
         */
        @Bean
        public IdentifierGenerator identifierGenerator() {
            LoggerUtils.debug(log, "Mybatis-Plus identifierGenerator in applicationContext");
            return new MybatisPlusCustomIdGenerator(sequenceGenerator);
        }
    }

}
