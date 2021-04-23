package com.lemon.framework.db.mp.page;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 名称：Mybatis分页助手<p>
 * 描述：<p>
 * 可以将多种来源的分页与排序数据包装成IPage<p>
 * 仅用于分页与排序的请求，不负责结果集的收集，结果集依然是mybatis的Page类<p>
 * 支持缓存
 *
 * @author hai-zhang
 * @since 2020/6/17
 */
@Data
public class MyPageRequest<T> {

    public static <T> IPage<T> of(int page, int size) {
        return MyPageRequest.of(page, size, null, null, null);
    }

    public static <T> IPage<T> of(int page, int size, String[] sort, Class<T> clazz) {
        return MyPageRequest.of(page, size, sort, null, clazz);
    }

    public static <T> IPage<T> of(int pageNumber, int size, String[] sort, String[] order, Class<T> clazz) {
        // Mybatis的首页是从1开始的
        final Page<T> page = new Page<>(pageNumber, size);

        if (null != sort && sort.length > 0) {
            Assert.notNull(clazz, "Sort entity class must not be null!");

            for (int i = 0; i < sort.length; i++) {
                // 实体属性转表字段名
                String columnName = getColumnNameByFieldName(sort[i], clazz);

                if (StringUtils.isNotEmpty(columnName)) {
                    if (null != order && order.length >= i + 1 && StringUtils.isNotEmpty(order[i]) && "desc".equalsIgnoreCase(order[i])) {
                        page.addOrder(OrderItem.desc(columnName));
                    } else {
                        page.addOrder(OrderItem.asc(columnName));
                    }
                }
            }
        }

        return page;
    }

    /**
     * Mybatis排序字段不能是实体字段，所以这里需要转换一下！
     *
     * @param fieldName 实体字段
     * @param clazz     实体类
     * @return 实际表中的字段
     */
    private static String getColumnNameByFieldName(final String fieldName, final Class clazz) {
        // 避免频繁反射，这里缓存一下
        String key = clazz.getName() + '.' + fieldName;
        String columnName = columnsCache.get(key);
        if (null != columnName) {
            return columnName;
        }

        columnName = fieldName;
        // 找到属性
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ignore) {
        }
        // 通过属性找到字段
        if (field != null) {
            Annotation[] annotations = field.getAnnotationsByType(TableField.class);
            if (annotations.length > 0) {
                TableField tableField = (TableField) annotations[0];
                columnName = tableField.value();
            }
        }
        columnsCache.put(key, columnName);
        return columnName;
    }

    private static final Map<String, String> columnsCache = new ConcurrentHashMap<>();

}
