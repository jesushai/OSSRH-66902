package com.lemon.schemaql.config.support;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@Accessors(chain = true)
public class CachingConfig {

    /**
     * 是否允许缓存
     */
    private Boolean cacheable = false;

    /**
     * 是否允许前端自己缓存
     */
    private Boolean cacheByClient = false;

    /**
     * 存活时间, 默认null永久缓存, PT10M
     */
    private Duration timeToLive;

    /**
     * 客户端存活时间, 默认null永久缓存, PT10M
     */
    private Duration timeToLiveByClient;
}
