package com.lemon.boot.autoconfigure.notify;

import com.github.qcloudsms.SmsSingleSender;
import com.lemon.boot.autoconfigure.notify.properties.NotifyProperties;
import com.lemon.framework.notify.AliyunSmsSender;
import com.lemon.framework.notify.NotifyMailService;
import com.lemon.framework.notify.NotifySmsService;
import com.lemon.framework.notify.TencentSmsSender;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyAutoConfiguration {

    @Configuration
    @ConditionalOnClass({JavaMailSender.class, MimeMessage.class})
    public static class JavaMailSenderAutoConfiguration {

        private final NotifyProperties properties;

        public JavaMailSenderAutoConfiguration(NotifyProperties properties) {
            this.properties = properties;
        }

        @Bean
        public JavaMailSender mailSender() {
            NotifyProperties.Mail mailConfig = properties.getMail();
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            if (mailConfig != null && mailConfig.isEnabled()) {
                mailSender.setHost(mailConfig.getHost());
                mailSender.setUsername(mailConfig.getUsername());
                mailSender.setPassword(mailConfig.getPassword());
                mailSender.setPort(mailConfig.getPort());
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", true);
                properties.put("mail.smtp.timeout", 5000);
                properties.put("mail.smtp.starttls.enable", true);
                properties.put("mail.smtp.socketFactory.fallback", "false");
                //阿里云 必须加入配置 outlook配置又不需要 视情况而定.发送不成功多数是这里的配置问题
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.put("mail.smtp.socketFactory.port", mailConfig.getPort());
                properties.put("debug", true);
                mailSender.setJavaMailProperties(properties);
            }
            return mailSender;
        }

        @Bean
        public NotifyMailService notifyMailService() {
            NotifyProperties.Mail mailConfig = properties.getMail();
            NotifyMailService mailService = new NotifyMailService();
            if (mailConfig != null && mailConfig.isEnabled()) {
                mailService.setMailSender(mailSender());
                mailService.setSendFrom(mailConfig.getSendFrom());
                mailService.setSendTo(mailConfig.getSendTo());
            }
            LoggerUtils.debug(log, "NotifyMailService in applicationContext");
            return mailService;
        }
    }

    @Configuration
    public static class NotifyServiceAutoConfiguration {

        private final NotifyProperties properties;

        public NotifyServiceAutoConfiguration(NotifyProperties properties) {
            this.properties = properties;
        }

        @Bean
        public NotifySmsService notifySmsService() {
            NotifyProperties.Sms smsConfig = properties.getSms();
            NotifySmsService smsService = new NotifySmsService();
            if (smsConfig != null && smsConfig.isEnabled()) {
                if ("tencent".equals(smsConfig.getActive())) {
                    smsService.setSmsSender(tencentSmsSender());
                } else if ("aliyun".equals(smsConfig.getActive())) {
                    smsService.setSmsSender(aliyunSmsSender());
                }
                smsService.setSmsTemplate(smsConfig.getTemplate());
            }
            LoggerUtils.debug(log, "NotifySmsService in applicationContext");
            return smsService;
        }

        private TencentSmsSender tencentSmsSender() {
            NotifyProperties.Sms smsConfig = properties.getSms();
            TencentSmsSender smsSender = new TencentSmsSender();
            NotifyProperties.Sms.Tencent tencent = smsConfig.getTencent();
            smsSender.setSender(new SmsSingleSender(tencent.getAppid(), tencent.getAppkey()));
            smsSender.setSign(smsConfig.getSign());
            return smsSender;
        }

        private AliyunSmsSender aliyunSmsSender() {
            NotifyProperties.Sms smsConfig = properties.getSms();
            AliyunSmsSender smsSender = new AliyunSmsSender();
            NotifyProperties.Sms.Aliyun aliyun = smsConfig.getAliyun();
            smsSender.setSign(smsConfig.getSign());
            smsSender.setRegionId(aliyun.getRegionId());
            smsSender.setAccessKeyId(aliyun.getAccessKeyId());
            smsSender.setAccessKeySecret(aliyun.getAccessKeySecret());
            return smsSender;
        }
    }

}
