package com.lemon.framework.core.concurrent;

import com.lemon.framework.util.LoggerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <b>名称：线程池执行器扩展</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
@Slf4j
public class VisibleThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    public ThreadPoolExecutorInfo getRuntimeInfo() {

        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

        return new ThreadPoolExecutorInfo(
                this.getThreadNamePrefix(),
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getQueue().size(),
                threadPoolExecutor.getCompletedTaskCount(),
                threadPoolExecutor.getTaskCount()
        );
    }

    @Override
    public void execute(Runnable task) {
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(task);
    }

    @Override
    public void destroy() {
        LoggerUtils.info(log, "Destroy VisibleThreadPoolTaskExecutor.");
        super.destroy();
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class ThreadPoolExecutorInfo {

        private String threadNamePrefix;
        private int activeCount;
        private int queueSize;
        private long completedTaskCount;
        private long taskCount;

    }
}
