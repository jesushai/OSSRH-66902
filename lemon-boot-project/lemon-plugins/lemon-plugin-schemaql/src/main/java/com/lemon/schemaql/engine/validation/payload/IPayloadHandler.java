package com.lemon.schemaql.engine.validation.payload;

import com.lemon.framework.exception.support.Message;

import javax.validation.ConstraintViolation;

/**
 * 名称：验证消息处理器标示接口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public interface IPayloadHandler<T> {

    Message getMessage(ConstraintViolation<T> violation);
}
