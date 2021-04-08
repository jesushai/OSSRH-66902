package com.lemon.framework.domain.specification;

/**
 * <b>名称：规范接口</b><br/>
 * <b>描述：</b><br/>
 * 只有方法{@link #isSatisfiedBy（isSatisfiedBy）}必须实现。
 *
 * @author hai-zhang
 * @since 2019/9/10
 */
public interface Specification<T> {

    /**
     * 检查{@code t}对象是否满足规范
     *
     * @param t 被检查的对象
     * @return {@code true}如果对象{@code t}满足规范要求。
     */
    boolean isSatisfiedBy(T t);

    /**
     * 建立一套规范去校验对象{@code this}
     *
     * @param specification 必须同时满足的规范
     * @return 一个新创建的规范
     */
    Specification<T> and(Specification<T> specification);

    /**
     * 建立一套规范去校验对象{@code this}
     *
     * @param specification 满足其中一条规范即可
     * @return 一个新创建的规范
     */
    Specification<T> or(Specification<T> specification);

    /**
     * 建立一套规范去校验对象{@code this}
     *
     * @param specification 必须不满足才可以
     * @return 一个新创建的规范
     */
    Specification<T> not(Specification<T> specification);

}
