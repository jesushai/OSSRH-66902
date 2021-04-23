package com.lemon.schemaql.engine.validation.payload;

import javax.validation.ConstraintViolation;

/**
 * 名称：验证消息处理器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public interface IPayloadHandler<T> {

    String getMessage(ConstraintViolation<T> violation);
}
