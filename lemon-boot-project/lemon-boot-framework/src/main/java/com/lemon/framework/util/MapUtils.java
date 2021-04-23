package com.lemon.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;
import static org.springframework.util.StringUtils.hasLength;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2019/5/26
 */
@Slf4j
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class MapUtils {

    public static <K, V> Map<K, V> of(K k1, V v1) {
        return (Map<K, V>) take(k1, v1);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        return (Map<K, V>) take(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return (Map<K, V>) take(k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return (Map<K, V>) take(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return (Map<K, V>) take(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return (Map<K, V>) take(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static <K> Map<K, Object> take(Object... kv) {
        Map<K, Object> m = new HashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            if (kv.length <= i + 1) {
                m.put((K) kv[i], null);
            } else {
                m.put((K) kv[i], kv[i + 1]);
            }
        }
        return m;
    }

    public static <K, V> Map<K, V> putIfNotEmpty(Map<K, V> map, K k, V v) {
        if (null != v && (v instanceof CharSequence && hasLength((CharSequence) v))) {
            map.put(k, v);
        }
        return map;
    }

    public static <F, K, V> Map<K, V> transform(Collection<F> fromCollection,
                                                Function<? super F, ? extends K> functionK,
                                                Function<? super F, ? extends V> functionV) {
        Map<K, V> result = new HashMap<>();

        if (fromCollection != null)
            fromCollection.forEach(f -> result.put(functionK.apply(f), functionV.apply(f)));

        return result;
    }

    public static <K, V> Map<K, V> union(Map<K, V>... m) {
        if (m == null || m.length <= 0) {
            return null;
        }

        if (m.length == 1) {
            return m[0];
        }

        for (int i = 1; i < m.length; i++) {
            m[0].putAll(m[i]);
        }

        return m[0];
    }

    public static void copyProperties(Map<String, Object> source, Object target) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Class<?> actualEditable = target.getClass();

        source.forEach((k, v) -> {
            PropertyDescriptor pd = getPropertyDescriptor(actualEditable, k);
            if (null != pd) {
                Method writeMethod = pd.getWriteMethod();
                if (null != writeMethod) {
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        throw new FatalBeanException("Target object's property '" + k + "' must have a public setter.");
                    }
                    try {
                        writeMethod.invoke(target, v);
                    } catch (Throwable e) {
                        throw new FatalBeanException("Could not copy property '" + pd.getName() + "' from source to target", e);
                    }
                }
            }
        });
    }

    /**
     * 将实体对象转换为Map
     *
     * @param source 实体
     * @param ps     要转换的属性(通过getter方法获取)列表
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object source, String... ps) {
        Assert.notNull(source, "Source must not be null");
        Map<String, Object> map = new HashMap<>();

        PropertyDescriptor[] pds = getPropertyDescriptors(source.getClass());
        for (PropertyDescriptor pd : pds) {
            if (null != ps && ArrayUtils.contains(ps, pd.getName())) {
                putReadMethodValue(source, pd, map);
            }
        }

        return map;
    }

    private static Collection<?> createEmptyCollection(Class<? extends Collection<?>> clazz) {

        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of class " + clazz.getName());
        }
    }

    /**
     * 将实体集合转为Map集合，转换实体内所有属性(具有getter方法的字段)
     *
     * @param beans 实体集合
     * @param clazz 返回的新集合类型
     * @return 返回的Map集合
     */
    public static Collection<Map<String, Object>> beansToMapCollection(
            Collection<Object> beans,
            Class<? extends Collection<?>> clazz) {

        if (clazz == null) {
            return null;
        }

        Collection results = createEmptyCollection(clazz);

        if (CollectionUtils.isEmpty(beans)) {
            return results;
        }

        for (Object bean : beans) {
            results.add(beanToMap(bean));
        }

        return results;
    }

    /**
     * 将实体集合转为Map集合，转换实体内所有属性(具有getter方法的字段)
     *
     * @param beans 实体集合
     * @return 返回的Map集合
     */
    public static Collection<Map<String, Object>> beansToMapCollection(Collection<Object> beans) {
        if (beans == null) {
            return null;
        }
        return beansToMapCollection(beans, (Class<? extends Collection<?>>) beans.getClass());
    }

    /**
     * 将实体集合转为Map集合
     *
     * @param beans 实体集合
     * @param ps    要转换的属性(通过getter方法获取)列表
     * @param clazz 返回的新集合类型
     * @return 返回的Map集合
     */
    public static Collection<Map<String, Object>> beansToMapCollection(
            Collection<Object> beans,
            String[] ps,
            Class<? extends Collection<?>> clazz) {

        if (clazz == null) {
            return null;
        }

        Collection results = createEmptyCollection(clazz);

        if (CollectionUtils.isEmpty(beans)) {
            return results;
        }

        for (Object bean : beans) {
            results.add(beanToMap(bean, ps));
        }

        return results;
    }

    /**
     * 将实体集合转为Map集合
     *
     * @param beans 实体集合
     * @param ps    要转换的属性(通过getter方法获取)列表
     * @return 返回的Map集合
     */
    public static Collection<Map<String, Object>> beansToMapCollection(Collection<Object> beans, String[] ps) {
        if (beans == null) {
            return null;
        }
        return beansToMapCollection(beans, ps, (Class<? extends Collection<?>>) beans.getClass());
    }

    private static void putReadMethodValue(Object source, PropertyDescriptor property, Map<String, Object> map) {
        try {
            if (property.getName().equals("class")) {
                return;
            }

            Method readMethod = property.getReadMethod();
            if (null == readMethod) {
                return;
            }

            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
            }

            Object value = readMethod.invoke(source);
            map.put(property.getName(), value);

        } catch (InvocationTargetException e) {
            log.error("获取属性失败：{}.{}", source.getClass().getSimpleName(), property.getName());
            // Do nothing!
        } catch (Exception ex) {
            throw new RuntimeException("Could not copy properties from source to target");
        }
    }
}
