package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：系统异常
 * 描述：如果是系统异常请直接使用ApplicationException
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SystemException extends LoggableRuntimeException {

    private static final long serialVersionUID = 6447016195338751388L;

    public SystemException() {
        this.code = "" + HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

}
