package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：未匹配异常
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@SuppressWarnings("unused")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BusinessException {

    private static final long serialVersionUID = -2502227632249026280L;

    public NotFoundException() {
        this.code = "" + HttpStatus.NOT_FOUND.value();
    }

}
