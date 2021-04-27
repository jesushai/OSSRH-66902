package com.lemon.schemaql.engine.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.exception.NotFoundException;
import com.lemon.framework.exception.support.Message;
import com.lemon.framework.protocal.Result;
import com.lemon.framework.util.JacksonUtils;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.annotation.SchemaQlDS;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.engine.cache.EntityCacheManager;
import com.lemon.schemaql.engine.helper.EntitySchemaHelper;
import com.lemon.schemaql.engine.helper.ProjectSchemaHelper;
import com.lemon.schemaql.engine.parser.input.MutationInput;
import com.lemon.schemaql.engine.validation.InputValidator;
import com.lemon.schemaql.enums.OperationTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：统一变更服务<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/7/30
 */
@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SchemaQlMutationService implements ISchemaQlService<MutationInput> {

    // 输入校验器
    @Autowired
    private InputValidator validator;

    @Autowired(required = false)
    private EntityCacheManager entityCacheManager;

    @Transactional(rollbackFor = Exception.class)
    @SchemaQlDS
    @Override
    public Object entrance(MutationInput input) {
        // 验证输入
        if (null == input.getMutationType()
                || null == input.getTarget()
                || null == input.getInput()
                || null == input.getInput().getBody()) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1016")
                    .throwIt();
        }

        Object result = null;
        switch (input.getMutationType()) {
            case create:
                result = create(input);
                break;
            case update:
                result = update(input);
                break;
            case delete:
                result = delete(input);
                break;
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @SchemaQlDS
    public Object entrance(List<MutationInput> inputs) {
        List<Object> results = new ArrayList<>(inputs.size());

        inputs.forEach(input -> {
            Object result = entrance(input);
            if (null != result) {
                results.add(result);
            }
        });

        return results;
    }

    private Object create(MutationInput input) {
        EntitySchemaConfig entitySchemaConfig = getTargetEntity(input.getTarget());
        Object entity = JacksonUtils.parseObject(input.getInput().getBody(), entitySchemaConfig.getEntityClass());

        // 验证输入的内容
        List<Message> infos = validator.validate(entity, entitySchemaConfig.getModuleSchemaConfig(), OperationTypeEnum.create);

        // 设置实体的默认属性
        EntitySchemaHelper.entityPropertiesDefault(entity, entitySchemaConfig);

        // 执行保存操作
        BaseMapper<Object> mapper = entitySchemaConfig.getMapper();
        if (mapper.insert(entity) <= 0) {
            new ExceptionBuilder<>()
                    .code("SCHEMAQL-1008")
                    .throwIt();
        }

        // 写入缓存
        if (null != entityCacheManager)
            entityCacheManager.put(entity, entitySchemaConfig);

        if (CollectionUtils.isNotEmpty(infos)) {
            return Result.ok().data(entity).addInfos(infos);
        } else {
            return entity;
        }
    }

    private Object update(MutationInput input) {
        EntitySchemaConfig entitySchemaConfig = getTargetEntity(input.getTarget());
        Object entity = JacksonUtils.parseObject(input.getInput().getBody(), entitySchemaConfig.getEntityClass());

        // 验证输入的内容
        List<Message> infos = validator.validate(entity, entitySchemaConfig.getModuleSchemaConfig(), OperationTypeEnum.update);

        // 执行保存操作
        BaseMapper<Object> mapper = entitySchemaConfig.getMapper();

        // 从数据库中获取托管实体
        Object dbEntity = mapper.selectById(EntitySchemaHelper.getId(entity, entitySchemaConfig));
        if (null == dbEntity) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1011")
                    .throwIt();
        }

        // 将传入的实体信息覆盖到托管实体中
        // 写回数据库中
        if (EntitySchemaHelper.entityPropertiesCover(entity, dbEntity, entitySchemaConfig)
                && mapper.updateById(dbEntity) <= 0) {
            new ExceptionBuilder<>().code("SCHEMAQL-1008").throwIt();
        }

        // 更新缓存
        if (null != entityCacheManager)
            entityCacheManager.put(entity, entitySchemaConfig);

        if (CollectionUtils.isNotEmpty(infos)) {
            return Result.ok().data(dbEntity).addInfos(infos);
        } else {
            return dbEntity;
        }
    }

    private Object delete(MutationInput input) {
        EntitySchemaConfig entitySchemaConfig = getTargetEntity(input.getTarget());
        Long id = null;
        try {
            id = JacksonUtils.parseLong(input.getInput().getBody(), "id");
        } catch (Exception e) {
            LoggerUtils.error(log, e.getMessage());
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1017")
                    .args("id", input.getInput())
                    .throwIt();
        }
        // 验证
        if (null == id || id <= 0) {
            new ExceptionBuilder<>(NotAcceptableException.class)
                    .code("SCHEMAQL-1002")
                    .throwIt();
        }
        // 执行删除
        BaseMapper<Object> mapper = entitySchemaConfig.getMapper();
        if (mapper.deleteById(id) <= 0) {
            new ExceptionBuilder<>().code("SCHEMAQL-1014").throwIt();
        }
        // 擦除缓存
        if (null != entityCacheManager)
            entityCacheManager.evict(id, entitySchemaConfig);
        return null;
    }

    private EntitySchemaConfig getTargetEntity(String target) {
        EntitySchemaConfig entitySchemaConfig = ProjectSchemaHelper.getEntitySchema(target);
        if (null == entitySchemaConfig) {
            new ExceptionBuilder<>(NotFoundException.class)
                    .code("SCHEMAQL-1001")
                    .throwIt();
        }
        return entitySchemaConfig;
    }

}
