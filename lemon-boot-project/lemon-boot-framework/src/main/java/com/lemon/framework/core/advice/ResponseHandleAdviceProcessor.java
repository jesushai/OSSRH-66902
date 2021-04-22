package com.lemon.framework.core.advice;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019-10-10
 */
public interface ResponseHandleAdviceProcessor extends ResponseBodyAdvice<Object>, Ordered {

}
