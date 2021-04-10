package thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/19
 */
public class ExecutorServiceDemo {

    /**
     * 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     */
    @Test
    public void testCachedThreadPool() throws InterruptedException {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            cachedThreadPool.execute(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("[%30s]: %d\n", Thread.currentThread().getName(), index);
            });
        }
        Thread.sleep(200L);
        cachedThreadPool.shutdown();
    }

    /**
     * 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待
     */
    @Test
    public void testFixedThreadPool() throws InterruptedException {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            fixedThreadPool.execute(() -> {
                try {
                    Thread.sleep(200);
                    System.out.printf("[%30s]: %d\n", Thread.currentThread().getName(), index);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(2000L);
        fixedThreadPool.shutdown();
    }

    /**
     * 创建一个定长线程池，制定定时及周期性任务执行
     */
    @Test
    public void testScheduledThreadPool() throws InterruptedException {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            scheduledThreadPool.schedule(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("[%30s]: %d\n", Thread.currentThread().getName(), index);
            }, 3, TimeUnit.SECONDS);  //表示延迟3秒执行
        }
        Thread.sleep(10000L);
        scheduledThreadPool.shutdown();
    }

    /**
     * 表示延迟1秒后每2秒执行一次。
     */
    @Test
    public void testFixedScheduleThreadPool() throws InterruptedException {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("[%30s]: %d\n", Thread.currentThread().getName(), index);
            }, 1, 2, TimeUnit.SECONDS);
        }
        Thread.sleep(20000L);
        scheduledThreadPool.shutdown();
    }

    /**
     * 创建一个单线程化的线程池，他只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序（FIFO,LIFO,优先级）执行。
     */
    @Test
    public void testSingleThreadExecutor() throws InterruptedException {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(() -> {
                try {
                    System.out.printf("[%30s]: %d\n", Thread.currentThread().getName(), index);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(2000L);
        singleThreadExecutor.shutdown();
    }
}
