package com.lemon.framework.cache.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
public interface RedisSerializer<T> {
    byte[] serialize(T var1) throws SerializationException;

    T deserialize(byte[] var1) throws SerializationException;
}
