package com.lemon.framework.util;

import com.lemon.framework.exception.ExceptionBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@Slf4j
@SuppressWarnings("unused")
public class BeanUtils {

    public static <T> T getOrElseThrow(Supplier<T> action, String msgKey) {
        return Optional.ofNullable(action.get()).orElseThrow(() ->
                new ExceptionBuilder<>().code(msgKey).build());
    }

    public static <T> T getOrEmptyThrow(Supplier<T> action, String msgKey) {
        T t = action.get();
        if (t instanceof CharSequence) {
            if (org.apache.commons.lang3.StringUtils.isBlank((CharSequence) t)) {
                t = null;
            }
        }
        return Optional.ofNullable(t)
                .orElseThrow(() -> new ExceptionBuilder<>().code(msgKey).build());
    }

    /**
     * 将数组数据转换为实体类
     * 此处数组元素的顺序必须与实体类构造函数中的属性顺序一致
     *
     * @param list  数组对象集合
     * @param clazz 实体类
     * @param <T>   实体类
     * @param model 实例化的实体类
     * @return 实体类集合
     */
    public static <T> List<T> castEntity(List<Object[]> list, Class<T> clazz, Object model) {
        List<T> returnList = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return returnList;
        }
        //获取每个数组集合的元素个数
        Object[] co = list.get(0);

        //获取当前实体类的属性名、属性值、属性类别
        List<Map<String, Object>> attributeInfoList = getFieldsInfo(model);
        //创建属性类别数组
        Class<?>[] c2 = new Class[attributeInfoList.size()];
        //如果数组集合元素个数与实体类属性个数不一致则发生错误
        if (attributeInfoList.size() != co.length) {
            return returnList;
        }
        //确定构造方法
        for (int i = 0; i < attributeInfoList.size(); i++) {
            c2[i] = (Class<?>) attributeInfoList.get(i).get("type");
        }
        try {
            for (Object[] o : list) {
                Constructor<T> constructor = clazz.getConstructor(c2);
                returnList.add(constructor.newInstance(o));
            }
        } catch (Exception ex) {
            log.error("实体数据转化为实体类发生异常：异常信息：{}", ex.getMessage());
            return returnList;
        }
        return returnList;
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名
     * @param model     实体类
     * @return 属性值
     */
    private static Object getFieldValueByName(String fieldName, Object model) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = model.getClass().getMethod(getter);
            return method.invoke(model);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取属性类型(type)，属性名(name)，属性值(value)的map组成的list
     *
     * @param model 实体类
     * @return list集合
     */
    private static List<Map<String, Object>> getFieldsInfo(Object model) {
        Field[] fields = model.getClass().getDeclaredFields();
        List<Map<String, Object>> list = new ArrayList<>(fields.length);
        Map<String, Object> infoMap;
        for (Field field : fields) {
            infoMap = new HashMap<>(3);
            infoMap.put("type", field.getType());
            infoMap.put("name", field.getName());
            infoMap.put("value", getFieldValueByName(field.getName(), model));
            list.add(infoMap);
        }
        return list;
    }

    /**
     * 克隆对象的部分属性<br/>
     *
     * @param original  源对象
     * @param target    目标对象
     * @param fieldName 属性列表
     * @throws NoSuchFieldException   没有找到字段
     * @throws IllegalAccessException 非法访问
     */
    public static void clonePart(Object original, Object target, String... fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (original == null || target == null)
            return;

        Class<?> clazz = original.getClass();
        for (String name : fieldName) {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, field.get(original));
        }
    }

    /**
     * 克隆对象的部分属性，忽略null的属性
     *
     * @param original  源对象
     * @param target    目标对象
     * @param fieldName 属性列表
     * @throws NoSuchFieldException   没有找到字段
     * @throws IllegalAccessException 非法访问
     */
    public static void clonePartIgnoreNullField(Object original, Object target, String... fieldName) throws NoSuchFieldException, IllegalAccessException {
        if (original == null || target == null)
            return;

        Class<?> clazz = original.getClass();
        for (String name : fieldName) {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            Object value = field.get(original);
            if (value != null) {
                field.set(target, value);
            }
        }
    }

    public static Object mapToBean(Map<String, Object> source, Object target) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        PropertyDescriptor[] pds = getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor pd : pds) {
            if (!source.containsKey(pd.getName())) {
                continue;
            }
            Method writeMethod = pd.getWriteMethod();
            if (null != writeMethod) {
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                try {
                    writeMethod.invoke(target, source.get(pd.getName()));
                } catch (Exception e) {
                    throw new FatalBeanException("Could not copy property '" + pd.getName() + "' from source to target", e);
                }
            }
        }
        return target;
    }
}
