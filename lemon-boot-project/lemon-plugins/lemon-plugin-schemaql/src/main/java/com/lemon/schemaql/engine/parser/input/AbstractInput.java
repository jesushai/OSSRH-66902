package com.lemon.schemaql.engine.parser.input;

import com.lemon.schemaql.enums.InputTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称：输入请求虚拟基类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@EqualsAndHashCode
@Data
public abstract class AbstractInput {

    /**
     * 请求的目标：实体、DTO等
     */
    private String target;

    /**
     * @return 请求类型：query|mutation
     */
    public abstract InputTypeEnum getType();

    public abstract InputArgument getInput();

//    /**
//     * 获取输入的属性值
//     *
//     * @param inputField 输入属性字段名
//     * @return 如果没有找到抛出异常
//     */
//    public Object getInputProperty(String inputField) {
//        if (null == getInput()) {
//            new ExceptionBuilder<>(NotAcceptableException.class).code("SCHEMAQL-1016").throwIt();
//        }
//
//        Map<String, Object> input = (Map<String, Object>) getInput();
//        if (!input.containsKey(inputField)) {
//            new ExceptionBuilder<>(NotAcceptableException.class)
//                    .code("SCHEMAQL-1017")
//                    .args(inputField)
//                    .throwIt();
//        }
//        return input.get(inputField);
//    }

//    /**
//     * 将输入的值组装成Bean
//     */
//    public Object getInputBean(Class<?> clazz) {
//        Object bean;
//        try {
//            bean = clazz.getConstructor().newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException("Could not create instance of class " + clazz.getName());
//        }
//
//        BeanUtils.mapToBean(getInput(), bean);
//        return bean;
//    }

}
