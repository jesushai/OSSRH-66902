package com.lemon.framework.notify;

import lombok.Data;

/**
 * 发送短信的返回结果
 */
@Data
public class SmsResult {
    private boolean successful;
    private Object result;
}
