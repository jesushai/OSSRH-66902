package com.lemon.framework.cache.redisson;

import org.redisson.api.RCountDownLatch;

import java.util.concurrent.Future;

public interface DistributedLocker {

    /**
     * 可重入锁，锁定时间使用默认值
     *
     * @param resourceName 资源名
     * @param worker       锁后的任务
     * @param <T>          任务的返回类型
     * @return 任务完成后的返回值
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception;

    /**
     * 可重入锁执行任务，并于lockTime秒后自动释放锁
     *
     * @param resourceName 资源名
     * @param worker       锁后的任务
     * @param releaseTime  多少秒后自动释放锁
     * @param <T>          任务的返回类型
     * @return 任务完成后的返回值
     */
    <T> T lock(String resourceName, AcquiredLockWorker<T> worker, int releaseTime) throws Exception;

    /**
     * 异步获取可重入锁
     *
     * @param resourceName 资源名
     * @param releaseTime  多少秒后自动释放锁
     * @return Future
     */
    Future<Boolean> lockAsync(String resourceName, int releaseTime);

    /**
     * 公平锁，锁定时间使用默认值
     *
     * @param resourceName 资源名
     * @param worker       锁后的任务
     * @return 任务完成后的返回值
     */
    <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker) throws Exception;

    /**
     * 公平锁
     *
     * @param resourceName 资源名
     * @param worker       锁后的任务
     * @param releaseTime  多少秒后自动释放锁
     * @return 任务完成后的返回值
     */
    <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int releaseTime) throws Exception;

    /**
     * 异步获取公平锁
     *
     * @param resourceName 资源名
     * @param releaseTime  多少秒后自动释放锁
     * @return Future
     */
    Future<Boolean> fairLockAsync(String resourceName, int releaseTime);

    /**
     * 创建一个闭锁
     *
     * @param resourceName 资源名
     * @param count        闭锁的数
     * @return RCountDownLatch
     */
    RCountDownLatch countDownLatch(String resourceName, long count) throws UnableToAcquiredLockException;
}
