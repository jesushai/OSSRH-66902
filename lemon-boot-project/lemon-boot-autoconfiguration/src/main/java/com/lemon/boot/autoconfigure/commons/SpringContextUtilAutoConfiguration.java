package com.lemon.boot.autoconfigure.commons;

import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/9/6
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
public class SpringContextUtilAutoConfiguration {

    @Bean(BeanNameConstants.SPRING_CONTEXT_UTILS)
    @ConditionalOnMissingBean(name = BeanNameConstants.SPRING_CONTEXT_UTILS)
    public SpringContextUtils springContextUtils() {
        return new SpringContextUtils();
    }
}
