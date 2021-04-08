package com.lemon.framework.cache.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/22
 */
public class ByteArrayRedisTemplate extends RedisTemplate<byte[], byte[]> {

    public ByteArrayRedisTemplate() {
        this.setKeySerializer(RedisSerializer.byteArray());
        this.setHashKeySerializer(RedisSerializer.byteArray());
        this.setValueSerializer(RedisSerializer.byteArray());
        this.setHashValueSerializer(RedisSerializer.byteArray());
    }

    public ByteArrayRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

}
