package com.lemon.schemaql.util;

import com.lemon.framework.util.TimestampUtils;
import com.lemon.schemaql.config.EnumSchemaConfig;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import org.apache.commons.lang3.time.DateUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;

/**
 * <b>名称：实体类工具</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/19
 */
public class EntityUtils {

    /**
     * 将字符串表达的数据转换为真实的数据<br/>
     * <b>枚举类型将会返回枚举元素</b>
     *
     * @param type  数据类型
     * @param value 字符串表达的值
     * @return 真实数据
     */
    public static Object convertRealValue(String type, String value, ModuleSchemaConfig moduleConfig) {
        try {
            Object result = null;
            switch (type) {
                case "Integer":
                    result = Integer.parseInt(value);
                    break;
                case "Long":
                    result = Long.parseLong(value);
                    break;
                case "Timestamp":
                    result = TimestampUtils.parse(value);
                    break;
                case "Date":
                    result = DateUtils.parseDate(value, "yyyy-MM-dd");
                    break;
                case "Boolean":
                    result = Boolean.parseBoolean(value);
                    break;
                case "Decimal":
                case "BigDecimal":
                    result = new BigDecimal(value);
                    break;
                default:
                    // Enum
                    if (type.endsWith("Enum")) {
                        EnumSchemaConfig ec = moduleConfig.getEnumSchemas().stream()
                                .filter(x -> x.getEnumName().equals(type))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Not found enum class '" + type + "'"));

                        Class<?> enumClass = ec.getEnumClass();
                        Object[] elements = enumClass.getEnumConstants();

                        if (elements.length <= 0) {
                            throw new RuntimeException("Enum class '" + type + "' is empty.");
                        }

                        PropertyDescriptor pd = getPropertyDescriptor(enumClass, ec.getValueProperty());
                        if (null == pd) {
                            throw new RuntimeException("Enum class '" + type + "' has not '" + ec.getValueProperty() + "' property.");
                        }
                        Method readMethod = pd.getReadMethod();
                        if (null == readMethod) {
                            throw new RuntimeException("Enum class '" + type + "' has not '" + ec.getValueProperty() + "' property.");
                        }

                        Integer iValue = Integer.parseInt(value);
                        for (Object element : elements) {
                            if (iValue.equals(readMethod.invoke(element))) {
                                result = element;
                                break;
                            }
                        }

                        if (null == result) {
                            throw new RuntimeException("Enum class '" + type + "' has not value " + value);
                        }
                    } else {
                        // String
                        result = value;
                    }
                    break;
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Convert property value error", e);
        }
    }
}
