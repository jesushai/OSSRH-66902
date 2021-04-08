package com.lemon.framework.core.aspect;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.cache.redis.JacksonRedisTemplate;
import com.lemon.framework.core.annotation.ApiLimit;
import com.lemon.framework.core.aspect.support.ApiLimitData;
import com.lemon.framework.core.aspect.support.ApiLimitRepositorySupport;
import com.lemon.framework.core.enums.LimitType;
import com.lemon.framework.exception.LimitAccessException;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.log.LogHelper;
import com.lemon.framework.log.LogTypeEnum;
import com.lemon.framework.util.IpUtils;
import com.lemon.framework.util.LoggerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * <b>名称：Api限流切片</b><br/>
 * <b>描述：</b><br/>
 * 需要redis支持
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class ApiLimitAspect extends AbstractAspect implements Ordered {

    private final JacksonRedisTemplate redisTemplate;
    private final AuthenticationService authenticationService;
    private final ApiLimitRepositorySupport apiLimitRepository;

    @Pointcut("@annotation(com.lemon.framework.core.annotation.ApiLimit)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void around(JoinPoint point) {
        Method method = resolveMethod(point);

        ApiLimit limitAnnotation = method.getAnnotation(ApiLimit.class);

        LimitType limitType = limitAnnotation.limitType();
        String name = limitAnnotation.name();
        int limitPeriod = limitAnnotation.period();
        int limitCount = limitAnnotation.count();

        // 尝试从数据库中获取限流配置
        ApiLimitData limitDb = apiLimitRepository.getApiLimit(limitAnnotation.key());
        if (null != limitDb && limitDb.getActive()) {
            name = limitDb.getName();
            limitPeriod = limitDb.getPeriod();
            limitCount = limitDb.getCount();
            limitType = limitDb.getLimitType();
            // Period&Count >= 0才限制
            if (limitPeriod <= 0 || limitCount <= 0) {
                return;
            }
        }

        String ip = IpUtils.getRequestHost();
        String redisKey = getRedisKey(
                limitAnnotation.prefix(), ip, limitAnnotation.key(), limitType, method.getName());

        List<String> keys = Collections.singletonList(redisKey);
        String luaScript = buildLuaScript();
        RedisScript<Number> redisScript = new DefaultRedisScript<>(luaScript, Number.class);
        Number count = redisTemplate.execute(redisScript, keys, limitCount, limitPeriod);

        if (count == null || count.intValue() > limitCount) {
            String message = MessageSourceHandler.getMessageOrNonLocale(
                    "OVER-API-LIMIT",
                    "API访问超过最大限制：[{0}]-API[{1}:{2}] ({3})秒内访问超过了({4})次！",
                    ip, limitAnnotation.key(), name, limitPeriod, count);
            LoggerUtils.debug(log, message);

            LogHelper.log(LogTypeEnum.SYSTEM.name(),
                    "ApiLimit",
                    limitAnnotation.key(),
                    0L,
                    message,
                    0L);

            throw new LimitAccessException();
        }
    }

    private String getRedisKey(String prefix, String ip, String key, LimitType type, String methodName) {
        String redisKey;
        switch (type) {
            case IP:
                redisKey = prefix + ':' + ip;
                break;
            case CUSTOMER:
                redisKey = prefix + ':' + key + '-' + ip;
                break;
            case PRINCIPAL:
                User user = authenticationService.getPrincipal();
                if (null != user) {
                    redisKey = prefix + ":[" + user.getTenant() + ']' + user.getUsername();
                } else {
                    redisKey = prefix + ':' + ip;
                }
                break;
            case PRINCIPAL_KEY:
                user = authenticationService.getPrincipal();
                if (user != null) {
                    redisKey = prefix + ":[" + user.getTenant() + ']' + user.getUsername() + '-' + key;
                } else {
                    redisKey = prefix + ':' + key;
                }
                break;
            default:
                redisKey = prefix + ':' + StringUtils.upperCase(methodName);
        }
        return redisKey;
    }

    /**
     * 限流脚本
     * 调用的时候不超过阈值，则直接返回并执行计算器自加。
     *
     * @return lua脚本
     */
    private String buildLuaScript() {
        return "local c" +
                "\nc = redis.call('get',KEYS[1])" +
                "\nif c and tonumber(c) > tonumber(ARGV[1]) then" +
                "\nreturn c;" +
                "\nend" +
                "\nc = redis.call('incr',KEYS[1])" +
                "\nif tonumber(c) == 1 then" +
                "\nredis.call('expire',KEYS[1],ARGV[2])" +
                "\nend" +
                "\nreturn c;";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
