package com.lemon.framework.notify;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <b>名称：短信通知服务</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/5
 */
@Setter
@Slf4j
@SuppressWarnings("unused")
public class NotifySmsService {

    private SmsSender smsSender;

    private List<Map<String, String>> smsTemplate = new ArrayList<>();

    public boolean isSmsEnable() {
        return smsSender != null;
    }

    private void checkSmsSender() {
        if (smsSender == null) {
            throw new RuntimeException("SmsSender is not configured.");
        }
    }

    /**
     * 短信消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param message     短消息内容，这里短消息内容必须已经在短信平台审核通过
     */
    @Async
    public void notifySms(String phoneNumber, String message) {
        checkSmsSender();
        smsSender.send(phoneNumber, message);
    }

    /**
     * 短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    @Async
    public void notifySmsTemplate(String phoneNumber, String notifyType, String... params) {
        checkSmsSender();
        smsSender.sendWithTemplate(phoneNumber, getTemplateId(notifyType, smsTemplate), params);
    }

    /**
     * 以同步的方式发送短信模版消息通知
     *
     * @param phoneNumber 接收通知的电话号码
     * @param notifyType  通知类别，通过该枚举值在配置文件中获取相应的模版ID
     * @param params      通知模版内容里的参数，类似"您的验证码为{1}"中{1}的值
     */
    public SmsResult notifySmsTemplateSync(String phoneNumber, String notifyType, String... params) {
        checkSmsSender();
        return smsSender.sendWithTemplate(phoneNumber, getTemplateId(notifyType, smsTemplate), params);
    }

    private String getTemplateId(String notifyType, List<Map<String, String>> values) {
        for (Map<String, String> item : values) {
            if (item.get("name").equals(notifyType)) {
                return item.get("templateId");
            }
        }
        throw new RuntimeException("SMS template(" + notifyType + ") not registered in application.");
    }

}
