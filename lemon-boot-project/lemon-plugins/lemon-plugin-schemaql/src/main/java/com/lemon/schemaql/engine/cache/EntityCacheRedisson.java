package com.lemon.schemaql.engine.cache;

import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.support.CachingConfig;
import com.lemon.schemaql.engine.helper.EntitySchemaHelper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * <b>名称：Redisson实体缓存</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/21
 */
@RequiredArgsConstructor
public class EntityCacheRedisson implements IEntityCache {

    private final RedissonClient redisson;

    private static Codec codec = new JsonJacksonCodec();

    @Override
    public Object get(Serializable id, EntitySchemaConfig config) {
        Assert.notNull(id, "Entity id must not be null");
        Assert.notNull(config, "EntitySchemaConfig must not be null");
        CachingConfig cachingConfig = config.getCaching();
        if (null != cachingConfig && cachingConfig.getCacheable()) {
            if (null != cachingConfig.getTimeToLive()) {
                RMapCache<Serializable, Object> entityMapCache = redisson.getMapCache(
                        getEntityCacheName(config), codec);
                return entityMapCache.get(id);
            } else {
                RMap<Serializable, Object> entityMap = redisson.getMap(
                        getEntityCacheName(config), codec);
                return entityMap.get(id);
            }
        }
        return null;
    }

    @Override
    public void put(Object entity, EntitySchemaConfig config) {
        Assert.notNull(entity, "Entity must not be null");
        Assert.notNull(config, "EntitySchemaConfig must not be null");
        CachingConfig cachingConfig = config.getCaching();
        if (null != cachingConfig && cachingConfig.getCacheable()) {
            if (null != cachingConfig.getTimeToLive()) {
                RMapCache<Serializable, Object> entityMapCache = redisson.getMapCache(
                        getEntityCacheName(config), codec);
                entityMapCache.put(
                        EntitySchemaHelper.getId(entity, config),
                        entity,
                        cachingConfig.getTimeToLive().getSeconds(),
                        TimeUnit.SECONDS);
            } else {
                RMap<Serializable, Object> entityMap = redisson.getMap(
                        getEntityCacheName(config), codec);
                entityMap.put(
                        EntitySchemaHelper.getId(entity, config),
                        entity);
            }
        }
    }

    @Override
    public void evict(Serializable id, EntitySchemaConfig config) {
        Assert.notNull(id, "Entity id must not be null");
        Assert.notNull(config, "EntitySchemaConfig must not be null");
        CachingConfig cachingConfig = config.getCaching();
        if (null != cachingConfig && cachingConfig.getCacheable()) {
            if (null != cachingConfig.getTimeToLive()) {
                RMapCache<Serializable, Object> entityMapCache = redisson.getMapCache(
                        getEntityCacheName(config), codec);
                entityMapCache.remove(id);
            } else {
                RMap<Serializable, Object> entityMap = redisson.getMap(
                        getEntityCacheName(config), codec);
                entityMap.remove(id);
            }
        }
    }
}
