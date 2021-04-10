package com.lemon.boot.autoconfigure.event;

import com.lemon.framework.domain.event.DefaultEventPublisher;
import com.lemon.framework.domain.event.EventPublisher;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/10/8
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DefaultEventPublisher.class)
public class DefaultEventPublisherAutoConfiguration {

    @Bean(EventPublisher.EVENT_PUBLISHER_BEAN)
    public DefaultEventPublisher defaultEventPublisher() {
        LoggerUtils.debug(log, "DefaultEventPublisher in applicationContext");
        return new DefaultEventPublisher();
    }
}
