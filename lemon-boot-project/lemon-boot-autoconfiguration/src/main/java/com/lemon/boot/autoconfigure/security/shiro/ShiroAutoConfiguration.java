package com.lemon.boot.autoconfigure.security.shiro;

import com.lemon.boot.autoconfigure.security.DefaultAuthorizationSupportAutoConfiguration;
import com.lemon.boot.autoconfigure.security.shiro.properties.ShiroProperties;
import com.lemon.boot.autoconfigure.sequence.SequenceGeneratorAutoConfiguration;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.shiro.*;
import com.lemon.framework.auth.shiro.redis.RedisShiroCacheManager;
import com.lemon.framework.auth.shiro.redis.RedisShiroManager;
import com.lemon.framework.auth.shiro.redis.RedisShiroSessionDAO;
import com.lemon.framework.auth.shiro.redisson.RedissonSessionDAO;
import com.lemon.framework.auth.shiro.redisson.RedissonSessionFactory;
import com.lemon.framework.auth.shiro.redisson.RedissonShiroCacheManager;
import com.lemon.framework.auth.shiro.web.ShiroSessionListenerMonitor;
import com.lemon.framework.cache.redis.JacksonRedisTemplate;
import com.lemon.framework.cache.redis.StringByteArrayRedisTemplate;
import com.lemon.framework.constant.BeanNameConstants;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.framework.util.MapUtils;
import com.lemon.framework.util.sequence.SequenceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnClass(DefaultWebSecurityManager.class)
@EnableConfigurationProperties(ShiroProperties.class)
@Import({
        ShiroRequiresPermissionsHelper.class,
        ShiroExceptionHandlerAdvice.class,
        ShiroAuthenticationServiceImpl.class,
        ShiroSessionListenerMonitor.class
})
@AutoConfigureAfter({
        DefaultAuthorizationSupportAutoConfiguration.class,
        SequenceGeneratorAutoConfiguration.class
})
@DependsOn({"redisAutoConfiguration"})
public class ShiroAutoConfiguration {

    public ShiroAutoConfiguration() {
        LoggerUtils.debug(log, "Register ShiroAutoConfiguration.");
    }

    @Resource
    private ApplicationContext ctx;

    /**
     * 巨坑：
     * 一旦你的项目使用了Spring-Boot-Aop
     * 则所有在Shiro中注入的组件都不会使用代理（Jdk&CGLib）
     * 解决方案：@Lazy延迟加载即可
     */
    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private PermissionService permissionService;

    @Bean(BeanNameConstants.SHIRO_KEY)
    @ConditionalOnMissingBean(name = BeanNameConstants.SHIRO_KEY)
    public ShiroKey shiroKey() {
        LoggerUtils.warn(log, "!!!!!Default shiro key used!!!!!");
        return new ShiroKeyDefaultImpl();
    }

    @Configuration
    @ConditionalOnMissingClass("org.redisson.api.RedissonClient")
    public static class RedisDependencyAutoConfiguration {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Bean
        public RedisShiroSessionDAO shiroSessionDAO(StringByteArrayRedisTemplate redisTemplate,
                                                    SequenceGenerator sequenceGenerator,
                                                    ShiroProperties shiroProperties) {

            RedisShiroSessionDAO redisSessionDAO = new RedisShiroSessionDAO(
                    new RedisShiroManager((RedisTemplate) redisTemplate),
                    sequenceGenerator);

            ShiroProperties.ShiroRedisProperties redisProperties = shiroProperties.getRedis();

            if (redisProperties != null) {
                ShiroProperties.ShiroRedisProperties.SessionProperties sessionProperties = redisProperties.getSession();
                if (sessionProperties != null) {
                    redisSessionDAO.setKeyPrefix(sessionProperties.getKeyPrefix());
                    redisSessionDAO.setSessionInMemoryEnabled(sessionProperties.isInMemoryEnabled());
                    redisSessionDAO.setSessionInMemoryTimeout(sessionProperties.getInMemoryTimeout());
                }
            }

            LoggerUtils.debug(log, "ShiroRedisSessionDAO in applicationContext");
            LoggerUtils.debug(log, "  |-- KeyPrefix({}), InMemoryEnabled({}), InMemoryTimeout({})",
                    redisSessionDAO.getKeyPrefix(), redisSessionDAO.isSessionInMemoryEnabled(), redisSessionDAO.getSessionInMemoryTimeout());

            return redisSessionDAO;
        }

        @Bean("shiroCacheManager")
        @ConditionalOnMissingBean(name = "shiroCacheManager")
        public CacheManager shiroCacheManager(JacksonRedisTemplate redisTemplate,
                                              ShiroProperties shiroProperties) {

            RedisShiroCacheManager redisCacheManager = new RedisShiroCacheManager(new RedisShiroManager(redisTemplate));
            ShiroProperties.ShiroRedisProperties redisProperties = shiroProperties.getRedis();

            if (redisProperties != null) {
                ShiroProperties.ShiroRedisProperties.CacheProperties cacheProperties = redisProperties.getCache();
                if (cacheProperties != null) {
                    redisCacheManager.setExpire(cacheProperties.getTimeToLive());
                    redisCacheManager.setKeyPrefix(cacheProperties.getKeyPrefix());
                    redisCacheManager.setPrincipalIdFieldName(cacheProperties.getPrincipalIdFieldName());
                }
            }

            LoggerUtils.debug(log, "ShiroRedisCacheManager properties: ");
            LoggerUtils.debug(log, "  |--KeyPrefix({}), Expire({}s), PrincipalIdName({})",
                    redisCacheManager.getKeyPrefix(), redisCacheManager.getExpire(), redisCacheManager.getPrincipalIdFieldName());

            return redisCacheManager;
        }

        @Bean
        public SessionFactory sessionFactory() {
            return new ShiroSessionFactory();
        }
    }

    @Configuration
    @ConditionalOnClass(RedissonClient.class)
    public static class RedissonDependencyAutoConfiguration {

        @Bean
        public RedissonSessionDAO shiroSessionDAO(RedissonClient redissonClient,
                                                  SequenceGenerator sequenceGenerator,
                                                  ShiroProperties shiroProperties) {

            RedissonSessionDAO sessionDAO = new RedissonSessionDAO(sequenceGenerator, redissonClient);

            ShiroProperties.ShiroRedisProperties redisProperties = shiroProperties.getRedis();

            if (redisProperties != null) {
                ShiroProperties.ShiroRedisProperties.SessionProperties sessionProperties = redisProperties.getSession();
                if (sessionProperties != null) {
                    sessionDAO.setSessionInMemoryEnabled(sessionProperties.isInMemoryEnabled());
                    sessionDAO.setSessionInMemoryTimeout(sessionProperties.getInMemoryTimeout());
                }
            }

            LoggerUtils.debug(log, "RedissonSessionDAO in applicationContext");
            LoggerUtils.debug(log, "  |-- InMemoryEnabled({}), InMemoryTimeout({})",
                    sessionDAO.isSessionInMemoryEnabled(), sessionDAO.getSessionInMemoryTimeout());

            return sessionDAO;
        }

        @Bean("shiroCacheManager")
        @ConditionalOnMissingBean(name = "shiroCacheManager")
        public CacheManager shiroCacheManager(RedissonClient redissonClient,
                                              ShiroProperties shiroProperties,
                                              TenantUserAuthorizingRealm realm) {

            ShiroProperties.ShiroRedisProperties redisProperties = shiroProperties.getRedis();

            String authenticationName = realm.getAuthenticationCacheName();
            String authorizationName = realm.getAuthorizationCacheName();

            CacheConfig cacheConfig = null;
            if (null != redisProperties) {
                ShiroProperties.ShiroRedisProperties.CacheProperties cacheProperties = redisProperties.getCache();
                if (null != cacheProperties) {
                    // 配置文件单位是秒，这里转为毫秒
                    cacheConfig = new CacheConfig(
                            cacheProperties.getTimeToLive() * 1000L,
                            cacheProperties.getMaxIdleTime() * 1000L);
                }
            }
            if (null == cacheConfig) {
                // 默认值
                cacheConfig = new CacheConfig(1800000, 1800000);
            }

            return new RedissonShiroCacheManager(
                    redissonClient,
                    MapUtils.of(authenticationName, cacheConfig, authorizationName, cacheConfig));
        }

        @Bean
        public SessionFactory sessionFactory(RedissonClient redisson) {
            return new RedissonSessionFactory(redisson, null);
        }
    }

//    /**
//     * 根据系统定义的租户列表生成不同的realm
//     */
//    @Bean
//    public List<Realm> realm() {
//        List<Realm> realms = new ArrayList<>();
//        if (tenantService != null) {
//            List<Long> tenantIds = tenantService.getAllTenantId();
//            if (CollectionUtils.isEmpty(tenantIds)) {
//                useDefaultTenant(realms);
//            } else {
//                tenantIds.forEach(tenantId -> {
//                    List<String> types = tenantService.getAllTenantClientType(tenantId);
//                    if (CollectionUtils.isEmpty(types)) {
//                        LoggerUtils.debug(log, "Register tenant [{}] into the authentication system.", tenantId);
//                        realms.add(new TenantUserAuthorizingRealm(userService, roleService, permissionService, tenantId, ""));
//                    } else {
//                        types.forEach(t -> {
//                            LoggerUtils.debug(log, "Register tenant [{}-{}] into the authentication system.", tenantId, t);
//                            realms.add(new TenantUserAuthorizingRealm(userService, roleService, permissionService, tenantId, t));
//                        });
//                    }
//                });
//            }
//        } else {
//            useDefaultTenant(realms);
//        }
//        return realms;
//    }

    @Bean
    public TenantUserAuthorizingRealm tenantUserAuthorizingRealm() {
        return new TenantUserAuthorizingRealm(userService, roleService, permissionService);
    }

//    private void useDefaultTenant(List<Realm> realms) {
//        // 默认租户0
//        LoggerUtils.info(log, "System uses default tenant, id is [0] and type is empty.");
//        realms.add(new TenantUserAuthorizingRealm(
//                userService, roleService, permissionService, 0L, ""));
//    }

    /**
     * securityManager
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager(CacheManager cacheManager,
                                                               ShiroProperties shiroProperties,
                                                               SessionFactory sessionFactory) {

        ShiroWebSecurityManager securityManager = new ShiroWebSecurityManager(
                sessionManager(shiroProperties, sessionFactory),
                tenantUserAuthorizingRealm());

        securityManager.setCacheManager(cacheManager);
        // RememberMe Cookie管理器，不支持
//        securityManager.setRememberMeManager(rememberMeManager());
        LoggerUtils.debug(log,
                "Shiro securityManager in applicationContext, cache manager is {}",
                securityManager.getCacheManager());

        return securityManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroSessionListener shiroSessionListener() {
        return new ShiroSessionListener();
    }

    /**
     * sessionManager
     */
    @Bean
    public ShiroWebSessionManager sessionManager(ShiroProperties shiroProperties,
                                                 SessionFactory sessionFactory) {

        SessionDAO sessionDAO = ctx.getBean(SessionInMemoryDAO.class);

        ShiroKey key = shiroKey();

        return new ShiroWebSessionManager(
                key.loginTokenKey(),
                key.referencedSessionIdSource(),
                shiroProperties.getSessionTimeout() * 1000,
                sessionDAO,
                sessionFactory,
                Collections.singleton(shiroSessionListener()),
                shiroProperties.getSessionRefreshPolicy(),
                shiroProperties.getSessionRefreshNear() * 1000,
                shiroProperties.getSessionMaxLiveTime() * 1000);
    }

    // region remember me
//        private RememberMeManager rememberMeManager() {
//            CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//            cookieRememberMeManager.setCookie(rememberMeCookie());
//            // rememberMe cookie 加密的密钥
//            String encryptKey = shiroKey.rememberMeEncryptKey();
//            byte[] encryptKeyBytes = encryptKey.getBytes(StandardCharsets.UTF_8);
//            String rememberKey = Base64Utils.encodeToString(Arrays.copyOf(encryptKeyBytes, 16));
//            cookieRememberMeManager.setCipherKey(Base64.decode(rememberKey));
//            return cookieRememberMeManager;
//        }
//
//        private SimpleCookie rememberMeCookie() {
//            // 设置 cookie 名称，对应 login.html 页面的 <input type="checkbox" name="rememberMe"/>
//            SimpleCookie cookie = new SimpleCookie("rememberMe");
//            // 设置 cookie 的过期时间，单位为秒
//            cookie.setMaxAge(shiroProperties.getCookieTimeout());
//            return cookie;
//        }
    // endregion

    // region 过滤器工厂，可以在具体的应用中覆盖这个Bean

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroProperties shiroProperties) {

        ShiroFilterFactoryBean shiroFilterFactory = new ShiroFilterFactoryBean();
        // 设置 securityManager
        shiroFilterFactory.setSecurityManager(securityManager);
        // 特定url
        shiroFilterFactory.setLoginUrl(shiroProperties.getLoginUrl());
        shiroFilterFactory.setSuccessUrl(shiroProperties.getSuccessUrl());
        shiroFilterFactory.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        // 免认证url列表，逗号分隔
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                shiroProperties.getAnonUrl(), ",");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        for (String url : anonUrls) {
            filterChainDefinitionMap.put(url, "anon");
        }
        // 配置退出过滤器，其中具体的退出代码 Shiro已经替我们实现了
        filterChainDefinitionMap.put(shiroProperties.getLogoutUrl(), "logout");

        // 除上以外所有 url都必须认证通过才可以访问，未通过认证自动访问 LoginUrl
        // 开启了rememberMe则需要用user，authc必须重新认证才可以
        // 普通操作user就可以，关键性操作比如支付可以改成authc
//        filterChainDefinitionMap.put("/**", "user");
        // 除了anon url之外都需要授权
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactory.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactory;
    }

    // endregion

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public static DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

}
