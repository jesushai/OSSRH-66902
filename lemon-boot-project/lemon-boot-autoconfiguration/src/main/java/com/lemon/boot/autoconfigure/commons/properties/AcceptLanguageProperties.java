package com.lemon.boot.autoconfigure.commons.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@SuppressWarnings("unused")
@ConfigurationProperties(prefix = "zh.http.accept-language")
public class AcceptLanguageProperties {

    public static String HEADER_ACCEPT_LANGUAGE = "header-accept-language";
    public static String REQUEST_PARAM_LANG = "request-param-lang";

    /**
     * <li>header-accept-language通过Headers: Accept-Language的值来确定请求的国际化</li>
     * 例子：<code>Accept-Language=zh_CN</code>
     * <li>request-param-lang通过请求参数lang的值来确定请求的国际化</li>
     * 例子：<code>http://address/api?lang=zh_CN</code>
     * <p/>
     * 默认：header-accept-language
     */
    private String type = HEADER_ACCEPT_LANGUAGE;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
