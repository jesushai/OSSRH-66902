package com.lemon.framework.db.jpa.repository;

import com.lemon.framework.util.sequence.SequenceGenerator;
import com.lemon.framework.util.spring.SpringContextUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.SingularAttribute;
import javax.transaction.Transactional;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 名称：通用JpaRepository实现类<p>
 * 描述：<p>
 * save方法判断是否新增<p>
 * 如果是新增则自动填充ID，否则更新记录忽略null值
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
public class CustomSimpleJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;
    private SequenceGenerator sequenceGenerator;

    @Autowired
    public CustomSimpleJpaRepository(
            JpaEntityInformation<T, ?> entityInformation,
            EntityManager entityManager) {

        super(entityInformation, entityManager);
        this.em = entityManager;
        this.entityInformation = entityInformation;
        this.sequenceGenerator = SpringContextUtils.getBean(SequenceGenerator.class);
    }

    private boolean isEmptyId(ID entityId) {
        if (entityId == null) {
            return true;
        }
        return ((Long) entityId) == 0L;
    }

    /**
     * 通用save方法 ：新增/选择性更新
     *
     * @param entity 实体
     * @return 保存后的实体
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        //获取ID
        ID entityId = (ID) entityInformation.getId(entity);
        Optional<T> optionalT;
        if (isEmptyId(entityId)) {
            long id = sequenceGenerator.nextId();
            SingularAttribute attribute = entityInformation.getIdAttribute();
            if (attribute != null) {
                new BeanWrapperImpl(entity)
                        .setPropertyValue(attribute.getName(), id);
            }
            //标记为新增数据
            optionalT = Optional.empty();
        } else {
            //若ID非空 则查询最新数据
            optionalT = findById(entityId);
        }
        //获取空属性并处理成null
        String[] nullProperties = getNullProperties(entity);
        //若根据ID查询结果为空
        if (!optionalT.isPresent()) {
            em.persist(entity);//新增
        } else {
            //1.获取最新对象
            T target = optionalT.get();
            //2.将非空属性覆盖到最新对象
            BeanUtils.copyProperties(entity, target, nullProperties);
            //3.更新非空属性
            entity = (S) em.merge(target);
        }
        return entity;
    }

    /**
     * 获取对象的空属性
     *
     * @param src 源对象
     * @return 空属性名
     */
    private static String[] getNullProperties(Object src) {
        //1.获取Bean
        BeanWrapper srcBean = new BeanWrapperImpl(src);
        //2.获取Bean的属性描述
        PropertyDescriptor[] pds = srcBean.getPropertyDescriptors();
        //3.获取Bean的空属性
        Set<String> properties = new HashSet<>();

        for (PropertyDescriptor propertyDescriptor : pds) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = srcBean.getPropertyValue(propertyName);

            if (propertyValue == null) {
                srcBean.setPropertyValue(propertyName, null);
                properties.add(propertyName);
            }
        }

        return properties.toArray(new String[0]);
    }
}
