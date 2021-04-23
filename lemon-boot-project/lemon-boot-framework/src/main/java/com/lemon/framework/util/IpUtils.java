package com.lemon.framework.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class IpUtils {

    private static final String UNKNOWN = "unknown";

    public static String getRequestHost() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            if (null != request) {
                return IpUtils.getRequestHost(request);
            }
        }
        return UNKNOWN;
    }

    /**
     * 获取 IP地址
     * 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()获取 IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     *
     * @param request Http请求
     * @return 真实IP地址
     */
    public static String getRequestHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (null == ip || 0 == ip.length() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (null == ip || 0 == ip.length() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (null == ip || 0 == ip.length() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
//            if (ip.equals("127.0.0.1")) {
//                // 根据网卡取本机配置的IP
//                InetAddress inet = null;
//                try {
//                    inet = InetAddress.getLocalHost();
//                    ip = inet.getHostAddress();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//            }
        }

        return getRealIp(ip);
    }

    /**
     * 获取 IP地址
     * 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()获取 IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，
     * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
     *
     * @param request Http请求
     * @return 真实IP地址
     */
    public static String getRequestHost(ServerHttpRequest request) {
        String ip = getHeaderValue(request, "x-forwarded-for");

        if (null == ip) {
            ip = getHeaderValue(request, "Proxy-Client-IP");
        }

        if (null == ip) {
            ip = getHeaderValue(request, "WL-Proxy-Client-IP");
        }

        if (null == ip) {
            ip = request.getRemoteAddress().getHostString();
        }

        return getRealIp(ip);
    }

    private static String getRealIp(String ip) {
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    private static String getHeaderValue(ServerHttpRequest request, String key) {
        HttpHeaders headers = request.getHeaders();
        List<String> values = headers.get(key);
        if (null == values || values.size() == 0) {
            return null;
        }
        String value = values.get(0);
        if (StringUtils.isEmpty(value) || UNKNOWN.equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }

}
