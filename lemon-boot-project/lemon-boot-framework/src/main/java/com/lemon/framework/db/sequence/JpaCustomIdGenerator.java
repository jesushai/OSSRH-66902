package com.lemon.framework.db.sequence;

import com.lemon.framework.util.sequence.SequenceGenerator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * <b>名称：JPA实体自动ID生成器</b><br/>
 * <b>描述：</b><br/>
 * <p>
 * 已经不需要了
 * <pre>
 * {@code
 *  @Id
 *  @GeneratedValue(strategy = GenerationType.AUTO, generator = "custom-id")
 *  @GenericGenerator(name = "custom-id", strategy = "com.lemon.framework.db.sequence.JpaCustomIdGenerator")
 *  @Column(name = "id_")
 *  private Long id;
 * }
 * </pre>
 *
 * @author hai-zhang
 * @since 2020/4/28
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
