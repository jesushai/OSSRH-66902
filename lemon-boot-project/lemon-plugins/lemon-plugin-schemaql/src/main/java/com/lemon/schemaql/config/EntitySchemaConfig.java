package com.lemon.schemaql.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.util.spring.SpringContextUtils;
import com.lemon.schemaql.config.aspect.AspectsConfig;
import com.lemon.schemaql.config.support.DynamicDatasourceConfig;
import com.lemon.schemaql.meta.EntityMeta;
import com.lemon.schemaql.meta.FieldMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/27
 */
@EqualsAndHashCode(callSuper = true, of = {"tableName"})
@Data
@Accessors(chain = true)
@SuppressWarnings("unchecked")
public class EntitySchemaConfig extends EntityMeta<FieldSchemaConfig> implements CanInput {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 对应Mybatis Mapper类名
     */
    private String mapperName;

    /**
     * 对应Mybatis mapper xml的名字
     */
    private String xmlName;

    /**
     * 所属子模块
     */
    private String subModuleName;

    /**
     * 是否排序租户：即表中没有租户字段
     */
    private boolean excludeTenant;

    /**
     * 动态数据源
     */
    private DynamicDatasourceConfig[] dynamicDataSources;

    /**
     * 授权与功能
     */
    private Set<PermissionsConfig> permissionsDescriptions;

    /**
     * 切片服务
     */
    private AspectsConfig aspects;

    /**
     * 包含的字段
     */
    private Set<FieldSchemaConfig> fields = new HashSet<>();

    @Override
    public EntityMeta toMeta() {
        EntityMeta meta = new EntityMeta();
        BeanUtils.copyProperties(this, meta, "fields");
        if (CollectionUtils.isNotEmpty(this.getFields())) {
            meta.setFields(new HashSet<>(this.getFields().size()));
            this.getFields().forEach(f -> meta.getFields().add(f.toMeta()));
        }
        return meta;
    }

    /**
     * 主键字段
     */
    @JsonIgnore
    private FieldMeta keyField;

    /**
     * 父级模块的配置对象引用
     */
    @JsonIgnore
    private ModuleSchemaConfig moduleSchemaConfig;

    /**
     * 实体类全名
     */
    @JsonIgnore
    private String entityClassName;

    @JsonIgnore
    private String mapperClassName;

    @JsonIgnore
    public String getEntityClassName() {
        if (null == entityClassName) {
            StringBuilder sb = new StringBuilder(moduleSchemaConfig.getBasePackage());
            // 子模块加入到包名
            if (StringUtils.isNotEmpty(this.subModuleName)) {
                sb.append('.').append(this.subModuleName);
            }
            // 实体包名
            if (StringUtils.isNotEmpty(moduleSchemaConfig.getEntityBasePackage())) {
                sb.append('.').append(moduleSchemaConfig.getEntityBasePackage());
            }
            // 实体类名
            entityClassName = sb.append('.').append(this.getEntityName()).toString();
        }
        return entityClassName;
    }

    @JsonIgnore
    public String getMapperClassName() {
        if (null == mapperClassName) {
            StringBuilder sb = new StringBuilder(moduleSchemaConfig.getBasePackage());
            if (StringUtils.isNotEmpty(this.subModuleName)) {
                sb.append('.').append(this.subModuleName);
            }
            if (StringUtils.isNotEmpty(moduleSchemaConfig.getMapperBasePackage())) {
                sb.append('.').append(moduleSchemaConfig.getMapperBasePackage());
            }
            mapperClassName = sb.append('.').append(this.getMapperName()).toString();
        }
        return mapperClassName;
    }

    @JsonIgnore
    public Class<?> getEntityClass() {
        String className = getEntityClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1004")
                    .args(className)
                    .build();
        }
    }

    @JsonIgnore
    public Class<? extends BaseMapper<?>> getMapperClass() {
        String className = getMapperClassName();
        try {
            return (Class<? extends BaseMapper<?>>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1006")
                    .args(className)
                    .build();
        }
    }

    public Object newEntity() {
        Class<?> clazz = getEntityClass();
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new ExceptionBuilder<>(SystemException.class)
                    .code("SCHEMAQL-1005")
                    .args(clazz.getName())
                    .build();
        }
    }

    @JsonIgnore
    public <T extends BaseMapper<?>> T getMapper() {
        String mapperClassName = getMapperClassName();
        try {
            return (T) SpringContextUtils.getBean(mapperClassName);
        } catch (Exception e) {
            throw new ExceptionBuilder<>(SystemException.class)
                    .code("SCHEMAQL-1007")
                    .args(mapperClassName)
                    .build();
        }
    }

}
