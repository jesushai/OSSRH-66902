package com.lemon.framework.cache.redisson;

/**
 * <b>名称：分布式锁获取异常</b><br/>
 * <b>描述：</b><br/>
 * redisson不能获取redis分布式锁
 *
 * @author hai-zhang
 * @since 2020/6/9
 */
public class UnableToAcquiredLockException extends Exception {

}
