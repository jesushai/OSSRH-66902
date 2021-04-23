package thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 名称：修饰线程池<p>
 * 描述：<p>
 * <p>
 * 省去每次Runnable和Callable传入线程池时的修饰，这个逻辑可以在线程池中完成。<p>
 * 通过工具类com.alibaba.ttl.threadpool.TtlExecutors完成，有下面的方法：
 * <li>getTtlExecutor：修饰接口Executor</li>
 * <li>getTtlExecutorService：修饰接口ExecutorService</li>
 * <li>getTtlScheduledExecutorService：修饰接口ScheduledExecutorService</li>
 *
 * @author hai-zhang
 * @since 2020/5/19
 */
public class TtlExecutorWrapperDemo {

    /**
     * 需要注意的是，使用TTL的时候，要想传递的值不出问题，线程池必须得用TTL加一层代理
     */
//    private static ExecutorService ttlExecutorService = TtlExecutors.getTtlExecutorService(Executors.newCachedThreadPool());

    /**
     * Spring boot 线程池
     */
    private static Executor ttlExecutorService = TtlExecutors.getTtlExecutor(newThreadPoolTaskExecutor());

    private static ThreadPoolTaskExecutor newThreadPoolTaskExecutor() {
        // https://blog.csdn.net/wrongyao/article/details/85788302
        // 配置说明
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        // 优先级最高，这些线程会一直存在
        executor.setCorePoolSize(5);
        // 设置队列容量
        // 优先级第二高，核心线程队列达到这个数量时才会新建线程
        executor.setQueueCapacity(50);
        // 设置最大线程数
        // 优先级最低，当核心线程全部队列满时才会新建线程，直到最大值
        executor.setMaxPoolSize(200);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(30);
        // 设置线程名称前缀
        executor.setThreadNamePrefix("Test-Async-Thread");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        // 设置拒绝策略
        // 即核心线程、最大线程都满且队列也都满的情况下走拒绝策略
        // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的主线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    /**
     * 可以定制化Ttl
     */
    private static TransmittableThreadLocal<Item> context = new TransmittableThreadLocal<Item>() {
        /**
         * 定制化备份方法，这里是克隆
         */
        @Override
        public Item copy(Item parentValue) {
            return parentValue.copy();
        }

        @Override
        protected void beforeExecute() {
            System.out.println("父子线程切换开始...");
            super.beforeExecute();
        }

        @Override
        protected void afterExecute() {
            System.out.println("父子线程切换完成...");
            super.afterExecute();
        }

        /**
         * 重写初始化方法
         */
        @Override
        protected Item initialValue() {
            return new Item(0L, "");
        }
    };

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        context.set(new Item(0, "foo"));
        System.out.printf("[parent thread] set %s\n", context.get());

        /////////////////////////////////////
        // Runnable
        /////////////////////////////////////
        Runnable task = () -> System.out.printf("[child thread] get %s in Runnable\n", context.get());
//        ttlExecutorService.submit(task).get();
        ttlExecutorService.execute(task);

//        /////////////////////////////////////
//        // Callable
//        /////////////////////////////////////
//        Callable<?> callable = (Callable<Object>) () -> {
//            System.out.printf("[child thread] get %s in Callable\n", context.get());
//            return "Callable result here.";
//        };

//        Object o = ttlExecutorService.submit(callable).get();
//        System.out.println(o);

        System.out.println("主线程：" + context.get());
    }

    @Data
    @ToString
    private static class Item {
        private long id;
        private String name;
        private long random;

        Item(long id, String name) {
            this.id = id;
            this.name = name;
            this.random = new Random().nextLong();
        }

        Item copy() {
            return new Item(this.getId() + 1, this.getName());
        }
    }
}
