package com.lemon.framework.exception.support;

import java.util.UUID;

/**
 * 名称：异常ID生成组件<p/>
 * 描述：为定制异常类生成UUID，从而判断异常是否已经写日志，避免重复。<br/>
 *
 * @author hai-zhang
 * @since 2019/8/30
 */
public class ExceptionIDGenerator {

    public static String getExceptionID() {
        String s = UUID.randomUUID().toString();
        return s.replaceAll("-", "");
    }
}
