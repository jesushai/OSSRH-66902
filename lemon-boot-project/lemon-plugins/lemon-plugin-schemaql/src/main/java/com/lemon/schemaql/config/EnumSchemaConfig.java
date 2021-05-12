package com.lemon.schemaql.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.schemaql.meta.Meta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * 名称：枚举配置类<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/8/20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true, of = "enumName")
public class EnumSchemaConfig extends Schema implements Meta<EnumSchemaConfig> {

    /**
     * 枚举类型名
     */
    private String enumName;

    /**
     * 枚举注释
     */
    private String comment;

    /**
     * 枚举的值字段名：默认value
     */
    private String valueProperty = "value";

    /**
     * 枚举的显示文本字段：默认display
     */
    private String displayProperty = "display";

    /**
     * 枚举的成员
     */
    private Set<EnumElement> elements;

    @Override
    public EnumSchemaConfig toMeta() {
        return this;
    }

    @JsonIgnore
    @ToString.Exclude
    private ModuleSchemaConfig moduleSchemaConfig;

    @JsonIgnore
    private String enumClassName;

    @JsonIgnore
    public String getEnumClassName() {
        if (null == enumClassName) {
            StringBuilder sb = new StringBuilder(moduleSchemaConfig.getBasePackage());
            if (StringUtils.isNotEmpty(moduleSchemaConfig.getEnumBasePackage())) {
                sb.append('.').append(moduleSchemaConfig.getEnumBasePackage());
            }
            enumClassName = sb.append('.').append(this.getEnumName()).toString();
        }
        return enumClassName;
    }

    @JsonIgnore
    public Class<?> getEnumClass() {
        String className = getEnumClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1013")
                    .args(className)
                    .build();
        }
    }


    @Data
    public static class EnumElement {

        /**
         * 枚举元素名
         */
        private String name;

        /**
         * 枚举的值，默认从0开始
         */
        private Integer value;

        /**
         * 枚举的文本，支持国际化（以%开头）
         */
        private String display;

        /**
         * 描述（可选），默认null
         */
        private String comment;
    }
}
