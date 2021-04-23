package com.lemon.framework.auth.model;

/**
 * 名称：Session标示接口<p>
 * 描述：<p>
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
