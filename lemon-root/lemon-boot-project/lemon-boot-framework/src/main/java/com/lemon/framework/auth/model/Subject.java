package com.lemon.framework.auth.model;

/**
 * <b>名称：Subject标示接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
public interface Subject {

    Session getSession();

    User getPrincipal();
}
