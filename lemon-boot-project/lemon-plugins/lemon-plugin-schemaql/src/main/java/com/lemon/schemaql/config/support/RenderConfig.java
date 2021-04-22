package com.lemon.schemaql.config.support;

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
public class RenderConfig {

    /**
     * 是否显示，固定：id|逻辑删除|乐观锁字段默认false；默认：其他字段均为true
     */
    private Boolean visible;

    /**
     * 显示序列，1开始
     */
    private int index;

    /**
     * 列宽度，这个是标准单位，具体客户端需要根据自己情况乘以系数来转成符合特定客户端的值
     */
    private double width;

    /**
     * 对齐方式：left|center|right，默认数值right，其他left
     */
    private String alignment = "left";

    /**
     * 显示格式
     */
    private String format;
}
