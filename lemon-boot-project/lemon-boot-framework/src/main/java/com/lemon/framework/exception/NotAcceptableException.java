package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：不可接受的请求或内容<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019/9/28
 */
@SuppressWarnings("unused")
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class NotAcceptableException extends BusinessException {

    private static final long serialVersionUID = 1378566706629927763L;

    public NotAcceptableException() {
        this.code = "" + HttpStatus.NOT_ACCEPTABLE.value();
    }

}
