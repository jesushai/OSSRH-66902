package shiro;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import shiro.db.entity.SysRole;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-22
 */
public class JacksonSerializerTest {
    @Test
    public void jacksonTest() {
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
}
