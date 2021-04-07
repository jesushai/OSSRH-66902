package com.lemon.framework.auth.shiro.redisson;

import com.lemon.framework.auth.shiro.ShiroSession;
import com.lemon.framework.util.LoggerUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.InvalidSessionException;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.cache.support.NullValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.lemon.framework.auth.shiro.redisson.RedissonSessionDAO.*;
import static com.lemon.framework.auth.shiro.redisson.RedissonSessionScript.*;

/**
 * <b>名称：封装了Redisson操作的Session</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class RedissonSession extends ShiroSession {

    static final String INFO_ID_KEY = "id";
    static final String INFO_START_KEY = "startTimestamp";
    static final String INFO_STOP_KEY = "stopTimestamp";
    static final String INFO_EXPIRE_KEY = "expireTimestamp";
    static final String INFO_LAST_KEY = "lastAccessTime";
    static final String INFO_TIMEOUT_KEY = "timeout";
    static final String INFO_HOST_KEY = "host";

    private final RedissonClient redisson;
    private final static Codec infoCodec = new JsonJacksonCodec();
    private Codec codec = infoCodec;

    /**
     * 是否已经保存到redis中
     */
    private boolean savedToRedis = false;

    public RedissonSession(RedissonClient redisson, Codec codec, Serializable id, Date startTimestamp, Date lastAccessTime, long timeout, String host, Date expireTimestamp) {
        super(id, startTimestamp, lastAccessTime, timeout, host, expireTimestamp);

        if (redisson == null) {
            throw new IllegalArgumentException("Arguments must not be null!");
        }

        this.redisson = redisson;
        if (codec != null) {
            this.codec = codec;
        }
    }

    public RedissonSession(RedissonClient redisson, Codec codec, String host) {
        super(host);

        this.redisson = redisson;
        if (codec != null) {
            this.codec = codec;
        }
    }

    public RedissonSession(RedissonClient redisson, Codec codec) {
        super();

        this.redisson = redisson;
        if (codec != null) {
            this.codec = codec;
        }
    }

    /**
     * 刷新超时时间
     */
    @Override
    public void setTimeout(long timeout) throws InvalidSessionException {
        Date expireTimestamp = new Date(System.currentTimeMillis() + timeout);

        if (!isAnon() && null != getId()) {
            String key = getSessionInfoKey(getId());
            List<Object> keys = new ArrayList<>(2);
            keys.add(key);
            keys.add(getSessionAttrKey(getId()));

            LoggerUtils.debug(log, "Set redis session timeout: key={}, {}(ms)", key, timeout);
            try {
                // 1. 刷新超时毫秒数，同时延长当前过期时间
                getRedissonScript().eval(key, RScript.Mode.READ_WRITE, SET_TIMEOUT_SCRIPT, RScript.ReturnType.VALUE, keys,
                        timeout, expireTimestamp);
            } catch (RedisException e) {
                convertException(e);
            }
        } else {
            LoggerUtils.debug(log, "Set anon session timeout: id={}, {}(ms)", getId(), timeout);
        }

        // 2. 更新内存中的数据
        super.setTimeout(timeout);
        super.setExpireTimestamp(expireTimestamp);
    }

    /**
     * SessionManager会首先拦截touch并更新lastAccessTime和expireTimestamp
     */
    @Override
    public void touch() throws InvalidSessionException {
        super.touch();

        if (!isAnon() && null != getId()) {
            String key = getSessionInfoKey(getId());
            List<Object> keys = new ArrayList<>(2);
            keys.add(key);
            keys.add(getSessionAttrKey(getId()));

            LoggerUtils.debug(log, "Touch redis session: key={}", key);
            try {
                getRedissonScript().eval(key, RScript.Mode.READ_WRITE, TOUCH_SCRIPT, RScript.ReturnType.VALUE, keys,
                        this.getLastAccessTime(), this.getExpireTimestamp());
            } catch (RedisException e) {
                convertException(e);
            }
        } else {
            LoggerUtils.debug(log, "Touch anon session: id={}", getId());
        }
    }

    @Override
    public void stop() throws InvalidSessionException {
        Date stopTimestamp = new Date();

        if (!isAnon() && null != getId()) {
            String key = getSessionInfoKey(getId());
            List<Object> keys = new ArrayList<>(1);
            keys.add(key);

            LoggerUtils.debug(log, "Stop redis session: key={}", key);
            try {
                getRedissonScript().eval(key, RScript.Mode.READ_WRITE, STOP_SCRIPT, RScript.ReturnType.VALUE, keys,
                        stopTimestamp);
            } catch (RedisException e) {
                convertException(e);
            }
        } else {
            LoggerUtils.debug(log, "Stop anon session: id={}", getId());
        }

        if (null == this.getStopTimestamp()) {
            this.setStopTimestamp(stopTimestamp);
        }
    }

    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        Collection<Object> res = null;

        if (!isAnon() && null != getId()) {
            String key = getSessionInfoKey(getId());
            List<Object> keys = new ArrayList<>(2);
            keys.add(key);
            keys.add(getSessionAttrKey(getId()));

            LoggerUtils.debug(log, "Get redis session attribute keys: id={}", getId());
            try {
                res = getRedissonScript().eval(key, RScript.Mode.READ_ONLY, GET_ATTRKEYS_SCRIPT, RScript.ReturnType.MAPVALUELIST, keys);
            } catch (RedisException e) {
                convertException(e);
            }
        } else {
            LoggerUtils.debug(log, "Get anon session attribute keys: id={}", getId());
        }

        if (res == null) {
            res = super.getAttributeKeys();
        }

        if (res == null) {
            throw new InvalidSessionException();
        } else {
            return res;
        }
    }

    @Override
    public Object getAttribute(Object key) throws InvalidSessionException {
        // 尝试先从内存中获取，尽量不要设置null值到属性中
        Object value = super.getAttribute(key);
        if (null != value) {
            LoggerUtils.debug(log, "Get session attribute by cache: id={}, attribute=[{}, {}]", getId(), key, value);
            if (NullValue.INSTANCE.equals(value)) {
                return null;
            } else {
                return value;
            }
        }

        if (!isAnon() && null != getId()) {
            List<Object> keys = new ArrayList<>(2);
            keys.add(getSessionInfoKey(getId()));
            keys.add(getSessionAttrKey(getId()));

            // 从redis获取单个属性即可
            try {
                value = getRedissonScript().eval((String) keys.get(0),
                        RScript.Mode.READ_ONLY, GET_ATTR_SCRIPT, RScript.ReturnType.MAPVALUE, keys,
                        key);
            } catch (RedisException e) {
                convertException(e);
            }
            LoggerUtils.debug(log, "Get redis session attribute: id={}, attribute=[{}, {}]", getId(), key, value);
        } else {
            LoggerUtils.debug(log, "The session not contain attribute: id={}, attribute={}", getId(), key);
        }

        // 缓存到内存中
        LoggerUtils.debug(log, "Cache the session attribute: id={}, attribute=[{}, {}]", getId(), key, value);
        if (null != value) {
            super.setAttribute(key, value);
        } else {
            // null也要缓存
            super.setAttribute(key, NullValue.INSTANCE);
        }

        return value;
    }

    @Override
    public void setAttribute(Object key, Object value) throws InvalidSessionException {

        if (null == value) {
            LoggerUtils.debug(log, "Set attribute value is null, to call remove: id={}, attribute={}", getId(), key);
            removeAttribute(key);
            return;
        }

        if (!isAnon() && null != getId()) {
            List<Object> keys = new ArrayList<>(2);
            keys.add(getSessionInfoKey(getId()));
            keys.add(getSessionAttrKey(getId()));

            // 写入Redis
            try {
                getRedissonScript().eval((String) keys.get(0),
                        RScript.Mode.READ_WRITE, SET_ATTR_SCRIPT, RScript.ReturnType.VALUE, keys,
                        key, value);
            } catch (RedisException e) {
                convertException(e);
            }
            LoggerUtils.debug(log, "Set redis session attribute: id={}, attribute=[{}, {}]", getId(), key, value);
        } else {
            LoggerUtils.debug(log, "Set anon session attribute: id={}, attribute=[{}, {}]", getId(), key, value);
        }

        // 写入内存中
        super.setAttribute(key, value);
    }

    @Override
    public Object removeAttribute(Object key) throws InvalidSessionException {
        Object value = null;

        if (!isAnon() && null != getId()) {
            List<Object> keys = new ArrayList<>(2);
            keys.add(getSessionInfoKey(getId()));
            keys.add(getSessionAttrKey(getId()));

            try {
                value = getRedissonScript().eval((String) keys.get(0),
                        RScript.Mode.READ_WRITE, REMOVE_ATTR_SCRIPT, RScript.ReturnType.MAPVALUE, keys,
                        key);
            } catch (RedisException e) {
                convertException(e);
            }
            LoggerUtils.debug(log, "Remove redis session attribute: id={}, attribute=[{}, {}]", getId(), key, value);
        } else {
            LoggerUtils.debug(log, "Remove anon session attribute: id={}, attribute={}", getId(), key);
        }

        // 删除内存中的属性
        super.removeAttribute(key);

        return value;
    }

    public boolean isAnon() {
        Object value = super.getAttribute(ShiroSession.IS_ANON);
        return null != value && (boolean) value;
    }

}
