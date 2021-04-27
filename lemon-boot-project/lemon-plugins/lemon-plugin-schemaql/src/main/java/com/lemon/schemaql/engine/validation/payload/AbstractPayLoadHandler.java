package com.lemon.schemaql.engine.validation.payload;

import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.exception.support.Message;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.exception.InputNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import javax.validation.ConstraintViolation;
import javax.validation.metadata.ConstraintDescriptor;

/**
 * 名称：验证消息处理器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
@Slf4j
public abstract class AbstractPayLoadHandler<T> implements IPayloadHandler<T> {

    @SuppressWarnings("unchecked")
    public static void processPayload(@NonNull InputNotValidException exception, @NonNull ConstraintViolation<Object> violation) {
        if (null == violation.getConstraintDescriptor().getPayload() ||
                violation.getConstraintDescriptor().getPayload().size() <= 0) {
            exception.addError("", violation.getMessage());
            return;
        }

        violation.getConstraintDescriptor().getPayload().forEach(p -> {
            if (ErrorPayload.class.isAssignableFrom(p)) {
                // 错误信息
                try {
                    exception.addError(
                            ((ErrorPayload) p.getDeclaredConstructor().newInstance())
                                    .getMessage(violation));
                } catch (Exception e) {
                    new ExceptionBuilder<SystemException>()
                            .messageTemplate("The payload class [" + p.getSimpleName() + "] no default constructor.")
                            .throwIt();
                }
            } else if (InfoPayLoad.class.isAssignableFrom(p)) {
                // 普通信息
                try {
                    exception.addInfo(
                            ((InfoPayLoad) p.getDeclaredConstructor().newInstance())
                                    .getMessage(violation));
                } catch (Exception e) {
                    new ExceptionBuilder<SystemException>()
                            .messageTemplate("The payload class [" + p.getSimpleName() + "] no default constructor.")
                            .throwIt();
                }
            }
        });
    }

    @Override
    public Message getMessage(ConstraintViolation<T> violation) {
        // 是否国际化
        if (violation.getMessageTemplate().startsWith("%")) {
            LoggerUtils.debug(log, formatMessage(violation));

            ConstraintDescriptor descriptor = violation.getConstraintDescriptor();
            descriptor.getComposingConstraints();
            descriptor.getMessageTemplate();    // 没经过插值的信息
            descriptor.getAnnotation();         // 验证类型
            descriptor.getAttributes();         //
            descriptor.getGroups();             // 分组类列表
            descriptor.getPayload();            // payload列表
            descriptor.getConstraintValidatorClasses(); // 得到需要作用在此约束上的所有校验器ConstraintValidator

            String message = MessageSourceHandler.getMessageBySourceName(
                    BeanNameConstants.VALIDATION_MESSAGE_SOURCE,
                    violation.getMessageTemplate().substring(1));
            return new Message(StringUtils.EMPTY, message);
        } else {
            // 非国际化
            return new Message(StringUtils.EMPTY, violation.getMessage());
        }
    }

    private String formatMessage(ConstraintViolation<T> violation) {
        return "Validation failed." +
                " " +
                violation.getPropertyPath() +
                " " +
                violation.getMessage() +
                " but got " +
                violation.getInvalidValue();
    }
}
