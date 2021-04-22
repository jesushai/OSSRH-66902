package com.lemon.schemaql.engine.parser.input;

import lombok.Data;

/**
 * 名称：输入的参数<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/9/1
 */
@Data
public class InputArgument {

    /**
     * 填充Schema对象的内容体
     */
    private String body;

    /**
     * 查询条件
     */
    private InputCondition condition;

    /**
     * 分组项
     */
    private String[] groupBy;

    /**
     * 排序：-倒叙
     */
    private String[] orderBy;

    /**
     * 分页从1开始
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;
}
