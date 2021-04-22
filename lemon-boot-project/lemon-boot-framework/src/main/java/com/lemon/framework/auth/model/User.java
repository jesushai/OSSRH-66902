package com.lemon.framework.auth.model;

/**
 * 名称：系统操作用户<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
@SuppressWarnings("unused")
public interface User {

    /**
     * @return 用户id
     */
    Long getId();

    /**
     * @return 用户所属的租户
     */
    Long getTenant();

    /**
     * @return 登录的用户名
     */
    String getUsername();

    /**
     * @return 密码（密文）
     */
    String getPassword();

    /**
     * @return 系统用户的角色列表json数组
     */
    Long[] getRoleIds();

    /**
     * @return 账号是否可用
     */
    boolean isValid();
}
