package com.lemon.boot.autoconfigure.commons.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/19
 */
@Data
@ConfigurationProperties(prefix = "zh.async")
public class ThreadPoolTaskExecutorProperties {

    /**
     * 核心线程数，优先级最高
     */
    private int corePoolSize = 5;

    /**
     * 队列容量，核心线程队列达到这个数量时才会新建线程
     */
    private int queueCapacity = 30;

    /**
     * 最大线程数，当核心线程全部队列满时才会新建线程，直到最大值
     */
    private int maxPoolSize = 50;

    /**
     * 线程活跃时间（秒）
     */
    private int keepAliveSeconds = 30;

    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "Lemon-Async-Thread";

    /**
     * 等待所有任务结束后再关闭线程池
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 多少秒后中断线程的执行
     */
    private int awaitTerminationSeconds = 60;

    /**
     * 拒绝策略: 即核心线程、最大线程都满且队列也都满的情况下走拒绝策略<p>
     * CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的主线程来执行
     */
    private String rejectedExecutionHandler = "CallerRunsPolicy";

}
