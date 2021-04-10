package com.lemon.schemaql.engine.validation.payload;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.ValidatorFactory;

/**
 * <b>名称：警告验证Payload</b><br/>
 * <b>描述：</b><br/>
 * 也可以作为提示信息
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public abstract class InfoPayLoad<T> extends AbstractPayLoadHandler<T> implements Payload {

    public InfoPayLoad(ValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public String getMessage(ConstraintViolation<T> violation) {
        return super.getMessage(violation);
    }
}
