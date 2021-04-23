package com.lemon.framework.cache.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;

/**
 * 名称：Redis序列化<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
public interface RedisSerializer<T> {
    byte[] serialize(T var1) throws SerializationException;

    T deserialize(byte[] var1) throws SerializationException;
}
