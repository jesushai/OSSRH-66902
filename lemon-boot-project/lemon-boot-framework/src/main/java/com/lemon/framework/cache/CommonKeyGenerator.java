package com.lemon.framework.cache;

import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.spring.AopTargetUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * <b>名称：缓存Key通用生成器</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/21
 */
public class CommonKeyGenerator implements KeyGenerator {

    /**
     * 针对方法自动生成缓存的key
     * <p>
     * 首先会尝试获取方法的缓存相关的标签中的key
     * <p>
     * 如果没有找到则用默认规则：className.method:arg1;arg2;...
     *
     * @param target 方法所在的类实例对象
     * @param method 方法
     * @param params 参数
     * @return 缓存的key
     */
    @Override
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {

        String key = getKey(method);
        StringBuilder buf;

        if (StringUtils.hasLength(key)) {
            String className;

            try {
                Object real = AopTargetUtils.getTarget(target);
                className = real.getClass().getSimpleName();
            } catch (Exception e) {
                className = target.getClass().getSimpleName();
            }

            buf = new StringBuilder(className)
                    .append('.')
                    .append(method.getName())
                    .append(':');
        } else {
            buf = new StringBuilder(key).append(':');
        }

        if (params.length > 0) {
            for (Object param : params) {
                if (param == null) {
                    buf.append("null;");
                } else {
                    if (param.getClass().isPrimitive()) {
                        buf.append(param).append(';');
                    } else if (param instanceof String) {
                        buf.append('"').append(param).append("\";");
                    } else {
                        buf.append(param.getClass().getSimpleName())
                                .append('&')
                                .append(JacksonUtils.parseString(param))
                                .append(';');
                    }
                }
            }
        }

        return buf.toString();
    }

    private String getKey(Method method) {
        String key = null;

        Cacheable cacheable = AnnotationUtils.getAnnotation(method, Cacheable.class);
        if (null != cacheable) {
            key = cacheable.key();
        }

        if (null == key) {
            CachePut cachePut = AnnotationUtils.getAnnotation(method, CachePut.class);
            if (null != cachePut) {
                key = cachePut.key();
            }
        }

        if (null == key) {
            CacheEvict cacheEvict = AnnotationUtils.getAnnotation(method, CacheEvict.class);
            if (null != cacheEvict) {
                key = cacheEvict.key();
            }
        }

        if (null == key) {
            Caching caching = AnnotationUtils.getAnnotation(method, Caching.class);
            if (null != caching) {
                if (caching.cacheable().length > 0) {
                    key = caching.cacheable()[0].key();
                }

                if (null == key && caching.put().length > 0) {
                    key = caching.put()[0].key();
                }

                if (null == key && caching.evict().length > 0) {
                    key = caching.evict()[0].key();
                }
            }
        }

        return key;
    }

}
