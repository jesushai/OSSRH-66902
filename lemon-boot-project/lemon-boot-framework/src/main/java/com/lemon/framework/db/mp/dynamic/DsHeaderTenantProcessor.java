package com.lemon.framework.db.mp.dynamic;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <b>名称：自定义多租户数据源解析器</b><br/>
 * <b>描述：</b><br/>
 * 租户由header传入<br/>
 * <code>tenant=100</code>
 * <p/>
 * 数据源配置：
 * <pre>
 *   tenant100-master:
 *      ...
 *   tenant100-slave_1:
 *      ...
 * </pre>
 * <p>
 * <p/>
 *
 * <pre>
 * 数据源定义：
 *   <code>@DS("#header.tenant + '-master'")</code>
 *   <code>@DS("#header.tenant + '-slave'")</code>
 * </pre>
 *
 * @author hai-zhang
 * @since 2020-5-12
 */
public class DsHeaderTenantProcessor extends DsProcessor {

    private static final String HEADER_PREFIX = "#header.tenant";

    @Override
    public boolean matches(String key) {
        return key.startsWith(HEADER_PREFIX);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation methodInvocation, String key) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new RuntimeException("RequestContextHolder.getRequestAttributes() is null.");
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String dsName;
        if (key.contains("'")) {
            dsName = "tenant" + request.getHeader("tenant") + key.substring(key.indexOf("'") + 1, key.lastIndexOf("'"));
        } else {
            dsName = "tenant" + request.getHeader("tenant");
        }

        return dsName;
    }
}
