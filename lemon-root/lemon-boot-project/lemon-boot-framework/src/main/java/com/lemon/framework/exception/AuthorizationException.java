package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：未授权的访问
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthorizationException extends BusinessException {

    private static final long serialVersionUID = 2627179555859252647L;

    public AuthorizationException() {
        this.code = "UNAUTHORIZED";
    }

}
