package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：业务异常
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends LoggableRuntimeException {

    private static final long serialVersionUID = 128553716825679746L;

    public BusinessException() {
    }

}
