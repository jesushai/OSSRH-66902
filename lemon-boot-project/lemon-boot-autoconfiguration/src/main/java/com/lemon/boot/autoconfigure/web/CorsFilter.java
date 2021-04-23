package com.lemon.boot.autoconfigure.web;

import com.lemon.boot.autoconfigure.web.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 名称：跨域过滤器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/9/23
 */
@Slf4j
@RequiredArgsConstructor
public class CorsFilter implements Filter, Ordered {

    private final CorsProperties corsProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", corsProperties.getAccessControlAllowOrigin()); // *
        response.setHeader("Access-Control-Allow-Methods", corsProperties.getAccessControlAllowMethods()); // POST, PUT, GET, OPTIONS, DELETE
        response.setHeader("Access-Control-Allow-Headers", corsProperties.getAccessControlAllowHeaders()); // Authorization, Content-Type
        response.setHeader("Access-Control-Max-Age", corsProperties.getAccessControlMaxAge()); // 3600
        response.setCharacterEncoding("UTF-8");
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        if ("*".equals(corsProperties.getAccessControlAllowOrigin())) {
            log.error("=================================================");
            log.error("===【警告】：当前已允许跨域访问，生产环境中请关闭===");
            log.error("=================================================");
        }
    }

    @Override
    public void destroy() {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
