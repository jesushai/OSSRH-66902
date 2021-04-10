package com.lemon.schemaql.enums;

/**
 * <b>名称：枚举接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public interface BaseEnum {

    /**
     * 枚举的值，默认从0开始
     */
    Integer getValue();

    /**
     * 枚举的文本，支持国际化（以%开头）
     */
    String getDisplay();
}
