package com.lemon.framework.domain.event;

/**
 * 发布领域事件（标示接口）
 */
@SuppressWarnings("unused")
public interface EventPublisher {

    String EVENT_PUBLISHER_BEAN = "defaultEventPublisher";

    /**
     * 发布领域事件
     *
     * @param event 发布的事件
     */
    void publish(DomainEvent<?> event);
}
