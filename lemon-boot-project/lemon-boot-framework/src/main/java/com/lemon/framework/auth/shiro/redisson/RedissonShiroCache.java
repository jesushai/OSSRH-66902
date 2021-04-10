package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.NullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@Slf4j
public class RedissonShiroCache<K, V> implements Cache<K, V> {

    private RMapCache<K, Object> mapCache;

    private final RMap<K, Object> map;

    private CacheConfig config;

    private final boolean allowNullValues;

    private final AtomicLong hits = new AtomicLong();

    private final AtomicLong misses = new AtomicLong();

    public RedissonShiroCache(RMapCache<K, Object> mapCache, CacheConfig config, boolean allowNullValues) {
        this.mapCache = mapCache;
        this.map = mapCache;
        this.config = config;
        this.allowNullValues = allowNullValues;
    }

    public RedissonShiroCache(RMap<K, Object> map, boolean allowNullValues) {
        this.map = map;
        this.allowNullValues = allowNullValues;
    }

    @Override
    public V get(K key) throws CacheException {
        LoggerUtils.debug(log, "Get cache by key [{}].", key);
        if (key == null) {
            return null;
        } else {
            Object value = this.map.get(key);
            if (value == null) {
                addCacheMiss();
            } else {
                addCacheHit();
            }
            return fromStoreValue(value);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        LoggerUtils.debug(log, "Put to cache [{}, {}].", key, value);
        Object previous;
        if (!allowNullValues && value == null) {
            if (mapCache != null) {
                previous = mapCache.remove(key);
            } else {
                previous = map.remove(key);
            }
            return fromStoreValue(previous);
        }

        Object val = toStoreValue(value);
        if (mapCache != null) {
            previous = mapCache.put(key, val, config.getTTL(), TimeUnit.MILLISECONDS,
                    config.getMaxIdleTime(), TimeUnit.MILLISECONDS);
        } else {
            previous = map.put(key, val);
        }
        return fromStoreValue(previous);
    }

    @SuppressWarnings("unchecked")
    public void fastPut(K key, V value) throws CacheException {
        if (!allowNullValues && value == null) {
            if (mapCache != null) {
                mapCache.fastRemove(key);
            } else {
                map.fastRemove(key);
            }
            return;
        }

        Object val = toStoreValue(value);
        if (mapCache != null) {
            mapCache.fastPut(key, val, config.getTTL(), TimeUnit.MILLISECONDS,
                    config.getMaxIdleTime(), TimeUnit.MILLISECONDS);
        } else {
            map.fastPut(key, val);
        }
    }

    public V putIfAbsent(K key, V value) throws CacheException {
        Object previous;
        if (!allowNullValues && value == null) {
            if (mapCache != null) {
                previous = mapCache.get(key);
            } else {
                previous = map.get(key);
            }
            return fromStoreValue(previous);
        }

        Object val = toStoreValue(value);
        if (mapCache != null) {
            previous = mapCache.putIfAbsent(key, val, config.getTTL(), TimeUnit.MILLISECONDS,
                    config.getMaxIdleTime(), TimeUnit.MILLISECONDS);
        } else {
            previous = map.putIfAbsent(key, val);
        }
        return fromStoreValue(previous);
    }

    public boolean fastPutIfAbsent(K key, V value) throws CacheException {
        if (!allowNullValues && value == null) {
            return false;
        }

        Object val = toStoreValue(value);
        if (mapCache != null) {
            return mapCache.fastPutIfAbsent(key, val, config.getTTL(), TimeUnit.MILLISECONDS,
                    config.getMaxIdleTime(), TimeUnit.MILLISECONDS);
        } else {
            return map.fastPutIfAbsent(key, val);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        LoggerUtils.debug(log, "Remove cache [{}].", key);
        Object previous = this.map.remove(key);
        return fromStoreValue(previous);
    }

    @SafeVarargs
    public final long fastRemove(K... keys) {
        return this.map.fastRemove(keys);
    }

    @Override
    public void clear() throws CacheException {
        // 不支持清空所有缓存的操作
        // 应该使用TenantUserAuthorizingRealm.clearCache()方法清空单个主体的缓存
        LoggerUtils.warn(log, "Not Supported clear all shiro session, " +
                "Please use TenantUserAuthorizingRealm.clearCache().");
//        this.map.clear();
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Set<K> keys() {
        LoggerUtils.debug(log, "Read all key set.");
        return this.map.readAllKeySet();
    }

    @Override
    public Collection<V> values() {
        Collection<Object> innerValues = this.map.readAllValues();
        Collection<V> res = new ArrayList<>(innerValues.size());
        for (Object val : innerValues) {
            res.add(fromStoreValue(val));
        }
        LoggerUtils.debug(log, "Read all values, count ({}).", innerValues.size());
        return res;
    }

    @SuppressWarnings("unchecked")
    protected V fromStoreValue(Object storeValue) {
        if (storeValue instanceof NullValue) {
            return null;
        }
        return (V) storeValue;
    }

    protected Object toStoreValue(V userValue) {
        if (userValue == null) {
            return NullValue.INSTANCE;
        }
        return userValue;
    }

    /**
     * The number of get requests that were satisfied by the cache.
     *
     * @return the number of hits
     */
    public long getCacheHits() {
        return this.hits.get();
    }

    /**
     * A miss is a get request that is not satisfied.
     *
     * @return the number of misses
     */
    public long getCacheMisses() {
        return this.misses.get();
    }

    private void addCacheHit() {
        this.hits.incrementAndGet();
    }

    private void addCacheMiss() {
        this.misses.incrementAndGet();
    }

}
