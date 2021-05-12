package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.EnumSchemaConfig;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.ValueObjectSchemaConfig;
import com.lemon.schemaql.config.support.TypeHandlerConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ModuleSchemaRepository extends AbstractSchemaRepository<ModuleSchemaConfig> {

    private static final String JSON_FILE_NAME = "module-${name}.json";

    public ModuleSchemaRepository(String moduleName) {
        super(FileUtil.mergePath(jsonRootPath(), JSON_FILE_NAME.replace("${name}", moduleName)));
    }

    @Override
    public ModuleSchemaConfig parse() {
        ModuleSchemaConfig moduleSchemaConfig = super.parse();

        // region 装载模块下的类型转换器
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getTypeHandlers())) {
            Set<TypeHandlerConfig> typeHandlerConfigs = new HashSet<>(moduleSchemaConfig.getTypeHandlers().size());
            moduleSchemaConfig.getTypeHandlers().forEach(t -> typeHandlerConfigs.add(
                    new TypeHandlerSchemaRepository(moduleSchemaConfig.getModuleName(), t)
                            .parse()));
            moduleSchemaConfig.setTypeHandlerConfigs(typeHandlerConfigs);
        }
        // endregion

        // region 装载模块下的实体
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getEntities())) {
            Set<EntitySchemaConfig> entitySchemaConfigs = new HashSet<>(moduleSchemaConfig.getEntities().size());
            moduleSchemaConfig.getEntities().forEach(e -> {
                EntitySchemaConfig entitySchemaConfig
                        = new EntitySchemaRepository(moduleSchemaConfig.getModuleName(), e).parse();
                entitySchemaConfig.setModuleSchemaConfig(moduleSchemaConfig);
                entitySchemaConfigs.add(entitySchemaConfig);
                // 关联类型转换器
                if (null != entitySchemaConfig.getFields()) {
                    entitySchemaConfig.getFields().stream()
                            .filter(x -> StringUtils.hasText(x.getTypeHandler()))
                            .forEach(field -> {
                                field.setTypeHandlerConfig(
                                        moduleSchemaConfig.getTypeHandlerConfigs().stream()
                                                .filter(x -> x.getName().equals(field.getTypeHandler()))
                                                .findFirst()
                                                .orElse(null)
                                );
                            });
                }
            });
            moduleSchemaConfig.setEntitySchemas(entitySchemaConfigs);
        }
        // endregion

        // region 装载模块下的ValueObject
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getValueObjects())) {
            Set<ValueObjectSchemaConfig> voSchemaConfigs = new HashSet<>(moduleSchemaConfig.getValueObjects().size());
            moduleSchemaConfig.getValueObjects().forEach(vo -> voSchemaConfigs.add(
                    new ValueObjectSchemaRepository(moduleSchemaConfig.getModuleName(), vo)
                            .parse()));
            moduleSchemaConfig.setValueObjectSchemas(voSchemaConfigs);
        }
        // endregion

        // region 装载模块下的验证器分组
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getValidatorGroups())) {
            Map<String, Class<?>> vgClasses = new HashMap<>(moduleSchemaConfig.getValidatorGroups().size());
            moduleSchemaConfig.getValidatorGroups().forEach(vg -> {
                try {
                    vgClasses.put(vg.getGroupName(), Class.forName(vg.getPackageName() + "." + vg.getGroupName()));
                } catch (ClassNotFoundException e) {
                    new ExceptionBuilder<>(NotFoundException.class)
                            .code("SCHEMAQL-1009")
                            .args(vg.getPackageName() + "." + vg.getGroupName())
                            .throwIt();
                }
            });
            moduleSchemaConfig.setValidatorGroupsClasses(vgClasses);
        }
        // endregion

        // region 装载模块下的枚举
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getEnums())) {
            Set<EnumSchemaConfig> enumSchemaConfigs = new HashSet<>(moduleSchemaConfig.getEnums().size());
            moduleSchemaConfig.getEnums().forEach(e -> {
                EnumSchemaConfig enumSchemaConfig
                        = new EnumSchemaRepository(moduleSchemaConfig.getModuleName(), e).parse();
                enumSchemaConfig.setModuleSchemaConfig(moduleSchemaConfig);
                enumSchemaConfigs.add(enumSchemaConfig);
            });
            moduleSchemaConfig.setEnumSchemas(enumSchemaConfigs);
        }
        // endregion

        return moduleSchemaConfig;
    }
}
