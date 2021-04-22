package com.lemon.framework.domain.event;

/**
 * 名称：领域事件接口<br/>
 * 描述：<br/>
 * 域事件是唯一的，但没有生命周期。
 * 身份可能是明确的，例如付款的序号，或者它可以从事件的各个方面衍生出来，例如在何处，何时以及在何处已经发生了。
 * <p>
 * <p/>在聚合根实体类中请使用@DomainEvents注解
 * <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.custom-implementations></a>
 *
 * @author hai-zhang
 * @since 2019/9/9
 */
@SuppressWarnings("unused")
public interface DomainEvent<T> {

    String CREATE = "create";
    String MODIFY = "modify";
    String DELETE = "delete";

    /**
     * @param other The other domain event.
     * @return <code>true</code> if the given domain event and this event are regarded as being the same event.
     */
    boolean sameEventAs(T other);

    /**
     * @return 事件的源对象
     */
    T getEventSource();

    /**
     * 在@EventListener的监听方法中返回事件的新状态，可以再次被Spring发布，并被其他带有condition条件的监听方法捕获
     *
     * @return 事件的状态<br />
     */
    String getEventState();

    void setEventState(String eventState);

//    void doStuff(Function<Object, T> function);
//
//    Function<Object, T> stuff();
}
