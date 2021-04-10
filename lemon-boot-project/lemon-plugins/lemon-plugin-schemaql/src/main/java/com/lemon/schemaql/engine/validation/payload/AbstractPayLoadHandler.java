package com.lemon.schemaql.engine.validation.payload;

import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
@RequiredArgsConstructor
public abstract class AbstractPayLoadHandler<T> implements IPayloadHandler<T> {

    private final ValidatorFactory validatorFactory;

    @Override
    public String getMessage(ConstraintViolation<T> violation) {
        String messageTemplate = violation.getMessageTemplate();

        Validator validator = validatorFactory.getValidator();

        // 是否国际化
        if (messageTemplate.startsWith("%")) {
            String message = null;
            violation.getExecutableParameters();
            ConstraintDescriptor descriptor = violation.getConstraintDescriptor();
            descriptor.getComposingConstraints();

            return message;
        } else {
            // 非国际化
            return violation.getMessage();
        }
    }
}
