package com.lemon.boot.autoconfigure.aspect;

import com.lemon.boot.autoconfigure.cache.redis.RedissonAutoConfiguration;
import com.lemon.framework.cache.redisson.DistributedLockAspect;
import com.lemon.framework.cache.redisson.DistributedLocker;
import com.lemon.framework.core.aspect.ApiLimitAspect;
import com.lemon.framework.core.aspect.ApiLogAspect;
import com.lemon.framework.core.aspect.support.ApiLimitHelper;
import com.lemon.framework.core.aspect.support.ApiLimitRepositorySupport;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ProceedingJoinPoint.class)
@Import({ApiLogAspect.class})
public class AspectAutoConfiguration {

    @Configuration
    @ConditionalOnClass(RedisOperations.class)
    @Import({ApiLimitAspect.class, ApiLimitHelper.class})
    public static class ApiLimitAspectAutoConfiguration {

    }

    @Configuration
    @ConditionalOnClass(RedissonClient.class)
    @AutoConfigureAfter(RedissonAutoConfiguration.class)
    public static class DistributedLockAspectAutoConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public DistributedLockAspect distributedLockAspect(DistributedLocker distributedLocker) {
            return new DistributedLockAspect(distributedLocker);
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public ApiLimitRepositorySupport apiLimitRepository() {
        return new ApiLimitRepositorySupport();
    }

}
