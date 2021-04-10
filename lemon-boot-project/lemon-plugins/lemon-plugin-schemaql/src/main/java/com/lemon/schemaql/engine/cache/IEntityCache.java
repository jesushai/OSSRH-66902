package com.lemon.schemaql.engine.cache;

import com.lemon.schemaql.config.EntitySchemaConfig;

import java.io.Serializable;

/**
 * <b>名称：实体缓存接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/21
 */
public interface IEntityCache {

    /**
     * 读取缓存的对象，没有则返回空
     *
     * @param id     主键
     * @param config 实体配置
     * @return 未命中则返回null
     */
    Object get(Serializable id, EntitySchemaConfig config);

    /**
     * 写入缓存
     *
     * @param entity 实体
     * @param config 实体配置
     */
    void put(Object entity, EntitySchemaConfig config);

    /**
     * 擦除缓存
     *
     * @param id     实体主键
     * @param config 实体配置
     */
    void evict(Serializable id, EntitySchemaConfig config);

    default String getEntityCacheName(EntitySchemaConfig config) {
        return config.getModuleSchemaConfig().getModuleName() + '.' + config.getEntityName();
    }
}
