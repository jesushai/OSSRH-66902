package com.lemon.framework.exception;

import com.lemon.framework.exception.support.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/1
 */
public class MultiErrorException extends LoggableRuntimeException {

    public MultiErrorException() {
        errors = new ArrayList<>();
    }

    public MultiErrorException addError(String code, String message) {
        errors.add(new ErrorMessage(code, message));
        return this;
    }

    private List<ErrorMessage> errors;

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorMessage> errors) {
        this.errors = errors;
    }
}
