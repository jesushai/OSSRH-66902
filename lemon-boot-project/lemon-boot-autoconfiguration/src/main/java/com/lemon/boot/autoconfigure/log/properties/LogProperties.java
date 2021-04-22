package com.lemon.boot.autoconfigure.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/14
 */
@Data
@ConfigurationProperties(prefix = "zh.log", ignoreInvalidFields = true)
public class LogProperties {
    private String active;
    private Serilog serilog;

    @Data
    public static class Serilog {

        private boolean enabledConsole;

        private boolean enabledRollingFile;

        /**
         * 默认：{Timestamp:yyyy-MM-dd HH:mm:ss.SSS zzz} [{Level}] {Message}{NewLine}{Exception}
         */
        private String outputTemplate = "{Timestamp:yyyy-MM-dd HH:mm:ss.SSS zzz} [{Level}] {Message}{NewLine}{Exception}";

        /**
         * 例子：test-{Date}.log
         */
        private String rollingFile = "serilog-{Date}.log";

        /**
         * 每个文件上限大小，默认1G
         */
        private long fileSizeLimitBytes = 1073741824L;

        /**
         * 最多保留多少文件，默认31个
         */
        private int retainedFileCountLimit = 31;

        /**
         * 例子：http://localhost:5341
         */
        private String seqUrl;
    }
}
