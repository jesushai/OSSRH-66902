package com.lemon.framework.exception.log;

import com.lemon.framework.log.Log;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * 名称：异常日志<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@Value
@AllArgsConstructor
public class ExceptionLog implements Log {

    private static final long serialVersionUID = -2837528336224532824L;

    Exception exception;
}
