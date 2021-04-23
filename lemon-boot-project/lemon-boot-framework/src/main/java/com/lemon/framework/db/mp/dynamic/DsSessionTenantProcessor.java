package com.lemon.framework.db.mp.dynamic;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/13
 */
public class DsSessionTenantProcessor extends DsProcessor {

    private static final String SESSION_PREFIX = "#session.tenant";

    public boolean matches(String key) {
        return key.startsWith(SESSION_PREFIX);
    }

    public String doDetermineDatasource(MethodInvocation invocation, String key) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new RuntimeException("RequestContextHolder.getRequestAttributes() is null.");
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String dsName;
        if (key.contains("'")) {
            dsName = "tenant" + request.getSession().getAttribute("tenant") + key.substring(key.indexOf("'") + 1, key.lastIndexOf("'"));
        } else {
            dsName = "tenant" + request.getSession().getAttribute("tenant");
        }

        return dsName;
    }
}
