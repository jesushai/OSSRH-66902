package com.lemon.schemaql.engine.helper;

import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.NotAcceptableException;
import com.lemon.framework.util.LoggerUtils;
import com.lemon.schemaql.config.EntitySchemaConfig;
import com.lemon.schemaql.meta.FieldMeta;
import com.lemon.schemaql.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/8/19
 */
@Slf4j
public class EntitySchemaHelper {

    /**
     * 获取实体的ID
     *
     * @param entity 实体
     * @param config 实体的配置信息
     * @return id值
     */
    public static Serializable getId(Object entity, EntitySchemaConfig config) {
        Assert.notNull(entity, "Entity must not be null");
        Assert.notNull(entity, "EntitySchemaConfig must not be null");
        PropertyDescriptor idPd = getPropertyDescriptor(entity.getClass(), config.getKeyField().getName());
        Assert.notNull(idPd, "Entity class '" + config.getEntityClassName() + "' must have a primary key property!");
        Method readMethod = idPd.getReadMethod();
        try {
            return (Serializable) readMethod.invoke(entity);
        } catch (Exception e) {
            LoggerUtils.error(log, e);
            throw new RuntimeException("Get entity id error. ", e);
        }
    }

    /**
     * 设置实体的默认属性
     *
     * @param entity 实体
     * @param config 实体配置信息
     */
    public static void entityPropertiesDefault(Object entity, EntitySchemaConfig config) {
        Assert.notNull(entity, "Entity must not be null");
        Assert.notNull(config, "EntitySchemaConfig must not be null");
        config.getFields().forEach(fc -> {
            try {
                PropertyDescriptor pd = getPropertyDescriptor(entity.getClass(), fc.getName());
                if (null != pd) {
                    Method readMethod = pd.getReadMethod();
                    if (null != readMethod) {
                        Object value = readMethod.invoke(entity);
                        if (null == value) {
                            if (fc.getAllowNull() && !"Enum".equals(fc.getType()) && !"ValueObject".equals(fc.getType())) {
                                // 设置为默认属性
                                value = EntityUtils.convertRealValue(fc.getType(), fc.getDefaultVale(), config.getModuleSchemaConfig());
                                Method writeMethod = pd.getWriteMethod();
                                writeMethod.invoke(entity, value);
                            } else {
                                // 不可为空属性或者枚举与值对象则抛出异常
                                new ExceptionBuilder<>(NotAcceptableException.class)
                                        .code("SCHEMAQL-1012")
                                        .throwIt();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 覆盖实体的值，仅复制可编辑的项，并排除乐观锁等字段
     *
     * @param source 源对象
     * @param target 目标对象
     * @param config 实体配置信息
     * @return 是否覆盖了某些属性
     */
    public static boolean entityPropertiesCover(Object source, Object target, EntitySchemaConfig config) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
        Assert.notNull(config, "EntitySchemaConfig must not be null");

        boolean sameClass = source.getClass() == target.getClass();

//        EntitySetting entitySetting = ProjectSchemaHelper.getProjectSchemaConfig().getEntitySettings();
        AtomicBoolean flag = new AtomicBoolean(false);

        config.getFields().stream()
                .filter(FieldMeta::getEditable)
                .forEach(fc -> {
                    // 主键；乐观锁；逻辑删除；租户；审计字段，这些都是不可编辑字段，已经被过滤掉了
                    // 属性描述器
                    PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), fc.getName());
                    PropertyDescriptor targetPd;
                    if (sameClass) {
                        targetPd = sourcePd;
                    } else {
                        targetPd = getPropertyDescriptor(target.getClass(), fc.getName());
                    }

                    try {
                        if (null != sourcePd && null != targetPd) {
                            // getter
                            Method readMethod = sourcePd.getReadMethod();
                            // setter
                            Method writeMethod = targetPd.getWriteMethod();

                            if (null != readMethod && null != writeMethod) {
                                Object value = readMethod.invoke(source);
                                // 字段允许为空或值不为空则覆盖
                                // 字段不允许为空，但值为空的则忽略掉
                                if (null != value || fc.getAllowNull()) {
                                    writeMethod.invoke(target, value);
                                    flag.set(true);
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return flag.get();
    }
}
