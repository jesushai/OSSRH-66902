package com.lemon.boot.autoconfigure.advice.exception;

import com.lemon.framework.domain.event.DefaultEventPublisher;
import com.lemon.framework.exception.LoggableRuntimeException;
import com.lemon.framework.exception.MultiErrorException;
import com.lemon.framework.exception.support.Message;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.exception.InputNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 名称：全局异常通知拦截<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/29
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {

    @Autowired
    protected DefaultEventPublisher eventPublisher;

    /**
     * 拦截所有的LoggableRuntimeException
     *
     * @param exception 异常
     * @return 包装错误结果
     */
    @ExceptionHandler(value = LoggableRuntimeException.class)
    public Result loggableRuntimeExceptionHandler(LoggableRuntimeException exception) {
//        eventPublisher.publish(new LogEventBase<>(new ExceptionLog(exception)));
        return Result.error(exception);
    }

    @ExceptionHandler(value = InputNotValidException.class)
    public Result inputNotValidExceptionHandler(InputNotValidException exception) {
        List<Message> errors = exception.getErrors();
        List<Message> warnings = exception.getInfos();

        Result result = new Result(
                "ARG-NOT-VALID",
                MessageSourceHandler.getMessageOrNonLocale(
                        "ARG-NOT-VALID",
                        "Parameter failed validation!"
                ));

        errors.forEach(error -> result.addError(
                new Message(
                        StringUtils.EMPTY,
                        MessageSourceHandler.getMessageOrNonLocale(
                                error.getCode(),
                                MessageSourceHandler.getMessage(error.getMessage())
                        ))
        ));

        warnings.forEach(warning -> result.addInfo(
                new Message(
                        StringUtils.EMPTY,
                        MessageSourceHandler.getMessageOrNonLocale(
                                warning.getCode(),
                                MessageSourceHandler.getMessage(warning.getMessage())
                        ))
        ));

        return result;
    }

    public Result multiErrorExceptionHandler(MultiErrorException exception) {
        List<Message> errors = exception.getErrors();
        Result result = new Result("500", "Internal Server Error");
        errors.forEach(error -> result.addError(
                new Message(
                        error.getCode(),
                        MessageSourceHandler.getMessageOrNonLocale(
                                error.getCode(),
                                MessageSourceHandler.getMessage(error.getMessage())
                        ))
        ));
        return result;
    }

    /**
     * 参数{@code @Validated}验证未通过
     *
     * @param excetion 异常
     * @return 包装错误结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException excetion) {
        List<ObjectError> errors = excetion.getBindingResult().getAllErrors();

        Result result = new Result(
                "ARG-NOT-VALID",
                MessageSourceHandler.getMessageOrNonLocale(
                        "ARG-NOT-VALID",
                        "Parameter failed validation!"
                ));

        errors.forEach(error -> result.addError(
                new Message(
                        StringUtils.EMPTY,
                        MessageSourceHandler.getMessage(error.getDefaultMessage())
                )
        ));

        return result;
    }

    /**
     * 拦截剩余所有未拦截的exception
     *
     * @param exception 异常
     * @return 包装错误结果
     */
    @ExceptionHandler(value = Exception.class)
    public Result exceptionHandler(Exception exception) {
        LoggerUtils.error(log, exception);
        exception.printStackTrace(System.err);
        return new Result("500", "Internal Server Error");
    }

}
