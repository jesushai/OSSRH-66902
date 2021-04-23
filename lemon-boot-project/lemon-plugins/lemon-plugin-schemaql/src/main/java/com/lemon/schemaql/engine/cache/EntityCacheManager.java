package com.lemon.schemaql.engine.cache;

import com.lemon.schemaql.config.EntitySchemaConfig;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/21
 */
@RequiredArgsConstructor
public class EntityCacheManager {

    private final IEntityCache entityCache;

    public Object get(Serializable id, EntitySchemaConfig config) {
        return entityCache.get(id, config);
    }

    public void put(Object entity, EntitySchemaConfig config) {
        entityCache.put(entity, config);
    }

    public void evict(Serializable id, EntitySchemaConfig config) {
        entityCache.evict(id, config);
    }
}
