package com.lemon.boot.autoconfigure.cache.redis;

import com.lemon.boot.autoconfigure.cache.redis.properties.CustomRedisCacheProperties;
import com.lemon.framework.cache.CommonKeyGenerator;
import com.lemon.framework.cache.redis.AllowUnknownJacksonRedisTemplate;
import com.lemon.framework.cache.redis.ByteArrayRedisTemplate;
import com.lemon.framework.cache.redis.JacksonRedisTemplate;
import com.lemon.framework.cache.redis.StringByteArrayRedisTemplate;
import com.lemon.framework.cache.redisson.DistributedLocker;
import com.lemon.framework.cache.redisson.RedisLocker;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;

import java.util.HashMap;
import java.util.Map;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/1
 */
@Slf4j
@Configuration(value = "redisAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass({RedissonClient.class, RedisOperations.class, org.redisson.spring.starter.RedissonAutoConfiguration.class})
public class RedissonAutoConfiguration {

    public RedissonAutoConfiguration() {
        LoggerUtils.debug(log, "Register RedissonAutoConfiguration.");
    }

    /**
     * Redisson Spring data
     */
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
    public DistributedLocker distributedLocker(RedissonClient redisson) {
        // 默认等待100秒，10秒后自动释放
        return new RedisLocker(redisson, 10, 100);
    }

    @EnableCaching
    @Configuration
    @ConditionalOnClass(RedisOperations.class)
    @EnableConfigurationProperties({CacheProperties.class, CustomRedisCacheProperties.class})
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis")
    public static class RedisCacheAutoConfiguration {

        @Bean(BeanNameConstants.COMMON_KEY_GENERATOR)
        @ConditionalOnMissingBean
        public KeyGenerator commonKeyGenerator() {
            return new CommonKeyGenerator();
        }

        /**
         * redisson集成spring cache
         */
        @Bean
        @ConditionalOnMissingBean(name = "cacheManager")
        public RedissonSpringCacheManager cacheManager(RedissonClient redissonClient,
                                                       CustomRedisCacheProperties redisCacheProperties) {
            Map<String, CacheConfig> config = new HashMap<>(redisCacheProperties.getNames().size());
            if (redisCacheProperties.getNames() != null) {
                redisCacheProperties.getNames().forEach((cacheName, redisProperties) -> {
                    // 过期时间与最长空闲时间，这里都是一个
                    // 如果都是0则永久缓存
                    long ttl = redisProperties.getTimeToLive() == null ? 0L : redisProperties.getTimeToLive().toMillis();
                    long mit = redisProperties.getMaxIdleTime() == null ? 0L : redisProperties.getMaxIdleTime().toMillis();
                    config.put(cacheName, new CacheConfig(ttl, mit));
                });
            }
            return new RedissonSpringCacheManager(redissonClient, config);
        }
    }
}
