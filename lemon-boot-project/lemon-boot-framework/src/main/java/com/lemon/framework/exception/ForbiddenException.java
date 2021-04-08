package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：禁止的业务异常
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends BusinessException {

    private static final long serialVersionUID = -4691131997939744738L;

    public ForbiddenException() {
        this.code = "" + HttpStatus.FORBIDDEN.value();
    }

}
