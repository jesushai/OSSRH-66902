package com.lemon.framework.util;

import com.lemon.framework.exception.LoggableRuntimeException;
import org.slf4j.Logger;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public class LoggerUtils {

    public static void error(Logger log, Exception e) {
        if (log == null || e == null) {
            return;
        }
        if (e instanceof LoggableRuntimeException) {
            LoggableRuntimeException le = (LoggableRuntimeException) e;
            le.setLogged(true);
        }
        log.error(e.getMessage());
    }

    public static void error(Logger log, String msg, Object... args) {
        if (log == null || msg == null) {
            return;
        }
        log.error(msg, args);
    }

    public static void debug(Logger log, String msg, Object... args) {
        if (log == null || msg == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(msg, args);
        }
    }

    public static void info(Logger log, String msg, Object... args) {
        if (log == null || msg == null) {
            return;
        }
        log.info(msg, args);
    }

    public static void warn(Logger log, String msg, Object... args) {
        if (log == null || msg == null) {
            return;
        }
        log.warn(msg, args);
    }
}
