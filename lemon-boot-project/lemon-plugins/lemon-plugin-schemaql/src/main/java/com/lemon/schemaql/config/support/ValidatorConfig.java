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
public class ValidatorConfig {

    /**
     * 所属验证分组
     */
    private String[] groups;

    /**
     * 错误消息文本，支持国际化
     */
    private String message;

    /**
     * payload类
     */
    private String[] payloads;

    /**
     * 部分验证需要用到，比如Max,Min,Pattern...
     */
    private String value;

    /**
     * 验证类型：NotNull,NotBlank,Min,Max,AssertTrue,Email,Size...Slider，Slider是自定义的验证器
     */
    private String type;

    /**
     * 是否包含value，DecimalMin,DecimalMax等会用到，默认true
     */
    private String inclusive;

    /**
     * Size,Between等会用到
     */
    private String min;

    /**
     * Size,Between等会用到
     */
    private String max;

    /**
     * 滑动条的精度，即每次滑动的最小距离，仅Slider用
     */
    private int precision;
}
