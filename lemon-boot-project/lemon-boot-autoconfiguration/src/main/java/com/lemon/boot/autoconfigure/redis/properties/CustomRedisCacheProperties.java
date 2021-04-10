package com.lemon.boot.autoconfigure.redis.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Map;

/**
 * <b>名称：自定义redis缓存策略</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/27
 */
@Data
@ConfigurationProperties(prefix = "zh.cache")
public class CustomRedisCacheProperties {

    private Map<String, Redisson> names;

    @SuppressWarnings("all")
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Redisson extends CacheProperties.Redis {
        /**
         * 最大空闲存活期（不超过TTL）<br/>
         * 仅限Redisson客户端
         */
        private Duration maxIdleTime;
    }

}
