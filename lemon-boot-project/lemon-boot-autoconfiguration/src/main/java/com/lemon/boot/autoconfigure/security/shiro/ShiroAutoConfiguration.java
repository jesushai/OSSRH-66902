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
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
        ShiroRequiresPermissionsHelper.class,   // ????????????????????????
        ShiroExceptionHandlerAdvice.class,      // Shiro????????????
        ShiroAuthenticationServiceImpl.class,   // Shiro????????????
        ShiroSessionListenerMonitor.class       // Shiro session???????????????
})
@AutoConfigureAfter({
        DefaultAuthorizationSupportAutoConfiguration.class,
        SequenceGeneratorAutoConfiguration.class
})
public class ShiroAutoConfiguration {

    public ShiroAutoConfiguration() {
        LoggerUtils.debug(log, "Register ShiroAutoConfiguration.");
    }

    @Resource
    private ApplicationContext ctx;

    /**
     * ?????????
     * ???????????????????????????Spring-Boot-Aop
     * ????????????Shiro??????????????????????????????????????????Jdk&CGLib???
     * ???????????????@Lazy??????????????????
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

    /**
     * ???Redission???redis????????????Shiro DAO???CacheManager???SessionFactory????????????
     */
    @Configuration
    @ConditionalOnMissingClass("org.redisson.api.RedissonClient")
    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisDependencyAutoConfiguration {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Bean
        @DependsOn("redisAutoConfiguration")
        public RedisShiroSessionDAO sessionDAO(StringByteArrayRedisTemplate redisTemplate,
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
        @DependsOn("redisAutoConfiguration")
        public CacheManager cacheManager(JacksonRedisTemplate redisTemplate,
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

    /**
     * Redission????????????Shiro DAO???CacheManager????????????
     */
    @Configuration
    @ConditionalOnClass(RedissonClient.class)
    public static class RedissonDependencyAutoConfiguration {

        @Bean
        public RedissonSessionDAO sessionDAO(RedissonClient redissonClient,
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
        public CacheManager cacheManager(RedissonClient redissonClient,
                                         ShiroProperties shiroProperties,
                                         TenantUserAuthorizingRealm realm) {

            ShiroProperties.ShiroRedisProperties redisProperties = shiroProperties.getRedis();

            String authenticationName = realm.getAuthenticationCacheName();
            String authorizationName = realm.getAuthorizationCacheName();

            CacheConfig cacheConfig = null;
            if (null != redisProperties) {
                ShiroProperties.ShiroRedisProperties.CacheProperties cacheProperties = redisProperties.getCache();
                if (null != cacheProperties) {
                    // ?????????????????????????????????????????????
                    cacheConfig = new CacheConfig(
                            cacheProperties.getTimeToLive() * 1000L,
                            cacheProperties.getMaxIdleTime() * 1000L);
                }
            }
            if (null == cacheConfig) {
                // ?????????
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

    /**
     * ?????????????????????Shiro
     */
    @Configuration
    @ConditionalOnMissingClass({"org.redisson.api.RedissonClient", "org.springframework.data.redis.core.RedisTemplate"})
    public static class DefaultShiroAutoConfiguration {

        @Bean
        public MemorySessionDAO sessionDAO() {
            return new MemorySessionDAO();
        }

        @Bean
        public CacheManager cacheManager() {
            return new MemoryConstrainedCacheManager();
        }

        @Bean
        public SessionFactory sessionFactory() {
            return new ShiroSessionFactory();
        }
    }

//    /**
//     * ????????????????????????????????????????????????realm
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
//
//    private void useDefaultTenant(List<Realm> realms) {
//        // ????????????0
//        LoggerUtils.info(log, "System uses default tenant, id is [0] and type is empty.");
//        realms.add(new TenantUserAuthorizingRealm(
//                userService, roleService, permissionService, 0L, ""));
//    }

    @Bean
    public TenantUserAuthorizingRealm tenantUserAuthorizingRealm() {
        return new TenantUserAuthorizingRealm(userService, roleService, permissionService);
    }

    // region securityManager

    /**
     * securityManager
     *
     * @param cacheManager CacheManager
     * @param shiroProperties ShiroProperties
     * @param sessionFactory SessionFactory
     * @return ShiroWebSecurityManager
     */
    @Bean
    @ConditionalOnBean(CacheManager.class)
    public ShiroWebSecurityManager securityManager(CacheManager cacheManager,
                                                   ShiroProperties shiroProperties,
                                                   SessionFactory sessionFactory) {

        ShiroWebSecurityManager securityManager = new ShiroWebSecurityManager(
                sessionManager(shiroProperties, sessionFactory),
                tenantUserAuthorizingRealm());

        securityManager.setCacheManager(cacheManager);
        // RememberMe Cookie?????????????????????
//        securityManager.setRememberMeManager(rememberMeManager());
        LoggerUtils.debug(log,
                "Shiro securityManager in applicationContext, cache manager is {}",
                securityManager.getCacheManager());

        return securityManager;
    }

    // endregion

    @Bean
    @ConditionalOnMissingBean
    public ShiroSessionListener sessionListener() {
        return new ShiroSessionListener();
    }

    /**
     * sessionManager
     *
     * @param shiroProperties ShiroProperties
     * @param sessionFactory  SessionFactory
     * @return ShiroWebSessionManager
     */
    @Bean
    public ShiroWebSessionManager sessionManager(ShiroProperties shiroProperties,
                                                 SessionFactory sessionFactory) {

        SessionDAO sessionDAO = ctx.getBean(SessionDAO.class);

        ShiroKey key = shiroKey();

        return new ShiroWebSessionManager(
                key.loginTokenKey(),
                key.referencedSessionIdSource(),
                shiroProperties.getSessionTimeout() * 1000,
                sessionDAO,
                sessionFactory,
                Collections.singleton(sessionListener()),
                shiroProperties.getSessionRefreshPolicy(),
                shiroProperties.getSessionRefreshNear() * 1000,
                shiroProperties.getSessionMaxLiveTime() * 1000);
    }

    // region remember me
//        private RememberMeManager rememberMeManager() {
//            CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
//            cookieRememberMeManager.setCookie(rememberMeCookie());
//            // rememberMe cookie ???????????????
//            String encryptKey = shiroKey.rememberMeEncryptKey();
//            byte[] encryptKeyBytes = encryptKey.getBytes(StandardCharsets.UTF_8);
//            String rememberKey = Base64Utils.encodeToString(Arrays.copyOf(encryptKeyBytes, 16));
//            cookieRememberMeManager.setCipherKey(Base64.decode(rememberKey));
//            return cookieRememberMeManager;
//        }
//
//        private SimpleCookie rememberMeCookie() {
//            // ?????? cookie ??????????????? login.html ????????? <input type="checkbox" name="rememberMe"/>
//            SimpleCookie cookie = new SimpleCookie("rememberMe");
//            // ?????? cookie ??????????????????????????????
//            cookie.setMaxAge(shiroProperties.getCookieTimeout());
//            return cookie;
//        }
    // endregion

    // region ?????????????????????????????????????????????????????????Bean

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroProperties shiroProperties) {

        ShiroFilterFactoryBean shiroFilterFactory = new ShiroFilterFactoryBean();
        // ?????? securityManager
        shiroFilterFactory.setSecurityManager(securityManager);
        // ??????url
        shiroFilterFactory.setLoginUrl(shiroProperties.getLoginUrl());
        shiroFilterFactory.setSuccessUrl(shiroProperties.getSuccessUrl());
        shiroFilterFactory.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        // ?????????url?????????????????????
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(
                shiroProperties.getAnonUrl(), ",");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        for (String url : anonUrls) {
            filterChainDefinitionMap.put(url, "anon");
        }
        // ??????????????????????????????????????????????????? Shiro????????????????????????
        filterChainDefinitionMap.put(shiroProperties.getLogoutUrl(), "logout");

        // ?????????????????? url?????????????????????????????????????????????????????????????????? LoginUrl
        // ?????????rememberMe????????????user???authc???????????????????????????
        // ????????????user???????????????????????????????????????????????????authc
//        filterChainDefinitionMap.put("/**", "user");
        // ??????anon url?????????????????????
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
