package com.lemon.framework.db.mp.dynamic;

import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 名称：数据源动态选择<p>
 * 描述：<p>
 * 从当前用户选择租户
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
public class DsPrincipalTenantProcessor extends DsProcessor {

    private static final String PRINCIPAL_PREFIX = "#principal.tenant";

    private AuthenticationService authenticationService = null;

    @Override
    public boolean matches(String key) {
        return key.startsWith(PRINCIPAL_PREFIX);
    }

    @Override
    public String doDetermineDatasource(MethodInvocation methodInvocation, String key) {
        if (null == authenticationService) {
            authenticationService = SpringContextUtils.getBean(AuthenticationService.class);
        }

        // 如果没有用户登录则默认0租户
        User user = authenticationService.getPrincipal();
        Long tenant;
        if (null == user) {
            tenant = 0L;
        } else {
            tenant = user.getTenant();
        }

        String dsName;
        if (key.contains("'")) {
            // tenant+#principal.tenant+'-slave'
            dsName = "tenant" + tenant + key.substring(key.indexOf("'") + 1, key.lastIndexOf("'"));
        } else {
            // tenant+#principal.tenant
            dsName = "tenant" + tenant;
        }

        return dsName;
    }
}
