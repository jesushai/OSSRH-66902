package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <b>名称：时限内访问超过上限</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class LimitAccessException extends BusinessException {

    private static final long serialVersionUID = 2166425638904368178L;

    public LimitAccessException() {
        this.code = "ACCESS_EXCEEDED_LIMIT";
    }

}
