package com.lemon.schemaql.engine.parser.input;

import lombok.Data;

import java.util.List;

/**
 * <b>名称：输入条件</b><br/>
 * <b>描述：</b><br/>
 * 即过滤条件表达式
 *
 * @author hai-zhang
 * @since 2020/9/1
 */
@Data
public class InputCondition {

    /**
     * 决定rules之间的逻辑操作符<br/>
     * AND | OR
     */
    private ConditionEnum condition;

    /**
     * 过滤条件
     */
    private List<RuleExpression> rules;

    /**
     * 条件分组（括号）
     */
    private List<InputCondition> group;

    private enum ConditionEnum {
        AND, OR
    }

    @Data
    private static class RuleExpression {
        private String field;
        private OperatorEnum operator;
        private String type;
        private Object value;
    }

    private enum OperatorEnum {
        eq("equals"),
        notEq("notEquals"),
        lLike("leftLike"),
        rLike("rightLike"),
        like("contains"),
        notLike("notLike"),
        lt("lessThan"),
        le("lessThanOrEquals"),
        gt("gatherThan"),
        ge("gatherThanOrEquals"),
        in("in"),
        notIn("notIn"),
        empty("empty"),
        notEmpty("notEmpty");

        private String display;

        public String getDisplay() {
            return display;
        }

        OperatorEnum(String display) {
            this.display = display;
        }
    }
}
