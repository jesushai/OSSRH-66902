package com.lemon.framework.cache.redisson;

/**
 * 名称：获取分布式锁后的操作<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
public interface AcquiredLockWorker<T> {

    T invokeAfterLockAcquired() throws Exception;
}
