package com.lemon.framework.domain.event.support;

import com.lemon.framework.domain.event.DomainEvent;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * <b>名称：领域事件接口</b><br/>
 * <b>描述：</b><br/>
 * <pre>
 * 领域事件没有自己的生命周期，但是每个领域事件都是唯一的，它有可能有一个很明显的主键标识，
 * 比如某一次支付事件的支付单据号，或者仅仅只能由何时、何地、发生了什么事件推导出自己的唯一标识。
 *
 * 提供了最基础的实体比较方法<code>hashCode</code>与<code>equals</code>
 * 因为Event也是不可变的对象，因此可以使用所有子类属性实现equals方法。
 * </pre>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@Data
public abstract class DomainEventSupport<T> implements DomainEvent<T>, Cloneable, Serializable {

    private static final long serialVersionUID = -3370971441746309342L;

    public DomainEventSupport(T eventSource, String eventState) {
        this.id = UUID.randomUUID().toString();
        this.eventSource = eventSource;
        this.eventState = eventState;
        setCreateTime(new Date());
    }

    protected String id;

    protected Date createTime;

    private transient int cachedHashCode = 0;

    private T eventSource;

    protected String eventState;

    /**
     * @param other The other value object.
     * @return True if all non-transient fields are equal.
     */
    public boolean sameEventAs(T other) {
        return other != null && EqualsBuilder.reflectionEquals(this, other, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return sameEventAs((T) o);
    }

    /**
     * @return Hash code built from all non-transient fields.
     */
    @Override
    public int hashCode() {
        // Using a local variable to ensure that we only do a single read
        // of the cachedHashCode field, to avoid race conditions.
        // It doesn't matter if several threads compute the hash code and
        // overwrite
        // each other, but it's important that we never return 0, which could
        // happen
        // with multiple reads of the cachedHashCode field.
        //
        // See java.lang.String.hashCode()
        if (cachedHashCode == 0) {
            // Lazy initialization of hash code.
            // Value objects are immutable, so the hash code never changes.
            cachedHashCode = HashCodeBuilder.reflectionHashCode(this, false);
        }

        return cachedHashCode;
    }

    /**
     * 发布事件到事件链<br/>
     * <b>要求必须克隆后才行，否则会导致事件重复发布</b>
     *
     * @param newState 新状态，即新事件
     * @return 克隆后的新状态事件
     */
    public DomainEvent publishChain(String newState) {
        try {
            DomainEvent event = (DomainEvent) this.clone();
            event.setEventState(newState);
            return event;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("无法复制值对象: " + e.getMessage());
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
