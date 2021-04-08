package com.lemon.framework.cache.redisson;

import org.redisson.api.RCountDownLatch;

import java.util.concurrent.Future;

public interface DistributedLocker {

    /**
     * 可重入锁，锁定时间使用默认值
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception;

    /**
     * 可重入锁执行任务，并于lockTime秒后自动释放锁
     *
     * @param worker   锁后的任务
     * @param lockTime 多少秒后自动释放锁
     * @param <T>      任务的返回类型
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception;

    /**
     * 异步获取可重入锁
     */
    Future<Boolean> lockAsync(String resourceName, int releaseTime);

    /**
     * 公平锁，锁定时间使用默认值
     */
    <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker) throws Exception;

    /**
     * 公平锁
     */
    <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int lockTime) throws Exception;

    /**
     * 异步获取公平锁
     */
    Future<Boolean> fairLockAsync(String resourceName, int releaseTime);

    /**
     * 创建一个闭锁
     */
    RCountDownLatch countDownLatch(String resourceName, long count) throws UnableToAcquiredLockException;
}
