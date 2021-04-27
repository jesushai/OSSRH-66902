package com.lemon.schemaql.engine.validation.payload;

import com.lemon.framework.exception.support.Message;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;

/**
 * 名称：错误验证Payload<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public abstract class ErrorPayload<T> extends AbstractPayLoadHandler<T> implements Payload {

    @Override
    public Message getMessage(ConstraintViolation<T> violation) {
        return super.getMessage(violation);
    }
}
