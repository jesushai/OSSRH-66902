package com.lemon.framework.auth.model;

/**
 * <b>名称：系统操作用户</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
@SuppressWarnings("unused")
public interface User {

    Long getId();

    /**
     * 用户所属的租户
     */
    Long getTenant();

    /**
     * 登录的用户名
     */
    String getUsername();

    /**
     * 密码（密文）
     */
    String getPassword();

    /**
     * 系统用户的角色列表json数组
     */
    Long[] getRoleIds();

    /**
     * 账号是否可用
     */
    boolean isValid();
}
