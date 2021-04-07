package com.lemon.framework.db.enums;

/**
 * <b>名称：通用的枚举接口</b><br/>
 * <b>描述：枚举必须有两个值，整型的value和用于显示的文字</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-2
 */
@SuppressWarnings("unused")
public interface DbFieldEnum {

    int getValue();

    String getDisplay();

    static <T extends DbFieldEnum> T fromValue(Class<T> enumClass, Object value) {
        T[] _enums = enumClass.getEnumConstants();

        int i;
        if (value instanceof Integer) {
            i = (int) value;
        } else {
            i = Integer.parseInt(value.toString());
        }

        for (T _enum : _enums) {
            if (i == _enum.getValue()) {
                return _enum;
            }
        }

        throw new RuntimeException("No enum value [" + value + "] of " + enumClass.getCanonicalName());
    }
}
