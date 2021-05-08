package com.lemon.framework.util.test;

import org.apache.commons.lang3.LocaleUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/5/6
 */
class LocaleTest {

    @Test
    void test1() {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        Assertions.assertEquals("zh-CN", locale.toLanguageTag());
        Assertions.assertEquals("zh", locale.getLanguage());
        Assertions.assertEquals("CN", locale.getCountry());
        Assertions.assertEquals("中文 (中国)", locale.getDisplayName());
        Assertions.assertEquals("中文", locale.getDisplayLanguage());
        Assertions.assertEquals("中国", locale.getDisplayCountry());
        Assertions.assertEquals(Locale.SIMPLIFIED_CHINESE, Locale.forLanguageTag("zh-CN"));
        Assertions.assertEquals(Locale.ROOT, Locale.forLanguageTag("zh_CN"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("zh-CN"));
        Assertions.assertEquals("zh_CN", locale.toString());
        Assertions.assertEquals("zh_CN", LocaleUtils.toLocale("zh_CN").toString());
    }

}
