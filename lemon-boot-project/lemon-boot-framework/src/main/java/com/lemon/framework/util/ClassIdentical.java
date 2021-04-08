package com.lemon.framework.util;

/**
 * 名称：<p/>
 * 描述：检测判断类是否符合标准<p/>
 *
 * @author hai-zhang
 * @since 2019/5/22
 */
public class ClassIdentical {
    /**
     * 判断对象是否兼容
     *
     * @param c class.
     * @param o instance.
     * @return 是否兼容true/false
     */
    public static boolean isCompatible(Class<?> c, Object o) {
        boolean pt = c.isPrimitive();

        if (o == null)
            return !pt;

        if (pt) {
            // 8种原始类型
            if (c == int.class)
                c = Integer.class;
            else if (c == boolean.class)
                c = Boolean.class;
            else if (c == long.class)
                c = Long.class;
            else if (c == float.class)
                c = Float.class;
            else if (c == double.class)
                c = Double.class;
            else if (c == char.class)
                c = Character.class;
            else if (c == byte.class)
                c = Byte.class;
            else if (c == short.class)
                c = Short.class;
        }

        if (c == o.getClass())
            return true;

        return c.isInstance(o);
    }
}
