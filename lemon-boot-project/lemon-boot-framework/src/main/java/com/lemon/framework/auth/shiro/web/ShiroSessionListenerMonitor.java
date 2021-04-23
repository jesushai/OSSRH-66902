package com.lemon.framework.auth.shiro.web;

import com.lemon.framework.auth.shiro.ShiroSessionListener;
import com.lemon.framework.core.annotation.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 名称：Shiro Session的监听器类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/27
 */
@Monitor
@RequestMapping("/sys/monitor/session")
public class ShiroSessionListenerMonitor {

    @Autowired
    @Lazy
    private ShiroSessionListener sessionListener;

    /**
     * 获取Session的数量
     *
     * @return Session的总数
     * <p>
     * 在分布式环境下这仅仅只是单个服务的数量，
     * 未来需要考虑返回Redis中的所有可用的session数量。
     */
    @GetMapping("/count")
    public Long getSessionCount() {
        return sessionListener.getSessionCount().get();
    }
}
