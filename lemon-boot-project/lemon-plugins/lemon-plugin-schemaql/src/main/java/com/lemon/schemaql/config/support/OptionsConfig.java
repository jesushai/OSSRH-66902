package com.lemon.schemaql.config.support;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@Data
@Accessors(chain = true)
public class OptionsConfig {

    /**
     * 默认的排序方式，-倒叙排列，多个排序逗号分隔
     */
    private String sortable;

    /**
     * 是否允许分页
     */
    private Boolean pageable = true;
}
