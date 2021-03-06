package com.lemon.schemaql.engine.parser.input;

import com.lemon.schemaql.enums.InputTypeEnum;
import com.lemon.schemaql.enums.QueryTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：查询的输入结构<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryInput extends AbstractInput {

    /**
     * 请求类型：query|mutation
     */
    private InputTypeEnum type = InputTypeEnum.query;

    /**
     * get|gets
     */
    private QueryTypeEnum queryType;

    private InputArgument input;
}
