package com.lemon.framework.auth.shiro.redis;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.*;

/**
 * <b>名称：Shiro redis缓存类</b><br/>
 * <b>描述：</b><br/>
 * 使用redis作为shiro授权主体的缓存<br/>
 * 过期时间与key前缀可以自由配置并通过ShiroRedisCacheManager传入
 *
 * @author hai-zhang
 * @since 2020/5/22
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedisShiroCache<V> implements Cache<String, V> {

    private final RedisShiroManager redisManager;

    /**
     * 单位：秒
     */
    private final int expire;
    private final String keyPrefix;

    public RedisShiroCache(RedisShiroManager redisManager, String keyPrefix, int expire) {
        this.redisManager = redisManager;
        this.expire = expire;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public V get(String k) throws CacheException {
        if (k == null) {
            return null;
        } else {
            try {
                String key = this.getRedisCacheKey(k);
                return (V) redisManager.get(key);
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
    }

    private String getRedisCacheKey(String key) {
        if (key == null) {
            return null;
        } else {
            return keyPrefix + key;
        }
    }

    @Override
    public V put(String k, V v) throws CacheException {
        if (k == null) {
            LoggerUtils.warn(log, "Saving a null key is meaningless, return value directly without call Redis.");
            return v;
        } else {
            try {
                String key = this.getRedisCacheKey(k);
                redisManager.set(key, v, expire);
                return v;
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
    }

    @Override
    public V remove(String k) throws CacheException {
        if (k == null) {
            return null;
        } else {
            try {
                String key = this.getRedisCacheKey(k);
                V previous = (V) redisManager.get(key);
                redisManager.del(key);
                return previous;
            } catch (Exception e) {
                throw new CacheException(e);
            }
        }
    }

    @Override
    public void clear() throws CacheException {
        // 不支持清空所有缓存的操作
        // 应该使用TenantUserAuthorizingRealm.clearCache()方法清空单个主体的缓存
        LoggerUtils.warn(log, "Not Supported clear all shiro session, " +
                "Please use TenantUserAuthorizingRealm.clearCache().");
    }

    @Override
    public int size() {
        Long size = 0L;
        try {
            size = redisManager.size(keyPrefix + '*');
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
        return size.intValue();
    }

    @Override
    public Set<String> keys() {
        Set<String> result = redisManager.keys(keyPrefix + '*');
        return result == null ? new HashSet<>() : result;
    }

    /**
     * 获取前缀key的所有缓存，并返回缓存的对象（不可变集合）
     */
    @Override
    public Collection<V> values() {
        Set<String> keys = this.keys();
        if (keys.size() == 0) {
            return new ArrayList<>();
        } else {
            List<V> result = new ArrayList<>(keys.size());
            keys.forEach(key -> result.add((V) redisManager.get(key)));
            return Collections.unmodifiableList(result);
        }
    }
}
