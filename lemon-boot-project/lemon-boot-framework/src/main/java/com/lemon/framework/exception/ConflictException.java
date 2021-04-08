package com.lemon.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 名称：冲突（乐观锁）
 * 描述：
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends BusinessException {

    private static final long serialVersionUID = -8549051117867249477L;

    public ConflictException() {
        this.code = "REV-CONFLICT";
    }

}
