package com.lemon.boot.autoconfigure.security.shiro.properties;

import com.lemon.framework.auth.shiro.ShiroWebSessionManager;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/9
 */
@Data
@ConfigurationProperties(prefix = "zh.shiro")
public class ShiroProperties {

    /**
     * session 超时时间，单位为秒
     */
    private int sessionTimeout;
    /**
     * session 刷新策略：实时刷新(Runtime); 临近刷新(Near)
     */
    private String sessionRefreshPolicy = ShiroWebSessionManager.DEFAULT_SESSION_REFRESH_POLICY;
    /**
     * session临近多少秒内才刷新，session-refresh-policy=Near有效，默认10分钟
     */
    private int sessionRefreshNear = ShiroWebSessionManager.DEFAULT_SESSION_REFRESH_NEAR;
    /**
     * session最长存活时间(秒)，>0有效，默认一天
     */
    private int sessionMaxLiveTime = ShiroWebSessionManager.DEFAULT_SESSION_MAX_LIVE_TIME;
    /**
     * rememberMe cookie有效时长，单位为秒
     */
    private int cookieTimeout;
    /**
     * 免认证的路径配置，如静态资源等，逗号分隔
     */
    private String anonUrl;
    /**
     * 登录url
     */
    private String loginUrl;
    /**
     * 登录成功后的首页url
     */
    private String successUrl;
    /**
     * 登出url
     */
    private String logoutUrl;
    /**
     * 未授权跳转url
     */
    private String unauthorizedUrl;

    /**
     * Redis支持
     */
    private ShiroRedisProperties redis;

    @Data
    public static class ShiroRedisProperties {

        /**
         * 缓存session
         */
        private SessionProperties session;
        /**
         * 缓存principal
         */
        private CacheProperties cache;

        @Data
        public static class SessionProperties {
            private String keyPrefix = "shiro-session:";
            private boolean inMemoryEnabled = false;
            /**
             * 单位（秒）
             */
            private int inMemoryTimeout = 300;
        }

        @Data
        public static class CacheProperties {
            private String keyPrefix = "shiro-session:";
            private String principalIdFieldName = "id";
            /**
             * 单位（秒）
             */
            private int timeToLive = 1800;
            /**
             * 单位（秒）
             */
            private int maxIdleTime = 1800;
        }
    }
}
