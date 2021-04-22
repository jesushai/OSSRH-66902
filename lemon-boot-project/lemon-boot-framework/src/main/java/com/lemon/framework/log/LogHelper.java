package com.lemon.framework.log;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.domain.event.DefaultEventPublisher;
import com.lemon.framework.log.event.LogEventSupport;
import com.lemon.framework.util.IpUtils;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Optional;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
public class LogHelper {

    private static DefaultEventPublisher eventPublisher = null;
    private static AuthenticationService authenticationService = null;

    /**
     * 生成日志并异步发送保存
     *
     * @param biz         日志业务
     * @param description 日志内容
     * @return 保存后的日志
     */
    public static ApiLog log(String biz, String description) {
        return log(LogTypeEnum.BUSINESS.name(), biz, StringUtils.EMPTY, 0L, description, 0L);
    }

    /**
     * 生成日志并异步发送保存
     *
     * @param biz          日志业务
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param description  日志内容
     * @return 保存后的日志
     */
    public static ApiLog log(String biz, String resourceType, Long resourceId, String description) {
        return log(LogTypeEnum.BUSINESS.name(), biz, resourceType, resourceId, description, 0L);
    }

    /**
     * 生成日志并异步发送保存
     *
     * @param type         日志类型
     * @param biz          日志业务
     * @param resourceType 资源类型
     * @param resourceId   资源ID
     * @param description  日志内容
     * @param spend        运行耗时
     * @return 保存后的日志
     */
    public static ApiLog log(String type, String biz, String resourceType, Long resourceId, String description, long spend) {
        if (null == eventPublisher) {
            eventPublisher = SpringContextUtils.getBean(DefaultEventPublisher.class);
        }
        if (null == authenticationService) {
            authenticationService = SpringContextUtils.getBean(AuthenticationService.class);
        }

        ApiLog log = new ApiLog();
        log.setBizName(biz);
        log.setDatetime(new Date());
        log.setDescription(description);
        log.setIp(IpUtils.getRequestHost());
        log.setResourceType(resourceType);
        log.setType(type);
        log.setResourceId(resourceId);
        Optional.ofNullable(authenticationService.getSession())
                .ifPresent(session -> log.setSessionId(session.getId()));
        Optional.ofNullable(authenticationService.getPrincipal())
                .ifPresent(principal -> {
                    log.setTenant(principal.getTenant());
                    log.setUserId(principal.getId());
                    log.setUsername(principal.getUsername());
                });
        log.setSpend(spend);

        eventPublisher.publish(new LogEventSupport<>(log));
        return log;
    }
}
