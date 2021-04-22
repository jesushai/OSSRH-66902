package com.lemon.framework.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;

/**
 * 名称：默认领域事件发布实现<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@Slf4j
public class DefaultEventPublisher implements EventPublisher {

    /**
     * 事件发布对象
     */
    @Resource
    private ApplicationEventPublisher publisher;

    @Override
    public void publish(DomainEvent<?> event) {
//        LoggerUtils.debug(log, "当前发布事件: {}", event);
//        event = event.stuff().apply("");
        publisher.publishEvent(event);
    }

//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//        this.publisher = applicationEventPublisher;
//    }

}
