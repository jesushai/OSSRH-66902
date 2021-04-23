package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@Slf4j
public class ProjectSchemaParser extends AbstractJsonResourceParser<ProjectSchemaConfig> {

    public static final String JSON_FILE_NAME = "project.json";

    private final String rootPath;

    public ProjectSchemaParser(String rootPath) {
        super(rootPath + '/' + JSON_FILE_NAME);
        this.rootPath = rootPath;
    }

    @Override
    public ProjectSchemaConfig parse() {
        ProjectSchemaConfig projectSchemaConfig = super.parse();
        // 判断实体设置是否为空
        if (null == projectSchemaConfig.getEntitySettings()) {
            throw new RuntimeException("Entity settings must not be null");
        }

        // 装载Modules
        if (CollectionUtils.isNotEmpty(projectSchemaConfig.getModules())) {
            Set<ModuleSchemaConfig> moduleSchemaConfigs = new HashSet<>(projectSchemaConfig.getModules().size());
            projectSchemaConfig.getModules()
                    .forEach(m -> moduleSchemaConfigs.add(
                            new ModuleSchemaParser(rootPath, m).parse()));
            projectSchemaConfig.setModuleSchemas(moduleSchemaConfigs);
        }
        return projectSchemaConfig;
    }
}
