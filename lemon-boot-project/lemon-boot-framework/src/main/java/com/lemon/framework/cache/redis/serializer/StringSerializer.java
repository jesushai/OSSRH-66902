package com.lemon.framework.cache.redis.serializer;

import org.springframework.data.redis.serializer.SerializationException;

import java.io.UnsupportedEncodingException;

/**
 * 名称：字符串序列化<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/23
 */
public class StringSerializer implements RedisSerializer<String> {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private String charset = DEFAULT_CHARSET;

    public byte[] serialize(String s) throws SerializationException {
        try {
            return s == null ? null : s.getBytes(this.charset);
        } catch (UnsupportedEncodingException var3) {
            throw new SerializationException("Serialize error, string=" + s, var3);
        }
    }

    public String deserialize(byte[] bytes) throws SerializationException {
        try {
            return bytes == null ? null : new String(bytes, this.charset);
        } catch (UnsupportedEncodingException var3) {
            throw new SerializationException("Deserialize error", var3);
        }
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
