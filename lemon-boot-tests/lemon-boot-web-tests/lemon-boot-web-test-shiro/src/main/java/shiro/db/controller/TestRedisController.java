package shiro.db.controller;

import com.lemon.framework.cache.redis.JacksonRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shiro.db.service.ITestCacheService;

import javax.annotation.Resource;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/25
 */
@RestController
@RequestMapping("/test/redis")
public class TestRedisController {

    @Resource
    private JacksonRedisTemplate redisTemplate;

    @Resource
    private ITestCacheService testCacheService;

    @GetMapping("/put")
    public String setKeyValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        return value;
    }

    @GetMapping("/get")
    public String getValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/foo")
    public String foo() {
        return testCacheService.foo();
    }
}
