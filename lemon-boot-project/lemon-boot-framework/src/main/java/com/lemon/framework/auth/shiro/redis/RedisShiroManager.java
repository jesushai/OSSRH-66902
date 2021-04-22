package com.lemon.framework.auth.shiro.redis;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/21
 */
@Slf4j
public class RedisShiroManager {

//    protected static final int DEFAULT_COUNT = 100;
//    private int count = 100;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisShiroManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Object get(String key) {
        if (key == null) {
            return null;
        } else {
            return redisTemplate.opsForValue().get(key);
        }
    }

    public Long getExpire(String key) {
        if (key == null) {
            return null;
        } else {
            return redisTemplate.getExpire(key);
        }
    }

    public void set(String key, Object value, int expireTime) {
        if (key != null) {
            try {
                redisTemplate.opsForValue().set(key, value);
                if (expireTime > 0) {
                    redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                LoggerUtils.error(log, e);
            }
        }
    }

    public void del(String key) {
        if (key != null) {
            redisTemplate.delete(key);
        }
    }

    public Long size(String pattern) {
//        try (Cursor<Map.Entry<Object, Object>> cursor =
//                     redisTemplate.opsForHash().scan()) {
//
//        }

        Long n = redisTemplate.execute((RedisCallback<Long>) connection -> {
//            Object nativeConn = connection.getNativeConnection();
//            if (nativeConn instanceof RedisClusterAsyncCommands) {
//                RedisClusterAsyncCommands commands = (RedisClusterAsyncCommands) nativeConn;
//                try {
//                    commands.scan(ScanArgs.Builder.matches(new String(pattern)).limit(this.count)).get();
//                } catch (InterruptedException | ExecutionException e) {
//                    e.printStackTrace();
//                }
//            } else if (nativeConn instanceof JedisCommands) {
//                JedisCommands commands = (JedisCommands) nativeConn;
//                commands.scan
//            }
            // TODO: 重写！可能占用内存，效率也不高
            long i = 0;
            try (Cursor<byte[]> cursor = connection.scan(
                    new ScanOptions.ScanOptionsBuilder()
                            .match(pattern)
                            .count(Integer.MAX_VALUE)
                            .build())) {

                while (cursor.hasNext()) {
                    cursor.next();
                    i++;
                }
            } catch (IOException e) {
                LoggerUtils.error(log, e);
            }
            return i;
        });
        LoggerUtils.debug(log, "Count the pattern[{}] is {}.", pattern, n);
        return n;
    }

    public Set<String> keys(String pattern) {
        // TODO: 重写！可能占用内存，效率也不高
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(
                    new ScanOptions.ScanOptionsBuilder()
                            .match(pattern)
                            .count(Integer.MAX_VALUE)
                            .build())) {

                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (IOException e) {
                LoggerUtils.error(log, e);
            }
            LoggerUtils.debug(log, "Get keys pattern[{}] is {}.", pattern, keys.size());
            return keys;
        });
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }
}
