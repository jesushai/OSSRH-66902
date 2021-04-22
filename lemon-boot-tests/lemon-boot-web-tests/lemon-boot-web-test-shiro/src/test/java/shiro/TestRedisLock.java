package shiro;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import shiro.db.entity.SysAdmin;
import shiro.db.service.TestRedissonService;

import javax.annotation.Resource;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
@SpringBootTest
public class TestRedisLock {

    @Resource
    private TestRedissonService testRedissonService;

    @Test
    public void testLock1() {
        SysAdmin user = new SysAdmin();
        user.setId(100L);
        String result = testRedissonService.testLock1(user);
        Assertions.assertEquals("K", result);
    }

}
