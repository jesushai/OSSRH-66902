package com.lemon.framework.handler;

import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>名称：国际化消息处理器</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@Slf4j
public class MessageSourceHandler {

    private MessageSourceHandler(String name, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.messageSourceName = name;
    }

    /**
     * 当前系统是否使用国际化
     */
    public static boolean useLocale() {
        return null != PRIMARY;
    }

    /**
     * 创建并缓存国际化消息处理器，第一个创建的就是主消息处理器
     *
     * @param name          国际化消息处理器名称
     * @param messageSource 消息资源
     */
    public static void buildMessageSourceHandler(String name, MessageSource messageSource) {

        MessageSourceHandler messageSourceHandler = new MessageSourceHandler(name, messageSource);

        synchronized (HOLDER) {
            if (HOLDER.containsKey(name)) {
                throw new RuntimeException("The MessageSourceHandler [" + name + "] already exists");
            }

            if (null == PRIMARY) {
                PRIMARY = messageSourceHandler;
            }

            HOLDER.put(name, messageSourceHandler);
        }

        LoggerUtils.debug(log, "MessageSourceHandler [{}] in applicationContext", name);
    }


    // region 静态部分

    private static MessageSourceHandler PRIMARY = null;
    private final static Map<String, MessageSourceHandler> HOLDER = new ConcurrentHashMap<>();

    public static Locale getLocale() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            return RequestContextUtils.getLocale(request);
        }
        return LocaleContextHolder.getLocale();
    }

    /**
     * 根据客户端请求来决定国际化
     */
    public static String getMessage(String key) {
        if (null == PRIMARY) {
            return getMessageSimple(key);
        } else {
            return PRIMARY.getMessageInner(key);
        }
    }

    /**
     * 根据客户端请求来决定国际化
     */
    public static String getMessage(String key, Object... args) {
        if (null == PRIMARY) {
            return getMessageSimple(key, args);
        } else {
            return PRIMARY.getMessageInner(key, args);
        }
    }

    /**
     * 框架底层使用，根据应用是否使用国际化使用不同的key
     *
     * @param messageKey      国际化消息键
     * @param messageTemplate 非国际化的消息模板
     */
    public static String getMessageOrNonLocale(String messageKey, @Nullable String messageTemplate, Object... args) {
        if (null == PRIMARY) {
            return getMessageSimple(messageTemplate, args);
        } else {
            return PRIMARY.getMessageInner(messageKey, args);
        }
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    public static String getMessageDefaultLocale(String key) {
        if (null == PRIMARY) {
            return getMessageSimple(key);
        } else {
            return PRIMARY.getMessageDefaultInner(key);
        }
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    public static String getMessageDefaultLocale(String key, Object... args) {
        if (null == PRIMARY) {
            return getMessageSimple(key, args);
        } else {
            return PRIMARY.getMessageDefaultInner(key, args);
        }
    }

    /**
     * 框架底层使用，根据应用是否使用国际化使用不同的key
     */
    public static String getMessageOrNonLocaleDefaultLocale(String key, String nonLocaleKey, Object... args) {
        if (null == PRIMARY) {
            return getMessageSimple(nonLocaleKey, args);
        } else {
            return PRIMARY.getMessageDefaultInner(key, args);
        }
    }

    /**
     * 根据客户端请求来决定国际化
     */
    public static String getMessageBySourceName(String sourceName, String key) {
        if (MapUtils.isEmpty(HOLDER) || !HOLDER.containsKey(sourceName)) {
            return getMessageSimple(key);
        } else {
            return HOLDER.get(sourceName).getMessageInner(key);
        }
    }

    /**
     * 根据客户端请求来决定国际化
     */
    public static String getMessageBySourceName(String sourceName, String key, Object... args) {
        if (MapUtils.isEmpty(HOLDER) || !HOLDER.containsKey(sourceName)) {
            return getMessageSimple(key, args);
        } else {
            return HOLDER.get(sourceName).getMessageInner(key, args);
        }
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    public static String getMessageBySourceNameDefaultLocale(String sourceName, String key) {
        if (MapUtils.isEmpty(HOLDER) || !HOLDER.containsKey(sourceName)) {
            return getMessageSimple(key);
        } else {
            return HOLDER.get(sourceName).getMessageDefaultInner(key);
        }
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    public static String getMessageBySourceNameDefaultLocale(String sourceName, String key, Object... args) {
        if (MapUtils.isEmpty(HOLDER) || !HOLDER.containsKey(sourceName)) {
            return getMessageSimple(key, args);
        } else {
            return HOLDER.get(sourceName).getMessageDefaultInner(key, args);
        }
    }

    /**
     * 不用国际化，简单返回消息，支持{n}表达式
     */
    private static String getMessageSimple(String messageTemplate, Object... args) {
        if (null == messageTemplate) {
            throw new RuntimeException("The system does not support locale, message template cannot be null.");
        }

        String message = messageTemplate;
        if (ArrayUtils.isNotEmpty(args) && messageTemplate.matches("\\{\\d{1,2}\\}")) {
            // 表达式处理 {0},{1}...
            for (int i = 0; i < args.length; i++) {
                message = message.replace("{" + i + "}", null == args[i] ? "null" : args[i].toString());
            }
        }
        return message;
    }

    // endregion


    // region 非静态部分

    private final String messageSourceName;
    private final MessageSource messageSource;

    /**
     * 根据客户端请求来决定国际化
     */
    private String getMessageInner(String key) {
        return parseMessage(key, getLocale());
    }

    /**
     * 根据客户端请求来决定国际化
     */
    private String getMessageInner(String key, Object... args) {
        return parseMessage(key, getLocale(), args);
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    private String getMessageDefaultInner(String key) {
        return parseMessage(key, LocaleContextHolder.getLocale());
    }

    /**
     * 根据应用部署的服务器系统来决定国际化
     */
    private String getMessageDefaultInner(String key, Object... args) {
        return parseMessage(key, LocaleContextHolder.getLocale(), args);
    }

    private String parseMessage(String messageKey, Locale locale, Object... args) {
        if (useLocale()) {
            try {
                return messageSource.getMessage(messageKey, args, locale);
            } catch (NoSuchMessageException e) {
                LoggerUtils.warn(log, "No such message key \"{}\" in MessageSource [{}]", messageKey, messageSourceName);
                return getMessageSimple(messageKey, args);
            }
        } else {
            return getMessageSimple(messageKey, args);
        }
    }

    // endregion
}
