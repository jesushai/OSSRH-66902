package com.lemon.boot.autoconfigure.log;

import com.lemon.boot.autoconfigure.log.properties.LogProperties;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.log.serilog.TenantUserLogContextEnricher;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.i18n.LocaleContextHolder;
import serilogj.Log;
import serilogj.LoggerConfiguration;
import serilogj.events.LogEventLevel;
import serilogj.formatting.display.MessageTemplateTextFormatter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import static serilogj.sinks.coloredconsole.ColoredConsoleSinkConfigurator.coloredConsole;
import static serilogj.sinks.rollingfile.RollingFileSinkConfigurator.rollingFile;
import static serilogj.sinks.seq.SeqSinkConfigurator.seq;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/14
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LogProperties.class)
@DependsOn(BeanNameConstants.SPRING_CONTEXT_UTILS)
public class LogAutoConfiguration {

    /**
     * 自动挂载Serilogj
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(LoggerConfiguration.class)
    @ConditionalOnProperty(prefix = "zh.log", name = "active", havingValue = "serilog")
    public static class SerilogAutoConfiguration {

        @Resource
        private LogProperties properties;

        @PostConstruct
        public void init() {
            LoggerConfiguration configuration = new LoggerConfiguration()
                    .setMinimumLevel(LogEventLevel.Verbose)
                    .with(new TenantUserLogContextEnricher())
//                    .with(new UserDestructor())
//                    .with(new TenantDestructor())
                    ;

            if (properties.getSerilog().isEnabledConsole()) {
                configuration.writeTo(coloredConsole(
                        properties.getSerilog().getOutputTemplate(),
                        LocaleContextHolder.getLocale()
                ));
            }

            if (properties.getSerilog().isEnabledRollingFile()) {
                configuration.writeTo(
                        rollingFile(
                                properties.getSerilog().getRollingFile(),
                                properties.getSerilog().getFileSizeLimitBytes(),
                                properties.getSerilog().getRetainedFileCountLimit(),
                                false,
                                new MessageTemplateTextFormatter(
                                        properties.getSerilog().getOutputTemplate(),
                                        LocaleContextHolder.getLocale()
                                ))
                );
            }

            Log.setLogger(configuration
                    .writeTo(seq(properties.getSerilog().getSeqUrl()))
                    .createLogger());

            LoggerUtils.debug(log, "Init Serilogj: {}.", properties.getSerilog().getSeqUrl());
        }

        /**
         * 服务停止的时候需要释放serilog configuration通过withTo打开的资源（如log文件）
         */
        @PreDestroy
        public void destroy() {
            LoggerUtils.debug(log, "Destroy serilog all event skins.");
            Log.closeAndFlush();
        }
    }
}
