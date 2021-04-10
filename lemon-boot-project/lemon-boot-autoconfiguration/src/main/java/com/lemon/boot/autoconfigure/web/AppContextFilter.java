package com.lemon.boot.autoconfigure.web;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.core.context.AppContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import javax.servlet.*;
import java.io.IOException;

/**
 * <b>名称：应用请求上下文过滤器</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/13
 */
public class AppContextFilter implements Filter, Ordered {

    @Autowired(required = false)
    private AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {
            /*
             * 缓存会话信息
             */
            if (null != authenticationService) {
                authenticationService.getSubject();
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } finally {
            // 清理请求的上下文缓存
            AppContextHolder.clearContext();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
