package com.lemon.framework.notify;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

/**
 * 名称：邮件通知服务<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/5
 */
@Slf4j
@Setter
@SuppressWarnings("unused")
public class NotifyMailService {

    private MailSender mailSender;
    private String sendFrom;
    private String sendTo;

    public boolean isMailEnable() {
        return mailSender != null;
    }

    /**
     * 邮件消息通知,
     * 接收者在spring.mail.sendTo中指定
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void notifyMail(String subject, String content) {
        _notifyMail(subject, content, sendTo);
    }

    @Async
    public void notifyMail(String subject, String content, String sendTo) {
        _notifyMail(subject, content, sendTo);
    }

    private void _notifyMail(String subject, String content, String sendTo) {
        if (mailSender == null) {
            throw new RuntimeException("MailSender is not configured.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sendFrom);
        message.setTo(sendTo);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

}
