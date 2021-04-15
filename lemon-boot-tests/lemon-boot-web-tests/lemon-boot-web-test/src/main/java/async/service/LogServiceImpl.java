package async.service;

import com.lemon.framework.core.context.AppContextHolder;
import async.event.AsyncEvent;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.domain.event.DefaultEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
@Service
@Slf4j
public class LogServiceImpl {

    // 事件发布器
    @Autowired
    private DefaultEventPublisher eventPublisher;

    // 明确指定异步线程池
    @Async(BeanNameConstants.CORE_ASYNC_POOL)
    public void log(String msg) throws InterruptedException {
        // 模拟耗时的操作
        Thread.sleep(100L);
        // 异步发送事件
        eventPublisher.publish(new AsyncEvent("SubThread AsyncEvent Message: " + msg));
        // 输出子线程信息与上下文变量
        System.out.printf("Publish event [%30s] - key=%s: %s\n",
                Thread.currentThread().getName(),
                AppContextHolder.getContext().get("Key"),
                msg);
    }

}
