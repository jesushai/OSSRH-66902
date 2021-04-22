package com.lemon.framework.auth.model;

/**
 * 名称：Subject标示接口<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
public interface Subject {

    /**
     * @return Session
     */
    Session getSession();

    /**
     * @return Principal
     */
    User getPrincipal();
}
