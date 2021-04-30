package com.lemon.schemaql.config.support.i18n;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Locale;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/30
 */
@Data
@Accessors
@EqualsAndHashCode(of = "localeString")
public class LocaleNode  {

    private Locale locale;

    private String localeString;

    private MessageItem item;

}
