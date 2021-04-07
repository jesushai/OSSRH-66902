package com.lemon.framework.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.support.NullValue;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public abstract class AbstractAspect implements Ordered {

    protected Method resolveMethod(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> targetClass = point.getTarget().getClass();

        Method method = getDeclaredMethod(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new RuntimeException("Unable to resolve target method: " + signature.getMethod().getName());
        }
        return method;
    }

    protected String[] resolveParameterNames(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        return signature.getParameterNames();
    }

    private Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }

    /**
     * 以方法的参数作为变量计算表达式的结果
     *
     * @param joinPoint  方法切入点
     * @param expression 表达式
     * @return 计算后的结果
     */
    protected String getValueExpression(JoinPoint joinPoint, Expression expression) {
        StandardEvaluationContext context = null;

        String[] paramNames = this.resolveParameterNames(joinPoint);

        if (null != paramNames && paramNames.length > 0) {
            context = new StandardEvaluationContext();

            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], joinPoint.getArgs()[i]);
            }
        }

        Object value;
        if (null == context) {
            value = expression.getValue();
        } else {
            value = expression.getValue(context);
        }

        if (null == value) {
            value = NullValue.INSTANCE;
//            throw new SystemException("Aspect method expression evaluation exception.");
        }

        return value.toString();
    }
}
