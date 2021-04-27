package com.lemon.schemaql.engine.parser.json;

import com.lemon.schemaql.config.ModuleSchemaConfig;
import com.lemon.schemaql.config.ProjectSchemaConfig;
import com.lemon.schemaql.util.JasyptPBECliUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
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

        // 数据源配置解密处理
        if (null != projectSchemaConfig.getDataSources()) {
            projectSchemaConfig.getDataSources().forEach(ds -> {
                ds.setUrl(decrypt(ds.getUrl()));
                ds.setUsername(decrypt(ds.getUsername()));
                ds.setPassword(decrypt(ds.getPassword()));
            });
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
