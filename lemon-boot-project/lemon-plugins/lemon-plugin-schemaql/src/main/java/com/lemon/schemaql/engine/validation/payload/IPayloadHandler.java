package com.lemon.schemaql.engine.validation.payload;

import javax.validation.ConstraintViolation;

/**
 * <b>名称：验证消息处理器</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public interface IPayloadHandler<T> {

    String getMessage(ConstraintViolation<T> violation);
}
