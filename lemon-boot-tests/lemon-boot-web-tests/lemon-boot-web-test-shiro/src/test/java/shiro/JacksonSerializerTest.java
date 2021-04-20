package shiro;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import shiro.db.entity.SysRole;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-22
 */
class JacksonSerializerTest {

    @Test
    void jacksonTest() {
        Jackson2JsonRedisSerializer<Object> jjrs = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        jjrs.setObjectMapper(om);

        SysRole role = new SysRole();
        role.setName("123904");
        byte[] b = jjrs.serialize(role);
        System.out.println(new String(b));
        role = (SysRole) jjrs.deserialize(b);
        System.out.println(role);
    }

    /**
     * 注意：RedisCacheConfiguration必须接收方法的返回值，并赋值。
     */
    @Test
    void testCacheKeyPrefix() {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.prefixCacheNameWith("pre");
        String cacheName = configuration.getKeyPrefixFor("CacheName");
        System.out.println(cacheName);
        Assertions.assertEquals("preCacheName::", cacheName);
    }

}
