package com.lemon.framework.auth.shiro.redis;

import com.lemon.framework.cache.redis.JacksonRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 名称：Shiro redis缓存管理器<br/>
 * 描述：<br/>
 * 1. 使用Json存储缓存信息<br/>
 * 2. 可以通过配置更改缓存默认行为（默认1小时过期）<br/>
 *
 * @author hai-zhang
 * @since 2020/5/22
 */
@Slf4j
public class RedisShiroCacheManager implements CacheManager {

    private final JacksonRedisTemplate redisTemplate;
    private final RedisShiroManager redisManager;

    private final ConcurrentMap<String, Cache<String, ?>> caches = new ConcurrentHashMap<>();

    private int expire = 1800;
    private String keyPrefix = "shiro-cache:";
    private String principalIdFieldName = "id";

    public RedisShiroCacheManager(RedisShiroManager redisManager) {
        this.redisTemplate = (JacksonRedisTemplate) redisManager.getRedisTemplate();
        this.redisManager = redisManager;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Assert.notNull(redisTemplate, "com.lemon.framework.auth.shiro.redis.ShiroRedisCacheManager.redisTemplate cannot be null.");
        Assert.notNull(redisManager, "com.lemon.framework.auth.shiro.redis.ShiroRedisCacheManager.redisManager cannot be null.");

        Cache<String, ?> cache = this.caches.get(name);
        if (cache == null) {
            // shiro-cache:name:TenantUserAuthorizingRealm.authorizationCache:userId
            cache = new RedisShiroCache<>(redisManager, keyPrefix + name + ":", expire);
            this.caches.put(name, cache);
        }

        return (Cache) cache;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getPrincipalIdFieldName() {
        return principalIdFieldName;
    }

    public void setPrincipalIdFieldName(String principalIdFieldName) {
        this.principalIdFieldName = principalIdFieldName;
    }
}
