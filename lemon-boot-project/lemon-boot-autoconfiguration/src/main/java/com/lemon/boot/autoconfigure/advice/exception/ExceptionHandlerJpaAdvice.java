package com.lemon.boot.autoconfigure.advice.exception;

import com.lemon.framework.exception.ConflictException;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.protocal.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@ControllerAdvice
@ResponseBody
@Slf4j
@SuppressWarnings("unused")
public class ExceptionHandlerJpaAdvice extends ExceptionHandlerAdvice {

    /**
     * 乐观锁异常
     *
     * @param exception 乐观锁异常
     * @param request   请求
     * @return 包装错误结果
     */
    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public Result optimisticLockingFailureExceptionHandler(ObjectOptimisticLockingFailureException exception, WebRequest request) {

        String error = String.format("OptimisticLockingException: %s - %s",
                exception.getPersistentClassName(),
                exception.getIdentifier() == null ? "" : exception.getIdentifier().toString());

        log.error(error);

//        eventPublisher.publish(new LogEventBase<>(new ErrorMessageLog(new ErrorMessage("", error))));
        return Result.error(
                new ExceptionBuilder<>(ConflictException.class)
                        .code("REV-CONFLICT")
                        .messageTemplate("您修改的资源已被其他人修改，请刷新后再试！")
                        .build());
    }

}
