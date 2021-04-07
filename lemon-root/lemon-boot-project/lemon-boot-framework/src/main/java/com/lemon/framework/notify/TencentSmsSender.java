package com.lemon.framework.notify;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.lemon.framework.util.LoggerUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/*
 * 腾讯云短信服务
 */
@Setter
@Slf4j
public class TencentSmsSender implements SmsSender {

    private SmsSingleSender sender;
    private String sign;

    @Override
    public SmsResult send(String phone, String content) {
        try {
            LoggerUtils.debug(log, "Send sms to {}: {}", phone, content);
            SmsSingleSenderResult result = sender.send(0, "86", phone, content, "", "");
            LoggerUtils.debug(log, result.toString());
            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        try {
            LoggerUtils.debug(log, "Send sms to {}, template code is {}", phone, templateId);
            SmsSingleSenderResult result = sender.sendWithParam("86", phone, Integer.parseInt(templateId), params, this.sign, "", "");
            LoggerUtils.debug(log, result.toString());
            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(result);
            return smsResult;
        } catch (HTTPException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
