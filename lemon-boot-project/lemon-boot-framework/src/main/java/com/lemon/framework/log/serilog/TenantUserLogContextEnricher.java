package com.lemon.framework.log.serilog;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.spring.SpringContextUtils;
import serilogj.core.ILogEventPropertyFactory;
import serilogj.core.enrichers.LogContextEnricher;
import serilogj.events.LogEvent;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/15
 */
public class TenantUserLogContextEnricher extends LogContextEnricher {

    private AuthenticationService authentication = null;

    @Override
    public void enrich(LogEvent logEvent, ILogEventPropertyFactory propertyFactory) {
        if (null == authentication && SpringContextUtils.containsBean(BeanNameConstants.AUTHENTICATION_SERVICE)) {
            authentication = (AuthenticationService) SpringContextUtils.getBean(BeanNameConstants.AUTHENTICATION_SERVICE);
        }
        if (null != authentication) {
            User user = authentication.getPrincipal();
            if (user != null) {
                logEvent.addPropertyIfAbsent(propertyFactory.createProperty("UserInfo", user, true));
                logEvent.addPropertyIfAbsent(propertyFactory.createProperty("TenantId", user.getTenant(), false));
            }
        }
        super.enrich(logEvent, propertyFactory);
    }
}
