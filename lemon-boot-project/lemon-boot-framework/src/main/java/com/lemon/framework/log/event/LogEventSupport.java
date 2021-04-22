package com.lemon.framework.log.event;

import com.lemon.framework.domain.event.support.DomainEventSupport;
import com.lemon.framework.log.Log;

/**
 * 名称：日志事件基础类<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
public class LogEventSupport<T extends Log> extends DomainEventSupport<T> {

    public LogEventSupport(T eventSource, String eventState) {
        super(eventSource, eventState);
    }

    public LogEventSupport(T eventSource) {
        super(eventSource, DomainEventSupport.CREATE);
    }
}
