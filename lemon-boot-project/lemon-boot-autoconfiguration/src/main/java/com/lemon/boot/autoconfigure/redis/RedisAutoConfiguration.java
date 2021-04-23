package com.lemon.boot.autoconfigure.redis;

import com.lemon.boot.autoconfigure.cache.redis.properties.CustomRedisCacheProperties;
import com.lemon.framework.cache.CommonKeyGenerator;
import com.lemon.framework.cache.redis.*;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
@Slf4j
@Configuration(value = "redisAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnMissingClass("org.redisson.api.RedissonClient")
@ConditionalOnClass(RedisOperations.class)
public class RedisAutoConfiguration {

    public RedisAutoConfiguration() {
        LoggerUtils.debug(log, "Register RedisAutoConfiguration.");
    }

    @Bean(BeanNameConstants.JACKSON_REDIS_TEMPLATE)
    @ConditionalOnMissingBean(name = BeanNameConstants.JACKSON_REDIS_TEMPLATE)
    public JacksonRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        LoggerUtils.debug(log, "JacksonRedisTemplate in applicationContext, beanName=(redisTemplate).");
        return new JacksonRedisTemplate(factory);
    }

    @Bean(BeanNameConstants.BYTE_ARRAY_REDIS_TEMPLATE)
    @ConditionalOnMissingBean(name = BeanNameConstants.BYTE_ARRAY_REDIS_TEMPLATE)
    public ByteArrayRedisTemplate byteArrayRedisTemplate(RedisConnectionFactory factory) {
        LoggerUtils.debug(log, "ByteArrayRedisTemplate in applicationContext, beanName=(byteArrayRedisTemplate).");
        return new ByteArrayRedisTemplate(factory);
    }

    @Bean(BeanNameConstants.STRING_BYTE_ARRAY_REDIS_TEMPLATE)
    @ConditionalOnMissingBean(name = BeanNameConstants.STRING_BYTE_ARRAY_REDIS_TEMPLATE)
    public StringByteArrayRedisTemplate stringByteArrayRedisTemplate(RedisConnectionFactory factory) {
        LoggerUtils.debug(log, "StringByteArrayRedisTemplate in applicationContext, beanName=(stringByteArrayRedisTemplate).");
        return new StringByteArrayRedisTemplate(factory);
    }

    @Bean(BeanNameConstants.ALLOW_UNKNOWN_JACKSON_REDIS_TEMPLATE)
    @ConditionalOnMissingBean(name = BeanNameConstants.ALLOW_UNKNOWN_JACKSON_REDIS_TEMPLATE)
    public AllowUnknownJacksonRedisTemplate allowUnknownJacksonRedisTemplate(RedisConnectionFactory factory) {
        LoggerUtils.debug(log, "AllowUnknownJacksonRedisTemplate in applicationContext, beanName=(allowUnknownJacksonRedisTemplate).");
        return new AllowUnknownJacksonRedisTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisService redisService(RedisConnectionFactory factory) {
        return new RedisService(redisTemplate(factory));
    }

    /**
     * Spring boot cache
     */
    @EnableCaching
    @Configuration
    @EnableConfigurationProperties({CacheProperties.class, CustomRedisCacheProperties.class})
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
    public static class RedisCacheAutoConfiguration {

        @Resource
        private CacheProperties cacheProperties;

        @Bean(BeanNameConstants.COMMON_KEY_GENERATOR)
        @ConditionalOnMissingBean
        public KeyGenerator commonKeyGenerator() {
            return new CommonKeyGenerator();
        }

        /**
         * 非redisson的redis客户端管理spring cache
         *
         * @param factory              RedisConnectionFactory
         * @param redisCacheProperties 自定义redis缓存策略
         * @return RedisCacheManager
         */
        @Bean
        @ConditionalOnMissingBean(name = "cacheManager")
        @ConditionalOnMissingClass("org.redisson.api.RedissonClient")
        public RedisCacheManager cacheManager(RedisConnectionFactory factory,
                                              CustomRedisCacheProperties redisCacheProperties) {
            // 1. 默认的缓存配置
            // 解决查询缓存转换异常的问题
            // 配置序列化（解决乱码的问题）
            RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(StringRedisSerializer.UTF_8))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(JacksonRedisTemplate.jackson2JsonRedisSerializer(null)));

            // 缓存生存时间，默认是不过期的
            CacheProperties.Redis redis = cacheProperties.getRedis();
            if (redis.getTimeToLive() != null && redis.getTimeToLive().compareTo(Duration.ZERO) > 0) {
                defaultConfig = defaultConfig.entryTtl(cacheProperties.getRedis().getTimeToLive());
            }
            // 缓存key前缀，默认是带前缀的
            if (cacheProperties.getRedis().isUseKeyPrefix() && StringUtils.hasText(cacheProperties.getRedis().getKeyPrefix())) {
                defaultConfig = defaultConfig.prefixCacheNameWith(cacheProperties.getRedis().getKeyPrefix());
            }
            // 是否缓存null值，默认是缓存的
            if (!cacheProperties.getRedis().isCacheNullValues()) {
                defaultConfig = defaultConfig.disableCachingNullValues();
            }

            // 2. RedisCacheManagerBuilder
            RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                    .fromCacheWriter(RedisCacheWriter.nonLockingRedisCacheWriter(factory))
//                    .fromCacheWriter(RedisCacheWriter.lockingRedisCacheWriter(factory))
//                    .transactionAware()
                    .cacheDefaults(defaultConfig);

            // 3. 不同缓存名不同的过期策略
            Map<String, RedisCacheConfiguration> configs = new HashMap<>();
            final RedisCacheConfiguration finalConfig = defaultConfig;

            Optional.ofNullable(redisCacheProperties)
                    .map(CustomRedisCacheProperties::getNames)
                    .ifPresent(cacheMap -> cacheMap.forEach((cacheName, redisProperties) -> {
                        RedisCacheConfiguration cfg = handleRedisCacheConfiguration(redisProperties, finalConfig);
                        configs.put(cacheName, cfg);
                    }));

            if (configs.size() > 0) {
                builder.withInitialCacheConfigurations(configs);
            }

            LoggerUtils.debug(log, "RedisCacheManager in applicationContext, beanName=(cacheManager).");

            return builder.build();
        }

        private RedisCacheConfiguration handleRedisCacheConfiguration(CacheProperties.Redis redisProperties,
                                                                      RedisCacheConfiguration config) {
            if (Objects.isNull(redisProperties)) {
                return config;
            }
            if (redisProperties.getTimeToLive() != null) {
                config = config.entryTtl(redisProperties.getTimeToLive());
            }
            if (redisProperties.getKeyPrefix() != null) {
                // 自定义cache的名字
                // prefix:cacheName:key
                config = config.computePrefixWith(cacheName -> redisProperties.getKeyPrefix() + ':' + cacheName + ':');
            }
            if (!redisProperties.isCacheNullValues()) {
                config = config.disableCachingNullValues();
            }
            if (!redisProperties.isUseKeyPrefix()) {
                config = config.disableKeyPrefix();
            }
            return config;
        }

    }
}
