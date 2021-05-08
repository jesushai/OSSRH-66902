package com.lemon.schemaql.engine.parser.json;

import com.baomidou.mybatisplus.annotation.IdType;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.io.FileUtil;
import com.lemon.schemaql.config.EntitySetting;
import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import com.lemon.schemaql.util.JasyptPBECliUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.lemon.schemaql.engine.SchemaQlContext.jsonRootPath;
import static com.lemon.schemaql.engine.SchemaQlContext.projectSchemaConfig;

/**
 * 名称：项目结构仓库<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/29
 */
@Slf4j
public class ProjectSchemaRepository extends AbstractSchemaRepository<ProjectSchemaConfig> {

    private static final String JSON_FILE_NAME = "project.json";

    public ProjectSchemaRepository() {
        super(FileUtil.mergePath(jsonRootPath(), JSON_FILE_NAME));
    }

    @Override
    public ProjectSchemaConfig parse() {
        ProjectSchemaConfig projectSchemaConfig = super.parse();
        // 判断实体设置是否为空，空则默认创建
        if (null == projectSchemaConfig.getEntitySettings()) {
            setDefaultEntitySettings(projectSchemaConfig);
        }

        // 数据源配置解密处理
        if (null != projectSchemaConfig.getDataSources()) {
            projectSchemaConfig.getDataSources().forEach(ds -> {
                ds.setDecryptUrl(decrypt(ds.getUrl()));
                ds.setDecryptUsername(decrypt(ds.getUsername()));
                ds.setDecryptPassword(decrypt(ds.getPassword()));
            });
        }

        // 装载Modules
        if (CollectionUtils.isNotEmpty(projectSchemaConfig.getModules())) {
            Set<ModuleSchemaConfig> moduleSchemaConfigs = new HashSet<>(projectSchemaConfig.getModules().size());
            projectSchemaConfig.getModules()
                    .forEach(m -> moduleSchemaConfigs.add(
                            new ModuleSchemaRepository(m).parse()));
            projectSchemaConfig.setModuleSchemas(moduleSchemaConfigs);
        }

        // i18n
        if (CollectionUtils.isNotEmpty(projectSchemaConfig.getI18n())) {
            projectSchemaConfig.getI18n().forEach(i18NConfig -> {
                if (!StringUtils.hasText(i18NConfig.getBaseName()) || !StringUtils.hasText(i18NConfig.getPath())) {
                    new ExceptionBuilder<>(SystemException.class)
                            .messageTemplate("I18N config is not valid.")
                            .throwIt();
                }
                i18NConfig.setProjectSchemaConfig(projectSchemaConfig);
            });
        }

        return projectSchemaConfig;
    }

    private void setDefaultEntitySettings(ProjectSchemaConfig config) {
        EntitySetting es = new EntitySetting()
                .setIdType(IdType.ASSIGN_ID)
                .setSuffix("Entity")
                .setVersionColumn("rev_")
                .setDeletedColumn("deleted_")
                .setTenantColumn("tenant_")
                .setCreateTimeColumn("create_time_")
                .setCreateByColumn("create_by_")
                .setCreateNameByColumn("create_name_by_")
                .setModifiedTimeColumn("modified_time_")
                .setModifiedByColumn("modified_by_")
                .setModifiedNameByColumn("modified_name_by_");
        config.setEntitySettings(es);
        this.save(config, false);
    }

    public void save(boolean deepProcess) {
        this.save(projectSchemaConfig(), deepProcess);
    }

    @Override
    public void save(ProjectSchemaConfig schema, boolean deepProcess) {
        super.save(schema, false);
        if (deepProcess) {
            if (CollectionUtils.isNotEmpty(schema.getModuleSchemas())) {
                schema.getModuleSchemas()
                        .forEach(m ->
                                new ModuleSchemaRepository(m.getModuleName())
                                        .save(m, true));
            }
        }
    }

    private String decrypt(String s) {
        int start = s.indexOf("ENC(");
        if (StringUtils.hasText(s) && start >= 0) {
            String password = System.getProperty("jasypt.encryptor.password");
            if (null == password) {
                throw new RuntimeException("Need jvm argument -Djasypt.encryptor.password");
            }

            String algorithm = Optional.ofNullable(System.getProperty("jasypt.encryptor.algorithm"))
                    .orElse("PBEWITHHMACSHA512ANDAES_256");

            String ivGeneratorClassName = Optional.ofNullable(System.getProperty("jasypt.encryptor.ivGeneratorClassName"))
                    .orElse("org.jasypt.iv.RandomIvGenerator");

            int end = s.indexOf(')', start);
            if (end < 0) {
                throw new RuntimeException("Illegal encrypted string format, missing terminator \")\".");
            }

            String enc = s.substring(start + 4, end);
            enc = JasyptPBECliUtil.decrypt(enc, password, algorithm, ivGeneratorClassName);
            return s.substring(0, start) + enc + s.substring(end + 1);
        }
        return s;
    }

}
