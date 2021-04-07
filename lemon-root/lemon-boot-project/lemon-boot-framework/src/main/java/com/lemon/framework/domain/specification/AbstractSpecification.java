package com.lemon.framework.domain.specification;

/**
 * <b>名称：复合{@link Specification}的默认抽象基础实现</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2019/9/10
 */
public abstract class AbstractSpecification<T> implements Specification<T> {

    @Override
    public abstract boolean isSatisfiedBy(T t);

    @Override
    public Specification<T> and(final Specification<T> specification) {
        return new AndSpecification<T>(this, specification);
    }

    @Override
    public Specification<T> or(final Specification<T> specification) {
        return new OrSpecification<T>(this, specification);
    }

    @Override
    public Specification<T> not(final Specification<T> specification) {
        return new NotSpecification<T>(specification);
    }
}
