package com.lemon.framework.core.aspect;

import com.lemon.framework.constant.AppContextConstants;
import com.lemon.framework.core.annotation.ApiDescription;
import com.lemon.framework.core.context.AppContextHolder;
import com.lemon.framework.domain.event.DefaultEventPublisher;
import com.lemon.framework.handler.MessageSourceHandler;
import com.lemon.framework.log.ApiLog;
import com.lemon.framework.log.LogHelper;
import com.lemon.framework.log.LogTypeEnum;
import com.lemon.framework.log.event.LogEventSupport;
import com.lemon.framework.util.LoggerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 名称：Api日志切片<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class ApiLogAspect extends AbstractAspect implements Ordered {

    private final DefaultEventPublisher eventPublisher;

    @Pointcut("@annotation(com.lemon.framework.core.annotation.ApiDescription)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void beforeExecute(JoinPoint point) {
        // 类上的注解
        ApiDescription classApi = AnnotationUtils.findAnnotation(point.getTarget().getClass(), ApiDescription.class);

        // 方法上的注解
        Method method = resolveMethod(point);
        ApiDescription api = method.getAnnotation(ApiDescription.class);

        String bizName = StringUtils.isEmpty(api.bizName()) && null != classApi ? classApi.bizName() : api.bizName();
        String resourceType = StringUtils.isEmpty(api.resourceType()) && null != classApi ? classApi.resourceType() : api.resourceType();

        // 生成并发送log
        ApiLog apiLog = LogHelper.log(
                api.type().name(),
                bizName,
                resourceType,
                computeResourceId(point, api.resourceId()),
                "Start: " + computeDescription(point, api.description(), api.args()),
                0L
        );

        // 将log缓存到上下文，afterExecute需要用它
        AppContextHolder.getContext().set(AppContextConstants.API_ASPECT_LOG, apiLog);

    }

    @After("pointcut()")
    public void afterExecute() {
        ApiLog apiLog = (ApiLog) AppContextHolder.getContext().get(AppContextConstants.API_ASPECT_LOG);
        if (null == apiLog) {
            LoggerUtils.error(log, "The context api log is lost.");
        } else {
            // 为了避免异步发送日志尚未结束的情况，这里克隆
            apiLog = apiLog.copyTo(ApiLog.class);
            // 更新日志的时间
            Date now = new Date();
            apiLog.setSpend(now.getTime() - apiLog.getDatetime().getTime());
            apiLog.setDatetime(now);
            // 更新日志描述，去掉"Start: "变为"End: "
            apiLog.setDescription("End: " + apiLog.getDescription().substring(7));
            // 发送异步事件
            eventPublisher.publish(new LogEventSupport<>(apiLog));
        }
    }

    @AfterThrowing(value = "pointcut()", throwing = "e")
    public void afterThrowing(Exception e) {
        ApiLog apiLog = (ApiLog) AppContextHolder.getContext().get(AppContextConstants.API_ASPECT_LOG);
        if (null == apiLog) {
            LoggerUtils.error(log, "The context api log is lost.");
        } else {
            // 为了避免异步发送日志尚未结束的情况，这里克隆
            apiLog = apiLog.copyTo(ApiLog.class);
            // 更新日志的时间
            Date now = new Date();
            apiLog.setSpend(now.getTime() - apiLog.getDatetime().getTime());
            apiLog.setDatetime(now);
            // 更新日志类型
            apiLog.setType(LogTypeEnum.ERROR.name());
            // 更新日志描述，去掉"Start: "变为"Error: "
            // 同时追加异常信息
            apiLog.setDescription("Error: " + apiLog.getDescription().substring(7) + "\n" + e.getMessage());
            // 发送异步事件
            eventPublisher.publish(new LogEventSupport<>(apiLog));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10000;
    }

    private long computeResourceId(JoinPoint joinPoint, String expressionStr) {
        if (StringUtils.isEmpty(expressionStr)) {
            return 0L;
        }
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(expressionStr);

        String value = getValueExpression(joinPoint, expression);
        if (StringUtils.isNumeric(value)) {
            return Long.parseLong(value);
        } else {
            return 0L;
        }
    }

    /**
     * 带参数表达式的日志描述
     *
     * @param joinPoint   切片
     * @param description 描述，可带表达式
     * @param args        参数
     * @return 计算表达式后的描述
     */
    private String computeDescription(JoinPoint joinPoint, String description, String[] args) {
        if (StringUtils.isEmpty(description)) {
            return StringUtils.EMPTY;
        }

        // 循环解析
        List<String> values = null;
        if (null != args && args.length > 0) {
            values = new ArrayList<>(args.length);
            ExpressionParser parser = new SpelExpressionParser();

            for (String arg : args) {
                Expression expression = parser.parseExpression(arg);
                values.add(getValueExpression(joinPoint, expression));
            }
        }

        // description
        if (CollectionUtils.isEmpty(values)) {
            return MessageSourceHandler.getMessageDefaultLocale(description);
        } else {
            return MessageSourceHandler.getMessageDefaultLocale(description, values.toArray());
        }
    }
}
