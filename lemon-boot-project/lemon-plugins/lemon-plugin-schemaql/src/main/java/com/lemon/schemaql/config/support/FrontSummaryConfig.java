package com.lemon.schemaql.config.support;

import com.lemon.schemaql.enums.SummaryTypeEnum;

/**
 * <b>名称：前端聚合方式配置</b><br/>
 * <b>描述：</b><br/>
 * 允许前端计算的聚合类型，根据数据类型会有不同的选择，默认必带CUSTOM
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
public class FrontSummaryConfig {

    /**
     * 聚合类型: SUM|COUNT|AVERAGE|MIN|MAX|CUSTOM
     */
    private SummaryTypeEnum summaryType;

    /**
     * 显示格式：支持表达式
     */
    private String displayFormat;
}
