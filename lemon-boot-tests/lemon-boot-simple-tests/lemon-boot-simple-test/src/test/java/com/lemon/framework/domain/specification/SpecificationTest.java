package com.lemon.framework.domain.specification;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.LoggableRuntimeException;
import com.lemon.framework.exception.NotAcceptableException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/28
 */
class SpecificationTest {

    /**
     * 测试执行动态业务规则
     */
    @Test
    void test1() {
        Assertions.assertThrows(NotAcceptableException.class,
                () -> doSatisfiedBy(TestSpecification.class, null));
        Assertions.assertThrows(NotAcceptableException.class,
                () -> doSatisfiedBy(TestSpecification.class, new TestBean(null)));
    }

    @SuppressWarnings("unchecked")
    <T extends AbstractSpecification> boolean doSatisfiedBy(Class<T> clazz, Object bean)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        AbstractSpecification o = clazz.getDeclaredConstructor().newInstance();
        return o.isSatisfiedBy(bean);
    }

    static class TestSpecification extends AbstractSpecification<TestBean> {

        @Override
        public boolean isSatisfiedBy(TestBean testBean) {
            if (null == testBean) {
                new ExceptionBuilder<>(NotAcceptableException.class).messageTemplate("Bean cannot be null.").throwIt();
            }
            if (!StringUtils.hasText(testBean.name)) {
                new ExceptionBuilder<>(NotAcceptableException.class).messageTemplate("Name is empty.").throwIt();
            }
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    static class TestBean {
        private String name;
    }
}
