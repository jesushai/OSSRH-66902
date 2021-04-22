package com.lemon.framework.core.advice;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019/10/10
 */
public interface RequestHandleAdviceProcessor extends RequestBodyAdvice, Ordered {

    boolean before();

    boolean after();

    boolean empty();

}
