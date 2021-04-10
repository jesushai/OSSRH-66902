package com.lemon.boot.autoconfigure.notify.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "zh.notify")
public class NotifyProperties {
    private Mail mail;
    private Sms sms;

    @Data
    public static class Mail {
        private boolean enabled;
        private String host;
        private String username;
        private String password;
        private String sendFrom;
        private String sendTo;
        private Integer port;
    }

    @Data
    public static class Sms {
        private boolean enabled;
        private String active;
        private String sign;
        private Tencent tencent;
        private Aliyun aliyun;
        private List<Map<String, String>> template = new ArrayList<>();

        @Data
        public static class Tencent {
            private int appid;
            private String appkey;
        }

        @Data
        public static class Aliyun {
            private String regionId;
            private String accessKeyId;
            private String accessKeySecret;
        }
    }
}
