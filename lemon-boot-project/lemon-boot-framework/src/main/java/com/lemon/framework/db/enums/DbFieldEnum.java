package com.lemon.framework.db.enums;

import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 名称：通用的枚举接口<br/>
 * 描述：枚举必须有两个值，整型的value和用于显示的文字<br/>
 *
 * @author hai-zhang
 * @since 2020-5-2
 */
public interface DbFieldEnum<C> {

    C getValue();

    String getDisplay();

    static <T extends DbFieldEnum<?>> T fromValue(Class<T> enumClass, Object value) {
        T[] _enums = enumClass.getEnumConstants();

        Assert.notNull(value, "Null value cannot be determined as enumeration.");

        Object convert = null;
        if (_enums.length > 0) {
            Object v = _enums[0].getValue();
            if (v instanceof String) {
                convert = value.toString();
            } else if (v instanceof Integer) {
                if (value instanceof Number)
                    convert = ((Number) value).intValue();
                else
                    convert = Integer.valueOf(value.toString());
            } else if (v instanceof Short) {
                if (value instanceof Number)
                    convert = ((Number) value).shortValue();
                else
                    convert = Short.valueOf(value.toString());
            }
        }

        for (T _enum : _enums) {
            if (Objects.equals(convert, _enum.getValue())) {
                return _enum;
            }
        }

        throw new RuntimeException("No enum value [" + value + "] of " + enumClass.getCanonicalName());
    }
}
