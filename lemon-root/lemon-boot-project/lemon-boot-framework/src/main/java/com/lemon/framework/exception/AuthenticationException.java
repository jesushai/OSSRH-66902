package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>名称：身份验证失败</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends BusinessException {

    private static final long serialVersionUID = 2627179555859252647L;

    public AuthenticationException() {
        this.code = "401";
    }

}
