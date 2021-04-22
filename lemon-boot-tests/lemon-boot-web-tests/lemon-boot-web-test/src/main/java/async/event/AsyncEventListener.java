package async.event;

import com.lemon.framework.core.context.AppContextHolder;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 名称：异步事件监听器<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/20
 */
@Component
public class AsyncEventListener {

    // 指定异步线程池来处理监听到的事件
    @Async(BeanNameConstants.CORE_ASYNC_POOL)
    @EventListener
    public void handle(AsyncEvent event) {
        // 输出子线程名称、事件信息与上下文变量
        System.out.printf("Listener event: @Log [%30s] - key=%s: %s\n",
                Thread.currentThread().getName(),
                AppContextHolder.getContext().get("Key"),
                event.getEventSource());
    }
}
