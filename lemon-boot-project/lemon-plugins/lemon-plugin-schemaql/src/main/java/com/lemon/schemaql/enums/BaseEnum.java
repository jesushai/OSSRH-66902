package com.lemon.schemaql.enums;

/**
 * 名称：枚举接口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
public interface BaseEnum {

    /**
     * @return 枚举的值，默认从0开始
     */
    Integer getValue();

    /**
     * @return 枚举的文本，支持国际化（以%开头）
     */
    String getDisplay();
}
