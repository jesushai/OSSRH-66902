package com.lemon.schemaql.engine.validation;

import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.springframework.beans.factory.DisposableBean;

import javax.validation.spi.ConfigurationState;

/**
 * <b>名称：验证器工厂</b><br/>
 * <b>描述：</b><br/>
 * 不支持国际化
 *
 * @author hai-zhang
 * @since 2020/8/1
 */
public class SimpleValidatorFactory extends ValidatorFactoryImpl implements DisposableBean {

    public SimpleValidatorFactory(ConfigurationState configurationState) {
        super(configurationState);
    }

    @Override
    public void destroy() throws Exception {
        this.close();
    }
}
