package com.lemon.framework.cache.redisson;

/**
 * 名称：获取分布式锁后的操作<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
public interface AcquiredLockWorker<T> {

    T invokeAfterLockAcquired() throws Exception;
}
