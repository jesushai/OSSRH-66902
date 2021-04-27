package com.lemon.framework.exception;

import com.lemon.framework.exception.support.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/1
 */
public class MultiErrorException extends LoggableRuntimeException {

    public MultiErrorException() {
        errors = new ArrayList<>();
    }

    public MultiErrorException addError(String code, String message) {
        errors.add(new Message(code, message));
        return this;
    }

    public MultiErrorException addError(Message message) {
        errors.add(message);
        return this;
    }

    private List<Message> errors;

    public List<Message> getErrors() {
        return errors;
    }

    public void setErrors(List<Message> errors) {
        this.errors = errors;
    }
}
