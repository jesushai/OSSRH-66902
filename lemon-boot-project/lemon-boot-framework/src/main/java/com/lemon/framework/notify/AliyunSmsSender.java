package com.lemon.framework.notify;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.LoggerUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/*
 * 阿里云短信服务
 */
@Setter
@Slf4j
public class AliyunSmsSender implements SmsSender {

    private String regionId;
    private String accessKeyId;
    private String accessKeySecret;
    private String sign;

    @Override
    public SmsResult send(String phone, String content) {
        throw new RuntimeException("Alicloud does not support non template sending.");
    }

    @Override
    public SmsResult sendWithTemplate(String phone, String templateId, String[] params) {
        DefaultProfile profile = DefaultProfile.getProfile(this.regionId, this.accessKeyId, this.accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        LoggerUtils.debug(log, "Send sms message to {}, template code is {}.", phone, templateId);
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", this.regionId);
        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", this.sign);
        request.putQueryParameter("TemplateCode", templateId);
        /*
          NOTE：阿里云短信和腾讯云短信这里存在不一致
          腾讯云短信模板参数是数组，因此短信模板形式如 “短信参数{1}， 短信参数{2}”
          阿里云短信模板参数是JSON，因此短信模板形式如“短信参数{param1}， 短信参数{param2}”
          为了保持统一，我们假定阿里云短信里面的参数是code，code1，code2...

          如果开发者在阿里云短信申请的模板参数是其他命名，请开发者自行调整这里的代码，或者直接写死。
         */
        String templateParam = "{}";
        if (params.length == 1) {
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            templateParam = JacksonUtils.toJson(data);
        } else if (params.length > 1) {
            Map<String, String> data = new HashMap<>();
            data.put("code", params[0]);
            for (int i = 1; i < params.length; i++) {
                data.put("code" + i, params[i]);
            }
            templateParam = JacksonUtils.toJson(data);
        }
        request.putQueryParameter("TemplateParam", templateParam);

        try {
            CommonResponse response = client.getCommonResponse(request);
            SmsResult smsResult = new SmsResult();
            smsResult.setSuccessful(true);
            smsResult.setResult(response);
            return smsResult;
        } catch (ClientException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
