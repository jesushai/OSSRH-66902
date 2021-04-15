package shiro.db.service;

import com.lemon.framework.auth.model.User;

public interface TestRedissonService {

    String testLock1(User user);
}
