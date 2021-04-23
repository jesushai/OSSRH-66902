package com.lemon.framework.cache.redisson;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
public class RedisLocker implements DistributedLocker {

    private final RedissonClient redisson;
    private final int defaultReleaseTime;
    private final int maxWaitTime;

    public RedisLocker(RedissonClient redisson, int defaultReleaseTime, int maxWaitTime) {
        Assert.notNull(redisson, "The parameter redissonClient cannot be null.");
        this.redisson = redisson;
        this.defaultReleaseTime = defaultReleaseTime <= 0 ? 10 : defaultReleaseTime;
        this.maxWaitTime = maxWaitTime <= 0 ? 100 : maxWaitTime;
    }

    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker) throws Exception {
        return lock(resourceName, worker, defaultReleaseTime);
    }

    @Override
    public <T> T lock(String resourceName, AcquiredLockWorker<T> worker, int releaseTime) throws Exception {
        RLock lock = redisson.getLock(resourceName);
        return doLock(lock, worker, releaseTime);
    }

    @Override
    public Future<Boolean> lockAsync(String resourceName, int releaseTime) {
        RLock lock = redisson.getLock(resourceName);
        return doLockAsync(lock, releaseTime);
    }

    @Override
    public <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker) throws Exception {
        return fairLock(resourceName, worker, defaultReleaseTime);
    }

    @Override
    public <T> T fairLock(String resourceName, AcquiredLockWorker<T> worker, int releaseTime) throws Exception {
        RLock lock = redisson.getFairLock(resourceName);
        return doLock(lock, worker, releaseTime);
    }

    @Override
    public Future<Boolean> fairLockAsync(String resourceName, int releaseTime) {
        RLock lock = redisson.getFairLock(resourceName);
        return doLockAsync(lock, releaseTime);
    }

    private <T> T doLock(RLock lock, AcquiredLockWorker<T> worker, int releaseTime) throws Exception {
        boolean success = lock.tryLock(maxWaitTime, releaseTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return worker.invokeAfterLockAcquired();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
        throw new UnableToAcquiredLockException();
    }

    private Future<Boolean> doLockAsync(RLock lock, int releaseTime) {
        return lock.tryLockAsync(maxWaitTime, releaseTime, TimeUnit.SECONDS);
    }

    @Override
    public RCountDownLatch countDownLatch(String resourceName, long count) throws UnableToAcquiredLockException {
        RCountDownLatch latch = redisson.getCountDownLatch(resourceName);
        boolean success = latch.trySetCount(count);
        if (success) {
            return latch;
        } else {
            throw new UnableToAcquiredLockException();
        }
    }

}
