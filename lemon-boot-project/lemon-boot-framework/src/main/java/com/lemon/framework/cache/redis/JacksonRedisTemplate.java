package com.lemon.framework.cache.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/22
 */
public class JacksonRedisTemplate extends RedisTemplate<String, Object> {

    protected void setSerializerMapper(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Object> serializer = jackson2JsonRedisSerializer(objectMapper);
        // key采用 String的序列化方式
        this.setKeySerializer(StringRedisSerializer.UTF_8);
        // hash的 key也采用 String的序列化方式
        this.setHashKeySerializer(StringRedisSerializer.UTF_8);
        // value序列化方式采用 jackson
        this.setValueSerializer(serializer);
        // hash的 value序列化方式采用 jackson
        this.setHashValueSerializer(serializer);
    }

    public JacksonRedisTemplate() {
        setSerializerMapper(null);
    }

    public JacksonRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        this.setConnectionFactory(connectionFactory);
        this.afterPropertiesSet();
    }

    /**
     * jackson序列化
     *
     * @param objectMapper 如果为null则使用默认规则
     * @return Jackson2JsonRedisSerializer
     */
    public static Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 解决查询缓存转换异常的问题
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        return jackson2JsonRedisSerializer;
    }

}
