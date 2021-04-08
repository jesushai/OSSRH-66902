package com.lemon.framework.exception;

/**
 * <b>名称：主键异常</b><br/>
 * <b>描述：</b><br/>
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
