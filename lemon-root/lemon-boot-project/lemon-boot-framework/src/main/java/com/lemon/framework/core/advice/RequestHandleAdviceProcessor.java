package com.lemon.framework.core.advice;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/10/10
 */
public interface RequestHandleAdviceProcessor extends RequestBodyAdvice, Ordered {

    boolean before();

    boolean after();

    boolean empty();

}
