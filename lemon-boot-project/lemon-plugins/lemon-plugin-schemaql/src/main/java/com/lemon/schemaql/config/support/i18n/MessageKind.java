package com.lemon.schemaql.config.support.i18n;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 名称：国际化消息分类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/4/30
 */
@Data
@Accessors
@EqualsAndHashCode(of = "prefix")
public class MessageKind {

    /**
     * 描述
     */
    private String description;

    /**
     * 顺序
     */
    private int rank;

    /**
     * key的前缀
     */
    private String prefix;

    /**
     * 前缀与具体内容的分隔符，默认是-
     */
    private String separator = "-";

    /**
     * 是否按序列排号，默认true
     */
    private boolean seqBoolean = true;

    /**
     * 序号的固定宽度，默认4（seqBoolean = true有效）
     */
    private int seqLength = 4;

    /**
     * 当前最大序列，0代表没有（seqBoolean = true有效）
     */
    private int maxSequence = 0;

}
