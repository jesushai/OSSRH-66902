package com.lemon.framework.cache.redisson;

/**
 * <b>名称：获取分布式锁后的操作</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
public interface AcquiredLockWorker<T> {

    T invokeAfterLockAcquired() throws Exception;
}
