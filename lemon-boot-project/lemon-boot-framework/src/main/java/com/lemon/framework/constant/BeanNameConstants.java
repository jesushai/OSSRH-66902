package com.lemon.framework.constant;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/21
 */
public class BeanNameConstants {

    /**
     * 核心异步线程池
     */
    public static final String CORE_ASYNC_POOL = "LemonAsyncThreadPool";


    /**
     * <p/>
     * 类：{@code com.lemon.framework.cache.redis.ByteArrayRedisTemplate}
     * <p/>
     * 对应的类型：{@code RedisTemplate<byte[], byte[]>}
     */
    public static final String BYTE_ARRAY_REDIS_TEMPLATE = "byteArrayRedisTemplate";

    /**
     * <p/>
     * 类：{@code com.lemon.framework.cache.redis.StringByteArrayRedisTemplate}
     * <p/>
     * 对应的类型：{@code RedisTemplate<String, byte[]>}
     */
    public static final String STRING_BYTE_ARRAY_REDIS_TEMPLATE = "stringByteArrayRedisTemplate";

    /**
     * <p/>
     * 类：{@code com.lemon.framework.cache.redis.JacksonRedisTemplate}
     * <p/>
     * 对应的类型：{@code RedisTemplate<String, Object>}
     */
    public static final String JACKSON_REDIS_TEMPLATE = "redisTemplate";

    /**
     * <p/>
     * 类：{@code com.lemon.framework.cache.redis.AllowUnknownJacksonRedisTemplate}
     * <p/>
     * 对应的类型：{@code RedisTemplate<String, Object>}
     */
    public static final String ALLOW_UNKNOWN_JACKSON_REDIS_TEMPLATE = "allowUnknownRedisTemplate";

    /**
     * <p/>
     * 类：{@code org.springframework.data.redis.core.StringRedisTemplate}
     * <p/>
     * 对应的类型：{@code RedisTemplate<String, String>}
     */
    public static final String STRING_REDIS_TEMPLATE = "stringRedisTemplate";


    /**
     * 通用的cache key生成规则
     */
    public static final String COMMON_KEY_GENERATOR = "commonKeyGenerator";

    /**
     * Spring上下文工具
     */
    public static final String SPRING_CONTEXT_UTILS = "springContextUtils";

    /**
     * 全局身份服务
     */
    public static final String AUTHENTICATION_SERVICE = "authenticationService";

    /**
     * 用户服务
     */
    public static final String USER_SERVICE = "userService";
    /**
     * 角色服务
     */
    public static final String ROLE_SERVICE = "roleService";
    /**
     * 授权服务
     */
    public static final String PERMISSION_SERVICE = "permissionService";

    /**
     * 租户服务
     */
    public static final String TENANT_SERVICE = "tenantService";


    /**
     * Shiro加密key
     */
    public static final String SHIRO_KEY = "shiroKey";

    /**
     * 国际化处理器
     */
    public static final String MESSAGE_SOURCE_HANDLER = "messageSourceHandler";

    /**
     * 验证消息国际化资源
     */
    public static final String VALIDATION_MESSAGE_SOURCE = "validationMessageSource";

}
