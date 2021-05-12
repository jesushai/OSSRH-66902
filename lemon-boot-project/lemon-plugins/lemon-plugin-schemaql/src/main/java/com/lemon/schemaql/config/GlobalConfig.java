package com.lemon.schemaql.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/5/10
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class GlobalConfig extends Schema {

    private String author;

}
