package com.lemon.schemaql.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.schemaql.config.support.TypeHandlerConfig;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/25
 */
@EqualsAndHashCode(callSuper = false, of = {"moduleName"})
@Data
@Accessors(chain = true)
public class ModuleSchemaConfig extends Schema {

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模块根路径，相对于工程路径
     */
    private String modulePath;

    /**
     * 源文件根路径，相对于modulePath
     */
    private String sourcesPath = "/src/main/java";

    /**
     * 资源根路径，相对于modulePath
     */
    private String resourcePath = "/src/main/resources";

    /**
     * 模块基础包
     */
    private String basePackage;

    /**
     * 子模块名，实体等对象可以建在子模块下
     */
    private Set<String> subModuleNames;

    /**
     * 模块内包含的类型转换器
     */
    private Set<String> typeHandlers;

    /**
     * 模块内包含的类型转换器
     */
    @JsonIgnore
    private Set<TypeHandlerConfig> typeHandlerConfigs;

    /**
     * 实体所在的包根路径
     */
    private String entityBasePackage = "entity";

    /**
     * Mapper所在的包根路径
     */
    private String mapperBasePackage = "mapper";

    /**
     * 包含的实体名称
     */
    private Set<String> entities;

    /**
     * 包含的实体
     */
    @JsonIgnore
    private Set<EntitySchemaConfig> entitySchemas;

    /**
     * 值对象包
     */
    private String valueObjectPackage = "vo";

    /**
     * 模块内包含的值对象
     */
    private Set<String> valueObjects;

    /**
     * 包含的值对象
     */
    @JsonIgnore
    private Set<ValueObjectSchemaConfig> valueObjectSchemas;

    // TODO: DTO
    private String dtoPackage = "dto";

    private Set<String> dtoObjects;

    @JsonIgnore
    private Set<DTOSchemaConfig> dtoObjectSchemas;

    /**
     * 枚举所在包，默认 enums
     */
    private String enumBasePackage = "enums";

    /**
     * 模块下的所有枚举
     */
    private Set<String> enums;

    /**
     * 缓存模块下的所有枚举
     */
    @JsonIgnore
    private Set<EnumSchemaConfig> enumSchemas;

    /**
     * 验证器分组
     */
    private Set<ValidatorGroups> validatorGroups;

    @JsonIgnore
    private Map<String, Class<?>> validatorGroupsClasses;

    @EqualsAndHashCode(of = "groupName")
    @Data
    public static class ValidatorGroups {
        /**
         * 验证器分组包路径
         */
        private String packageName;

        /**
         * 注释
         */
        private String comment;

        /**
         * 验证器分组名
         */
        private String groupName;

        /**
         * 验证适用于哪些操作类型：get|gets|create|update|delete
         */
        private OperationTypeEnum[] operationTypes;
    }
}
