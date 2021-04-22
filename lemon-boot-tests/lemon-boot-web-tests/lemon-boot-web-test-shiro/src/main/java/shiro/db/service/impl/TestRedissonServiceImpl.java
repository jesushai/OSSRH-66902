package shiro.db.service.impl;

import com.lemon.framework.auth.model.User;
import com.lemon.framework.cache.redisson.DistributedLock;
import org.springframework.stereotype.Service;
import shiro.db.service.TestRedissonService;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
@Service
public class TestRedissonServiceImpl implements TestRedissonService {

    @Override
    @DistributedLock(expression = "'test-lock['+#user.id+']'", releaseTime = 2)
    public String testLock1(User user) {
        return "K";
    }
}
