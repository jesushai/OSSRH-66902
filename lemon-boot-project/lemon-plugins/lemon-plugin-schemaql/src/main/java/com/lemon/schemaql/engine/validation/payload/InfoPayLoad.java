package com.lemon.schemaql.engine.validation.payload;

import com.lemon.framework.exception.support.Message;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;

/**
 * 名称：警告验证Payload<p>
 * 描述：<p>
 * 也可以作为提示信息
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public abstract class InfoPayLoad<T> extends AbstractPayLoadHandler<T> implements Payload {

    @Override
    public Message getMessage(ConstraintViolation<T> violation) {
        return super.getMessage(violation);
    }
}
