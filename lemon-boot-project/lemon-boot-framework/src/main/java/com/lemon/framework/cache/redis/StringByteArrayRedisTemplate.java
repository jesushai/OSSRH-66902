package com.lemon.framework.cache.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
public class StringByteArrayRedisTemplate extends RedisTemplate<String, byte[]> {

    public StringByteArrayRedisTemplate() {
        this.setKeySerializer(StringRedisSerializer.UTF_8);
        this.setHashKeySerializer(StringRedisSerializer.UTF_8);
        this.setValueSerializer(RedisSerializer.byteArray());
        this.setHashValueSerializer(RedisSerializer.byteArray());
    }

    public StringByteArrayRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

}
