package com.lemon.framework.core.concurrent.web;

import com.lemon.framework.core.annotation.Monitor;
import com.lemon.framework.core.concurrent.VisibleThreadPoolTaskExecutor;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/28
 */
@Monitor
@RequestMapping("/sys/monitor/thread")
public class ThreadPoolTaskMonitor {

    private final VisibleThreadPoolTaskExecutor visibleThreadPoolTaskExecutor;

    public ThreadPoolTaskMonitor(@Lazy VisibleThreadPoolTaskExecutor visibleThreadPoolTaskExecutor) {
        this.visibleThreadPoolTaskExecutor = visibleThreadPoolTaskExecutor;
    }

    @GetMapping("/info")
    public VisibleThreadPoolTaskExecutor.ThreadPoolExecutorInfo getThreadPoolInfo() {
        return visibleThreadPoolTaskExecutor.getRuntimeInfo();
    }
}
