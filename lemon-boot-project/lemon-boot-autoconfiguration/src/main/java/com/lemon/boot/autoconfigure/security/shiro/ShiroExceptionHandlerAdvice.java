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
 * 名称：<p>
 * 描述：<p>
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
     *
     * @param e 身份验证失败异常
     * @return 异常结果
     */
    @ExceptionHandler(org.apache.shiro.authc.AuthenticationException.class)
    public Object unauthenticatedHandler(org.apache.shiro.authc.AuthenticationException e) {
        LoggerUtils.error(log, e);
        return Result.error(new AuthenticationException());
    }

    /**
     * 未经授权的访问
     *
     * @param e 异常
     * @return 异常结果
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
