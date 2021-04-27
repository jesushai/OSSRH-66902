package com.lemon.schemaql.engine.validation;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.support.Message;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.engine.validation.payload.AbstractPayLoadHandler;
import com.lemon.schemaql.enums.OperationTypeEnum;
import com.lemon.schemaql.exception.InputNotValidException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 名称：输入验证器<p>
 * 描述：<p>
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
     * 执行验证<p>
     * 如果提供Module配置信息则根据指定操作类型的分组来执行验证
     *
     * @param bean               要验证的bean
     * @param moduleSchemaConfig 模块的配置（包含了验证器分组）
     * @param operationType      对当前bean要执行的操作
     * @return 如果验证通过，但是有警告项则返回，无警告返回null
     * @throws InputNotValidException 验证未通过
     */
    public List<Message> validate(Object bean, ModuleSchemaConfig moduleSchemaConfig, OperationTypeEnum operationType) {
        // 先验证非分组项
        Set<ConstraintViolation<Object>> constraintViolations = new HashSet<>(validator.validate(bean));

        // 再验证分组的解析
        if (null != operationType && null != moduleSchemaConfig.getValidatorGroups()) {
            moduleSchemaConfig.getValidatorGroups().stream()
                    .filter(x -> ArrayUtils.contains(x.getOperationTypes(), operationType))
                    .forEach(vg -> {
                        Class<?> groupClass = moduleSchemaConfig.getValidatorGroupsClasses().get(vg.getGroupName());
                        if (null != groupClass) {
                            constraintViolations.addAll(validator.validate(bean, groupClass));
                        }
                    });
        }

        if (CollectionUtils.isNotEmpty(constraintViolations)) {
            InputNotValidException exception = new ExceptionBuilder<>(InputNotValidException.class)
                    .code("ARG-NOT-VALID")
                    .build();

            for (ConstraintViolation<Object> violation : constraintViolations) {
                AbstractPayLoadHandler.processPayload(exception, violation);
            }

            if (exception.getErrors().size() > 0)
                throw exception;
            else if (exception.getInfos().size() > 0)
                return exception.getInfos();
        }

        return null;
    }
}
