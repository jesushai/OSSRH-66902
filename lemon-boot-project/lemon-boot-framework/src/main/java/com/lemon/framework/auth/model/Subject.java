package com.lemon.framework.auth.model;

/**
 * 名称：Subject标示接口<p>
 * 描述：<p>
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
