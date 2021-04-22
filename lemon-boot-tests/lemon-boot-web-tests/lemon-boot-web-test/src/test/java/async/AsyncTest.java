package async;

import async.service.LogServiceImpl;
import com.lemon.framework.core.concurrent.VisibleThreadPoolTaskExecutor;
import com.lemon.framework.core.context.AppContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 名称：测试异步调用与异步消息<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
@SpringBootTest
@Slf4j
public class AsyncTest {

    @Autowired
    private LogServiceImpl logService;

    @Autowired
    private VisibleThreadPoolTaskExecutor executor;

    @Test
    public void testAsyncService() throws InterruptedException {
        new Thread(() -> {
            // 主线程放入上下文变量
            AppContextHolder.getContext().set("Key", "Thread1 Value");
            // 模拟异步写日志
            for (int i = 0; i < 10; i++) {
                try {
                    logService.log("@Log first " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.printf("@Log first %s: Send all messages (key=%s)\n", Thread.currentThread().getName(), AppContextHolder.getContext().get("Key"));
            // 等待测试完成
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("@Log first %s: Completed.\n", Thread.currentThread().getName());
            // 释放上下文
            AppContextHolder.clearContext();
        }).start();

        new Thread(() -> {
            // 主线程放入上下文变量
            AppContextHolder.getContext().set("Key", "Thread2 Value");
            // 模拟异步写日志
            for (int i = 0; i < 10; i++) {
                try {
                    logService.log("@Log second " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.printf("@Log second %s: Send all messages (key=%s)\n", Thread.currentThread().getName(), AppContextHolder.getContext().get("Key"));
            // 等待测试完成
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("@Log second %s: Completed.\n", Thread.currentThread().getName());
            // 释放上下文
            AppContextHolder.clearContext();
        }).start();

        VisibleThreadPoolTaskExecutor.ThreadPoolExecutorInfo info = null;
        for (int i = 0; i < 100; i++) {
            Thread.sleep(100L);
            info = executor.getRuntimeInfo();
            System.out.println("-------------"+info);
            if (info.getCompletedTaskCount() >= 40)
                break;
        }
        Assertions.assertEquals(info.getTaskCount(), info.getCompletedTaskCount());
    }

}
