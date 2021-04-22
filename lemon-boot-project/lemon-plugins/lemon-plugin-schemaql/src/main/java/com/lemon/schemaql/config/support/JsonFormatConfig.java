package com.lemon.schemaql.config.support;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@Accessors(chain = true)
public class JsonFormatConfig {

    private String pattern;

    private String locale;

    private String timezone;

    private JsonFormat.Shape shape;

}
