package com.lemon.boot.autoconfigure.advice.exception;

import com.lemon.framework.domain.event.DefaultEventPublisher;
import com.lemon.framework.exception.LoggableRuntimeException;
import com.lemon.framework.exception.support.ErrorMessage;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@ControllerAdvice
@ResponseBody
@Slf4j
@SuppressWarnings("unused")
public class ExceptionHandlerAdvice {

    @Autowired
    protected DefaultEventPublisher eventPublisher;

    /**
     * 拦截所有的LoggableRuntimeException
     */
    @ExceptionHandler(value = LoggableRuntimeException.class)
    public Result loggableRuntimeExceptionHandler(LoggableRuntimeException exception, WebRequest request) {
//        eventPublisher.publish(new LogEventBase<>(new ExceptionLog(exception)));
        return Result.error(exception);
    }

    /**
     * 参数{@code @Validated}未通过
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException excetion, WebRequest request) {
        List<ObjectError> errors = excetion.getBindingResult().getAllErrors();
        Result result = new Result("500", "请求参数未通过验证");
        errors.forEach(v -> result.addError(
                new ErrorMessage(
                        "ARG-NOT-VALID",
                        MessageSourceHandler.getMessageOrNonLocale(
                                "ARG-NOT-VALID",
                                "{0}",
                                MessageSourceHandler.getMessage(v.getDefaultMessage())
                        ))
        ));
//        eventPublisher.publish(new LogEventBase<>(new ErrorMessageLog(result.getErrors())));
        return result;
    }

    /**
     * 拦截剩余所有未拦截的exception
     */
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception exception, WebRequest request) {
        LoggerUtils.error(log, exception);
        exception.printStackTrace(System.err);
//        eventPublisher.publish(new LogEventBase<>(new ExceptionLog(exception)));
        return new Result("500", "Internal Server Error");
    }

}
