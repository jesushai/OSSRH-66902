package com.lemon.framework.db.sequence;

import com.lemon.framework.util.sequence.SequenceGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * 名称：JPA实体自动ID生成器<p>
 * 描述：<p>
 * Example:<p>
 * {@literal @Id}<p>
 * {@literal @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")}<p>
 * {@literal @GenericGenerator(name = "custom-id", strategy = "com.lemon.framework.db.sequence.JpaCustomIdGenerator")}<p>
 * {@literal @Column(name = "id_")}<p>
 * private Long id;<p>
 *
 * @author hai-zhang
 * @since 2020/4/28
 * @deprecated 已经不需要了
 */
@Deprecated
public class JpaCustomIdGenerator extends IdentityGenerator {

    final private SequenceGenerator generator;

    public JpaCustomIdGenerator(SequenceGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        return generator.nextId();
    }
}
