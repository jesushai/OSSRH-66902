package com.lemon.framework.exception.log;

import com.lemon.framework.exception.support.Message;
import com.lemon.framework.log.Log;
import lombok.Value;

import java.util.Collection;
import java.util.Collections;

/**
 * 名称：错误信息日志<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@Value
public class ErrorMessageLog implements Log {

    private static final long serialVersionUID = 232339144418025543L;

    Collection<Message> message;

    public ErrorMessageLog(Collection<Message> message) {
        this.message = message;
    }

    public ErrorMessageLog(Message message) {
        this.message = Collections.singletonList(message);
    }
}
