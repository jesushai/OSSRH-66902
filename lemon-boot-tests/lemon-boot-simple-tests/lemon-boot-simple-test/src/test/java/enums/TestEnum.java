package enums;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public class TestEnum {

    @SneakyThrows
    @Test
    public void Test1() {
        Object o = Enum1.A;
        Object[] elements = o.getClass().getEnumConstants();

        Enum1 enum1 = null;
        for (Object element : elements) {
            if (((BaseEnum) element).getValue() == 1) {
                enum1 = (Enum1) element;
            }
        }
        Assertions.assertEquals(enum1, Enum1.B);

        PropertyDescriptor pd = getPropertyDescriptor(Enum1.class, "value");
        Assertions.assertNotNull(pd);
        Method m = pd.getReadMethod();
        Assertions.assertNotNull(m);
        Object value = m.invoke(enum1);
        Assertions.assertEquals(value, 1);
    }
}
