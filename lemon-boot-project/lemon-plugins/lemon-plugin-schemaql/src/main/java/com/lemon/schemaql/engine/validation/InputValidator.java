package com.lemon.schemaql.engine.validation;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.enums.OperationTypeEnum;
import com.lemon.schemaql.exception.InputNotValidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

/**
 * <b>名称：输入验证器</b><br/>
 * <b>描述：</b><br/>
 * 对实体的新增与修改进行验证
 *
 * @author hai-zhang
 * @since 2020/7/31
 */
public class InputValidator {

    private final Validator validator;

    public InputValidator(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }

    /**
     * 执行验证<br/>
     * 如果提供Module配置信息则根据指定操作类型的分组来执行验证
     *
     * @param bean               要验证的bean
     * @param moduleSchemaConfig 模块的配置（包含了验证器分组）
     * @param operationType      对当前bean要执行的操作
     * @throws InputNotValidException 验证未通过
     */
    public void validate(Object bean, ModuleSchemaConfig moduleSchemaConfig, OperationTypeEnum operationType) {
        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>();
        if (null != operationType && null != moduleSchemaConfig.getValidatorGroups()) {
            // 验证分组的解析
            moduleSchemaConfig.getValidatorGroups().stream()
                    .filter(x -> ArrayUtils.contains(x.getOperationTypes(), operationType))
                    .forEach(vg -> {
                        Class<?> groupClass = moduleSchemaConfig.getValidatorGroupsClasses().get(vg.getGroupName());
                        if (null != groupClass) {
                            constraintViolations.addAll(validator.validate(bean, groupClass));
                        }
                    });
        } else {
            constraintViolations.addAll(validator.validate(bean));
        }

        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            InputNotValidException exception = new ExceptionBuilder<>(InputNotValidException.class)
                    .code("ARG-NOT-VALID").build();
            for (ConstraintViolation<Object> violation : constraintViolations) {
                exception.addError("", violation.getMessage());
            }
            throw exception;
        }
    }
}
