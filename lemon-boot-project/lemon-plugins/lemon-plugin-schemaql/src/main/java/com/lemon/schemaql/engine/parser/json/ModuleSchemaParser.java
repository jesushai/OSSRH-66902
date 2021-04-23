package com.lemon.schemaql.engine.parser.json;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.config.EnumSchemaConfig;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.ValueObjectSchemaConfig;
import com.lemon.schemaql.config.support.TypeHandlerConfig;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
public class ModuleSchemaParser extends AbstractJsonResourceParser<ModuleSchemaConfig> {

    public static final String JSON_FILE_NAME = "module-${name}.json";

    private String rootPath;

    public ModuleSchemaParser(String rootPath, String moduleName) {
        super(rootPath + '/' + JSON_FILE_NAME.replace("${name}", moduleName));
        this.rootPath = rootPath;
    }

    @Override
    public ModuleSchemaConfig parse() {
        ModuleSchemaConfig moduleSchemaConfig = super.parse();

        // region 装载模块下的类型转换器
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getTypeHandlers())) {
            Set<TypeHandlerConfig> typeHandlerConfigs = new HashSet<>(moduleSchemaConfig.getTypeHandlers().size());
            moduleSchemaConfig.getTypeHandlers().forEach(t -> typeHandlerConfigs.add(
                    new TypeHandlerSchemaParser(rootPath, moduleSchemaConfig.getModuleName(), t)
                            .parse()));
            moduleSchemaConfig.setTypeHandlerConfigs(typeHandlerConfigs);
        }
        // endregion

        // region 装载模块下的实体
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getEntities())) {
            Set<EntitySchemaConfig> entitySchemaConfigs = new HashSet<>(moduleSchemaConfig.getEntities().size());
            moduleSchemaConfig.getEntities().forEach(e -> {
                EntitySchemaConfig entitySchemaConfig
                        = new EntitySchemaParser(rootPath, moduleSchemaConfig.getModuleName(), e).parse();
                entitySchemaConfig.setModuleSchemaConfig(moduleSchemaConfig);
                entitySchemaConfigs.add(entitySchemaConfig);
            });
            moduleSchemaConfig.setEntitySchemas(entitySchemaConfigs);
        }
        // endregion

        // region 装载模块下的ValueObject
        if (CollectionUtils.isNotEmpty(moduleSchemaConfig.getValueObjects())) {
            Set<ValueObjectSchemaConfig> voSchemaConfigs = new HashSet<>(moduleSchemaConfig.getValueObjects().size());
            moduleSchemaConfig.getValueObjects().forEach(vo -> voSchemaConfigs.add(
                    new ValueObjectSchemaParser(rootPath, moduleSchemaConfig.getModuleName(), vo)
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
                        = new EnumSchemaParser(rootPath, moduleSchemaConfig.getModuleName(), e).parse();
                enumSchemaConfig.setModuleSchemaConfig(moduleSchemaConfig);
                enumSchemaConfigs.add(enumSchemaConfig);
            });
            moduleSchemaConfig.setEnumSchemas(enumSchemaConfigs);
        }
        // endregion

        return moduleSchemaConfig;
    }
}
