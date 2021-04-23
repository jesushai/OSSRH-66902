package shiro;

import com.lemon.framework.cache.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-14
 */
@SpringBootTest
@Slf4j
class TestRedis {

    @Autowired
    private RedisService redisService;

    @Test
    void testRedis() {
        boolean b = redisService.set("kkk", "kkkkk");
        b = redisService.expire("kkk", 10L);
        Object o = redisService.get("kkk");
        System.out.println(o);
        Assertions.assertEquals(o, "kkkkk");
    }

}
