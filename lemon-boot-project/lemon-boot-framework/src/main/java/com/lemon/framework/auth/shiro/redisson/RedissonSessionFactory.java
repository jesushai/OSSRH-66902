package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.auth.shiro.ShiroSession;
import com.lemon.framework.auth.shiro.ShiroSessionFactory;
import com.lemon.framework.auth.shiro.ShiroSubject;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

/**
 * 名称：Shiro Session工厂<br/>
 * 描述：<br/>
 * 首先Shiro会通过这个工厂生产ShiroSession
 * <p>
 * 随后会生成ID并更新timeout等
 * <p>
 * 接着会将session的各种属性保存到redis中
 * <p>
 * 最后会将这个session包装成RedissonSession，同时缓存到内存中
 *
 * @author hai-zhang
 * @since 2020/6/8
 */
@Slf4j
public class RedissonSessionFactory extends ShiroSessionFactory {

    private final RedissonClient redissonClient;
    private Codec codec;

    public RedissonSessionFactory(RedissonClient redissonClient, Codec codec) {
        this.redissonClient = redissonClient;
        if (null == codec) {
            this.codec = new JsonJacksonCodec();
        } else {
            this.codec = codec;
        }
    }

    @Override
    public Session createSession(SessionContext initData) {

        RedissonSession session = null;

        if (initData != null) {
            String host = initData.getHost();

            if (host != null) {
                session = new RedissonSession(redissonClient, codec, host);
            }
        }

        if (session == null) {
            session = new RedissonSession(redissonClient, codec);
        }

        if (ShiroSubject.isAnonAccess()) {
            session.setAttribute(ShiroSession.IS_ANON, true);
            LoggerUtils.debug(log, "Factory production a anonymous session.");
        } else {
            LoggerUtils.debug(log, "Factory production a authenticated session.");
        }

        return session;
    }
}
