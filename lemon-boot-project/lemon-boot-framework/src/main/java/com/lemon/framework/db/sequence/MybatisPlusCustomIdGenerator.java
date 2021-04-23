package com.lemon.framework.db.sequence;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.lemon.framework.util.sequence.SequenceGenerator;

/**
 * 名称：MybatisPlus的自定义ID生成器<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
public class MybatisPlusCustomIdGenerator implements IdentifierGenerator {

    final private SequenceGenerator generator;

    public MybatisPlusCustomIdGenerator(SequenceGenerator generator) {
        this.generator = generator;
    }

    @Override
    public Number nextId(Object entity) {
        return generator.nextId();
    }

}
