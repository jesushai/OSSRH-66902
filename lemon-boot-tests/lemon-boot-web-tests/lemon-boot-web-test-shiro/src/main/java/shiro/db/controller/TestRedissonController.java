package shiro.db.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shiro.db.entity.SysAdmin;
import shiro.db.service.TestRedissonService;

import javax.annotation.Resource;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/10
 */
@RestController
@RequestMapping("/test/redisson")
public class TestRedissonController {

    @Resource
    private TestRedissonService testRedissonService;

    @GetMapping("/lock1")
    public String testLock1() {
        SysAdmin user = new SysAdmin();
        user.setId(100L);
        return testRedissonService.testLock1(user);
    }
}
