package com.lemon.framework.exception;

/**
 * 名称：主键异常<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/9/28
 */
@SuppressWarnings("unused")
public class SequenceException extends RuntimeException {

    private static final long serialVersionUID = -6770005047408105390L;

    public SequenceException(String message) {
        super(message);
    }

    public SequenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
