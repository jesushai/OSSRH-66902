package com.lemon.schemaql.engine.parser.input;

import com.lemon.schemaql.enums.InputTypeEnum;
import com.lemon.schemaql.enums.MutationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MutationInput extends AbstractInput {

    /**
     * 请求类型：query|mutation
     */
    private InputTypeEnum type = InputTypeEnum.mutation;

    /**
     * create|update|delete
     */
    private MutationTypeEnum mutationType;

    /**
     * 修改的内容
     */
    private InputArgument input;

}
