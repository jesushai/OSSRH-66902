package com.lemon.boot.autoconfigure.commons;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.lemon.boot.autoconfigure.commons.properties.ThreadPoolTaskExecutorProperties;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.core.concurrent.VisibleThreadPoolTaskExecutor;
import com.lemon.framework.core.concurrent.web.ThreadPoolTaskMonitor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/19
 */
@Configuration(proxyBeanMethods = false)
@Import(ThreadPoolTaskMonitor.class)
@EnableConfigurationProperties(ThreadPoolTaskExecutorProperties.class)
public class ThreadPoolTaskExecutorAutoConfiguration {

    @Bean
    public VisibleThreadPoolTaskExecutor asyncThreadPoolTaskExecutor(ThreadPoolTaskExecutorProperties properties) {
        // https://blog.csdn.net/wrongyao/article/details/85788302
        // 配置说明
        VisibleThreadPoolTaskExecutor executor = new VisibleThreadPoolTaskExecutor();
        // 设置核心线程数
        // 优先级最高，这些线程会一直存在
        executor.setCorePoolSize(properties.getCorePoolSize());
        // 设置队列容量
        // 优先级第二高，核心线程队列达到这个数量时才会新建线程
        executor.setQueueCapacity(properties.getQueueCapacity());
        // 设置最大线程数
        // 优先级最低，当核心线程全部队列满时才会新建线程，直到最大值
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        // 设置线程名称前缀
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());
        // 设置拒绝策略
        // 即核心线程、最大线程都满且队列也都满的情况下走拒绝策略
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的主线程来执行
        switch (properties.getRejectedExecutionHandler()) {
            case "CallerRunsPolicy":
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
                break;
            case "AbortPolicy":
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
                break;
            case "DiscardOldestPolicy":
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
                break;
            case "DiscardPolicy":
                executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
                break;
            default:
                break;
        }
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 普通的Spring boot线程池执行器
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("com.alibaba.ttl.threadpool.TtlExecutors")
    @RequiredArgsConstructor
    public static class SimpleThreadPoolTaskExecutorConfiguration {

        private final VisibleThreadPoolTaskExecutor executor;

        @Bean(BeanNameConstants.CORE_ASYNC_POOL)
        public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
            return executor;
        }
    }

    /**
     * 使用Ttl包装的Spring boot线程池执行器
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(com.alibaba.ttl.threadpool.TtlExecutors.class)
    @RequiredArgsConstructor
    public static class TtlThreadPoolTaskExecutorConfiguration {

        private final VisibleThreadPoolTaskExecutor executor;

        @Bean(BeanNameConstants.CORE_ASYNC_POOL)
        public Executor asyncThreadPoolTaskExecutor() {
            return TtlExecutors.getTtlExecutor(executor);
        }
    }

}
