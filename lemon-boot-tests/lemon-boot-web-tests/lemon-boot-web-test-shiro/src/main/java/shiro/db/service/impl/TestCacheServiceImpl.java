package shiro.db.service.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import shiro.db.service.ITestCacheService;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/28
 */
@Service
@CacheConfig(cacheNames = "cache1")
public class TestCacheServiceImpl implements ITestCacheService {

    @Cacheable(key = "'foo'")
    public String foo() {
        System.out.println("=================foo no cache!");
        return "FOO";
    }
}
