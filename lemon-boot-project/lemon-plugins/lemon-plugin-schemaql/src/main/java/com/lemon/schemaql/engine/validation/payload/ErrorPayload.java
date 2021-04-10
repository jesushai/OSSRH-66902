package com.lemon.schemaql.engine.validation.payload;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.ValidatorFactory;

/**
 * <b>名称：错误验证Payload</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public abstract class ErrorPayload<T> extends AbstractPayLoadHandler<T> implements Payload {

    public ErrorPayload(ValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public String getMessage(ConstraintViolation<T> violation) {
        return super.getMessage(violation);
    }
}
