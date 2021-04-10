package com.lemon.boot.autoconfigure.security.shiro;

import com.lemon.framework.exception.AuthenticationException;
import com.lemon.framework.exception.AuthorizationException;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
@ControllerAdvice
@ResponseBody
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ShiroExceptionHandlerAdvice {

    /**
     * 身份验证失败
     */
    @ExceptionHandler(org.apache.shiro.authc.AuthenticationException.class)
    public Object unauthenticatedHandler(org.apache.shiro.authc.AuthenticationException e) {
        LoggerUtils.error(log, e);
        return Result.error(new AuthenticationException());
    }

    /**
     * 未经授权的访问
     */
    @ExceptionHandler(org.apache.shiro.authz.AuthorizationException.class)
    public Object unauthorizedHandler(org.apache.shiro.authz.AuthorizationException e) {
        LoggerUtils.error(log, e);
        return Result.error(
                new ExceptionBuilder<>(AuthorizationException.class)
                        .code("UNAUTHORIZED")
                        .build()
        );
    }

}
