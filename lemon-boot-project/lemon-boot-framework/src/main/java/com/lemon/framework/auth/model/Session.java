package com.lemon.framework.auth.model;

/**
 * 名称：Session标示接口<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/6/18
 */
public interface Session {

    /**
     * @return Session id
     */
    String getId();

    /**
     * @return Session host
     */
    String getHost();
}
