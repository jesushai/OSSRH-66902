package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.util.Initializable;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.cache.CacheConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 名称：Redission Shiro缓存管理器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedissonShiroCacheManager implements CacheManager, Initializable {

    private boolean allowNullValues = true;

    private Codec codec = new JsonJacksonCodec();

    private RedissonClient redisson;

    private String configLocation;

    private Map<String, CacheConfig> configs = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    public RedissonShiroCacheManager() {
    }

    public RedissonShiroCacheManager(RedissonClient redisson) {
        this(redisson, (String) null, null);
    }

    public RedissonShiroCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config) {
        this(redisson, config, null);
    }

    public RedissonShiroCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config, Codec codec) {
        this.redisson = redisson;
        this.configs = (Map<String, CacheConfig>) config;
        if (codec != null) {
            this.codec = codec;
        }

        if (null == this.configs) {
            LoggerUtils.debug(log, "No configuration information provided, use default cache configuration.");
        } else {
            this.configs.forEach((key, cc) ->
                    LoggerUtils.debug(log,
                            "Register the cache configuration: [{}] time-to-live({})s, max-idle-time({})s, max-size({})",
                            key, cc.getTTL(), cc.getMaxIdleTime(), cc.getMaxSize()));
        }
    }

    public RedissonShiroCacheManager(RedissonClient redisson, String configLocation) {
        this(redisson, configLocation, null);
    }

    public RedissonShiroCacheManager(RedissonClient redisson, String configLocation, Codec codec) {
        this.redisson = redisson;
        this.configLocation = configLocation;
        if (codec != null) {
            this.codec = codec;
        }
    }

    protected CacheConfig createDefaultConfig() {
        return new CacheConfig();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache<K, V> cache = this.caches.get(name);
        if (cache != null) {
            return cache;
        }

        CacheConfig config = this.configs.get(name);
        if (config == null) {
            config = createDefaultConfig();
            configs.put(name, config);
        }

        if (config.getMaxIdleTime() == 0 && config.getTTL() == 0 && config.getMaxSize() == 0) {
            return createMap(name, config);
        }

        return createMapCache(name, config);
    }

    private <K, V> Cache<K, V> createMap(String name, CacheConfig config) {
        RMap<K, Object> map = getMap(name, config);
        LoggerUtils.debug(log, "Create shiro redisson cache with default config: name={}, allowNullValues={}.", name, allowNullValues);
        Cache<K, V> cache = new RedissonShiroCache<>(map, allowNullValues);
        Cache<K, V> oldCache = this.caches.putIfAbsent(name, cache);
        if (oldCache != null) {
            cache = oldCache;
        }
        return cache;
    }

    protected <K> RMap<K, Object> getMap(String name, CacheConfig config) {
        if (null != this.codec) {
            return this.redisson.getMap(name, this.codec);
        }
        return this.redisson.getMap(name);
    }

    private <K, V> Cache<K, V> createMapCache(String name, CacheConfig config) {
        RMapCache<K, Object> map = getMapCache(name, config);
        LoggerUtils.debug(log, "Create shiro redisson cache with config: name={}, allowNullValues={}, ttl={}ms, maxIdleTime{}ms.",
                name, allowNullValues, config.getTTL(), config.getMaxIdleTime());
        Cache<K, V> cache = new RedissonShiroCache<>(map, config, this.allowNullValues);
        Cache<K, V> oldCache = this.caches.putIfAbsent(name, cache);
        if (null != oldCache) {
            cache = oldCache;
        } else {
            map.setMaxSize(config.getMaxSize());
        }
        return cache;
    }

    protected <K> RMapCache<K, Object> getMapCache(String name, CacheConfig config) {
        if (this.codec != null) {
            return this.redisson.getMapCache(name, this.codec);
        }
        return redisson.getMapCache(name);
    }

    @Override
    public void init() {
        if (this.configLocation == null) {
            return;
        }

        try {
            LoggerUtils.debug(log, "Loading configuration from resource file, the file location is {}", this.configLocation);
            this.configs = (Map<String, CacheConfig>) CacheConfig.fromJSON(ResourceUtils.getInputStreamForPath(this.configLocation));
        } catch (IOException e) {
            // try to read yaml
            try {
                this.configs = (Map<String, CacheConfig>) CacheConfig.fromYAML(ResourceUtils.getInputStreamForPath(this.configLocation));
            } catch (IOException e1) {
                throw new IllegalArgumentException(
                        "Could not parse cache configuration at [" + configLocation + "]", e1);
            }
        }
    }

    public void setConfig(Map<String, ? extends CacheConfig> config) {
        this.configs = (Map<String, CacheConfig>) config;
    }

    public Map<String, CacheConfig> getConfig() {
        return configs;
    }

    public RedissonClient getRedisson() {
        return redisson;
    }

    public void setRedisson(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public Codec getCodec() {
        return codec;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public boolean isAllowNullValues() {
        return allowNullValues;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }
}
