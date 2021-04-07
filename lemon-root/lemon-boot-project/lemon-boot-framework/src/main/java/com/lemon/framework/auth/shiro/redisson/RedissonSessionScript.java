package com.lemon.framework.auth.shiro.redisson;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
public abstract class RedissonSessionScript {

    public static final String RETURN_CODE_EXPIRED = "-1";

    public static final String RETURN_CODE_STOPPED = "-2";

    public static final String RETURN_CODE_INVALID = "-3";

    /**
     * 触摸session
     *
     * <li>1. 检车key是否存在（session未过期）</li>
     * <li>2. 检车是否已经停止（session停用）</li>
     * <li>3. 获得session的超时信息</li>
     * <li>4. 更新最后访问时间</li>
     * <li>5. 刷新当前过期时间</li>
     * <li>6. 刷新两个key的有效期</li>
     */
    public static final String TOUCH_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local timeoutEncoded = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_TIMEOUT_KEY + "');\n" +
                    "if timeoutEncoded == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local timeout = cjson.decode(timeoutEncoded)[2];\n" +
                    "\n" +
                    "redis.call('HSET', KEYS[1], '" + RedissonSession.INFO_LAST_KEY + "', ARGV[1]);\n" +
                    "redis.call('HSET', KEYS[1], '" + RedissonSession.INFO_EXPIRE_KEY + "', ARGV[2]);\n" +
                    "redis.call('PEXPIRE', KEYS[1], timeout);\n" +
                    "redis.call('PEXPIRE', KEYS[2], timeout);";

    /**
     * 初始化session哈希对象<p/>
     * 参数：
     * <li>1. sessionId</li>
     * <li>2. 超时毫秒数</li>
     * <li>3. 开始时间戳</li>
     * <li>4. host key</li>
     * <li>5. 当前过期时间</li>
     */
    public static final String INIT_SCRIPT =
            "redis.call('HMSET', KEYS[1], '" + RedissonSession.INFO_ID_KEY + "', ARGV[1],\n'" +
                    RedissonSession.INFO_TIMEOUT_KEY + "', ARGV[2],\n'" +
                    RedissonSession.INFO_START_KEY + "', ARGV[3],\n'" +
                    RedissonSession.INFO_LAST_KEY + "', ARGV[3],\n'" +
                    RedissonSession.INFO_HOST_KEY + "', ARGV[4],\n'" +
                    RedissonSession.INFO_EXPIRE_KEY + "', ARGV[5]);\n" +
                    "local timeout = cjson.decode(ARGV[2])[2];\n" +
                    "redis.call('PEXPIRE', KEYS[1], timeout);";

    /**
     * 获取session哈希对象<p/>
     * 返回序列：
     * <li>1. 开始时间</li>
     * <li>2. 最后访问时间</li>
     * <li>3. 超时时间</li>
     * <li>4. host key</li>
     * <li>5. 当前到期时间</li>
     */
    public static final String READ_INFO_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local session = redis.call('HMGET', KEYS[1], '" + RedissonSession.INFO_START_KEY +
                    "', '" + RedissonSession.INFO_LAST_KEY +
                    "', '" + RedissonSession.INFO_TIMEOUT_KEY +
                    "', '" + RedissonSession.INFO_HOST_KEY +
                    "', '" + RedissonSession.INFO_EXPIRE_KEY + "');\n" +
                    "if session == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return session;";

    /**
     * 获取session的开始时间戳
     */
    public static final String GET_START_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local startTime = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_START_KEY + "');\n" +
                    "if startTime == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return startTime;";

    /**
     * 获取session的最后访问时间
     */
    public static final String GET_LAST_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local lastTime = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_LAST_KEY + "');\n" +
                    "if lastTime == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return lastTime;";

    /**
     * 获取session的当前到期时间
     */
    public static final String GET_EXPIRE_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local expireTime = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_EXPIRE_KEY + "');\n" +
                    "if expireTime == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return expireTime;";

    /**
     * 获取session的超时信息
     */
    public static final String GET_TIMEOUT_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local timeout = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_TIMEOUT_KEY + "');\n" +
                    "if timeout == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return timeout;";

    /**
     * 获取session的host
     */
    public static final String GET_HOST_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local host = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_HOST_KEY + "');\n" +
                    "if host == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return host;";

    /**
     * 设置新的超时毫秒数，同时延长当前过期时间
     */
    static final String SET_TIMEOUT_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local timeout = redis.call('HGET', KEYS[1], '" + RedissonSession.INFO_TIMEOUT_KEY + "');\n" +
                    "if timeout == nil then\n" +
                    "  return " + throwError(RETURN_CODE_INVALID) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "redis.call('HSET', KEYS[1], '" + RedissonSession.INFO_TIMEOUT_KEY + "', ARGV[1]);\n" +
                    "redis.call('HSET', KEYS[1], '" + RedissonSession.INFO_EXPIRE_KEY + "', ARGV[2]);\n" +
                    "local newTimeout = cjson.decode(ARGV[1])[2];\n" +
                    "redis.call('PEXPIRE', KEYS[1], newTimeout);\n" +
                    "redis.call('PEXPIRE', KEYS[2], newTimeout);";

    /**
     * 设置session的停止时间戳（session停用）
     */
    public static final String STOP_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "redis.call('HSET', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "', ARGV[1]);";

    /**
     * 获取session的附加属性的名字集合
     */
    public static final String GET_ATTRKEYS_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return redis.call('HKEYS', KEYS[2]);";

    /**
     * 获取属性值
     */
    static final String GET_ATTR_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "return redis.call('HGET', KEYS[2], ARGV[1]);";

    /**
     * 删除属性值
     */
    public static final String REMOVE_ATTR_SCRIPT =
            "if redis.call('PTTL', KEYS[1]) <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "local attr = redis.call('HGET', KEYS[2], ARGV[1]);\n" +
                    "if attr ~= nil then\n" +
                    "  redis.call('HDEL', KEYS[2], ARGV[1]);\n" +
                    "end;\n" +
                    "\n" +
                    "return attr;";

    static final String SET_ATTR_SCRIPT =
            "local pttl = redis.call('PTTL', KEYS[1]);\n" +
                    "if pttl <= 0 then\n" +
                    "  return " + throwError(RETURN_CODE_EXPIRED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "if redis.call('HEXISTS', KEYS[1], '" + RedissonSession.INFO_STOP_KEY + "') == 1 then\n" +
                    "  return " + throwError(RETURN_CODE_STOPPED) + ";\n" +
                    "end;\n" +
                    "\n" +
                    "redis.call('HSET', KEYS[2], ARGV[1], ARGV[2]);\n" +
                    "-- redis auto delete key of hash when it is empty.\n" +
                    "-- then, expire time of the hash will be lost.\n" +
                    "if redis.call('PTTL', KEYS[2]) <= 0 then\n" +
                    "  redis.call('PEXPIRE', KEYS[2], pttl);\n" +
                    "end;";

    public static final String DELETE_SCRIPT =
            "redis.call('UNLINK', KEYS[1], KEYS[2]);";

    public static final String READ_SCRIPT =
            "return redis.call('PTTL', KEYS[1]);";

    private static String throwError(String errMsg) {
        return "redis.error_reply('" + errMsg + "')";
    }
}
