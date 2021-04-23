package com.lemon.framework.exception;

import com.lemon.framework.handler.MessageSourceHandler;

/**
 * 名称：异常构建器<p>
 * 描述：<p>
 * 实现响应式操作，可选是否国际化，是否使用默认模板消息
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public final class ExceptionBuilder<T extends LoggableRuntimeException> {

    private String code;
    private String messageTemplate;
    private Object[] args;
    private final Class<T> exceptionClass;

    public ExceptionBuilder(Class<T> clazz) {
        this.exceptionClass = clazz;
    }

    /**
     * 默认BusinessException
     */
    @SuppressWarnings("unchecked")
    public ExceptionBuilder() {
        this.exceptionClass = (Class<T>) BusinessException.class;
    }

    /**
     * 设置异常代码<p>
     * 默认使用国际化
     *
     * @param code 异常代码
     * @return this.
     */
    public ExceptionBuilder<T> code(String code) {
        this.code = code;
        return this;
    }

    /**
     * 使用默认模板，如果没有国际化则使用
     *
     * @param messageTemplate Message template.
     * @return this.
     */
    public ExceptionBuilder<T> messageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
        return this;
    }

    /**
     * 消息变量
     *
     * @param args 参数
     * @return this.
     */
    public ExceptionBuilder<T> args(Object... args) {
        this.args = args;
        return this;
    }

    /**
     * 构建异常实例
     *
     * @return 最终构建的异常
     */
    public T build() {
        T th;
        try {
            th = exceptionClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("The exception class [" + exceptionClass.getSimpleName() + "] no default constructor.");
        }

        // 为空则使用默认code
        if (null != this.code) {
            th.setCode(this.code);
        }
        // 根据开关尝试国际化
        th.setMessage(
                MessageSourceHandler.getMessageOrNonLocale(
                        this.code,
                        this.messageTemplate,
                        this.args)
        );
        return th;
    }

    /**
     * 直接抛出异常
     */
    public void throwIt() {
        throw build();
    }
}
