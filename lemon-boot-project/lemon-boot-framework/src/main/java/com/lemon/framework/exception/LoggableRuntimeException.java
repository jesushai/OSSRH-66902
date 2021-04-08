package com.lemon.framework.exception;

import com.lemon.framework.exception.support.ErrorMessage;
import com.lemon.framework.exception.support.ExceptionIDGenerator;

/**
 * 名称：
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
public abstract class LoggableRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -5516569464771491423L;

    private boolean isLogged;
    private String uniqueID;

    protected String code;
    protected String message;

    {
        isLogged = false;
        uniqueID = ExceptionIDGenerator.getExceptionID();
    }

    public ErrorMessage getErrorMessage() {
        return new ErrorMessage(code, message);
    }

    LoggableRuntimeException() {
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public void setLogged(boolean logged) {
        isLogged = logged;
    }

    public boolean getLogged() {
        return isLogged;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    void setCode(String code) {
        this.code = code;
    }

    void setMessage(String message) {
        this.message = message;
    }

}
