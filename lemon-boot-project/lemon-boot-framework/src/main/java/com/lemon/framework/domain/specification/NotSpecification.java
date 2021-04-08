package com.lemon.framework.domain.specification;

/**
 * <b>名称：NOT，用于创建一个新规范</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/9/10
 */
public class NotSpecification<T> extends AbstractSpecification<T> {

    private Specification<T> spec1;

    /**
     * 创建一个NOT规则，在其他规则之上
     */
    public NotSpecification(final Specification<T> spec1) {
        this.spec1 = spec1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSatisfiedBy(final T t) {
        return !spec1.isSatisfiedBy(t);
    }
}
