package com.lemon.framework.exception.log;

import com.lemon.framework.exception.support.ErrorMessage;
import com.lemon.framework.log.Log;
import lombok.Value;

import java.util.Collection;
import java.util.Collections;

/**
 * 名称：错误信息日志<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@Value
public class ErrorMessageLog implements Log {

    private static final long serialVersionUID = 232339144418025543L;

    Collection<ErrorMessage> message;

    public ErrorMessageLog(Collection<ErrorMessage> message) {
        this.message = message;
    }

    public ErrorMessageLog(ErrorMessage message) {
        this.message = Collections.singletonList(message);
    }
}
