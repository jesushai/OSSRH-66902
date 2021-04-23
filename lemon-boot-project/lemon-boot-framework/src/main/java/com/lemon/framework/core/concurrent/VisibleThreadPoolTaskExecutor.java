package com.lemon.framework.core.concurrent;

import com.lemon.framework.util.LoggerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 名称：线程池执行器扩展<p>
 * 描述：<p>
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
    public void execute(@NonNull Runnable task) {
        super.execute(task);
    }

    @Override
    public void execute(@NonNull Runnable task, long startTimeout) {
        super.execute(task, startTimeout);
    }

    @Override
    @NonNull
    public Future<?> submit(@NonNull Runnable task) {
        return super.submit(task);
    }

    @Override
    @NonNull
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return super.submit(task);
    }

    @Override
    @NonNull
    public ListenableFuture<?> submitListenable(@NonNull Runnable task) {
        return super.submitListenable(task);
    }

    @Override
    @NonNull
    public <T> ListenableFuture<T> submitListenable(@NonNull Callable<T> task) {
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

        /**
         * 线程名前缀
         */
        private String threadNamePrefix;

        /**
         * 活跃数
         */
        private int activeCount;

        /**
         * 队列大小
         */
        private int queueSize;

        /**
         * 完成任务数
         */
        private long completedTaskCount;

        /**
         * 任务数
         */
        private long taskCount;

    }
}
