package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：时限内访问超过上限<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class LimitAccessException extends BusinessException {

    private static final long serialVersionUID = 2166425638904368178L;

    public LimitAccessException() {
        this.code = "ACCESS-EXCEEDED-LIMIT";
    }

}
