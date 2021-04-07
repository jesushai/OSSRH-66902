package com.lemon.framework.auth.shiro.web;

import com.lemon.framework.auth.shiro.ShiroSessionListener;
import com.lemon.framework.core.annotation.Monitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
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

    @GetMapping("/count")
    public Long getSessionCount() {
        return sessionListener.getSessionCount().get();
    }
}
