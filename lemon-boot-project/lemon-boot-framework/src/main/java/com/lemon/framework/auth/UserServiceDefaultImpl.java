package com.lemon.framework.auth;

import com.lemon.framework.auth.model.User;

import java.util.List;

/**
 * 名称：默认的用户服务<p>
 * 描述：<p>
 * 请覆盖它
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class UserServiceDefaultImpl implements UserService {

    @Override
    public List<User> getUserByIdentificationAndTenant(String username, Long tenantId) {
        throw new RuntimeException("Unimplemented SysUserService service.");
    }

    @Override
    public List<User> getAllUserByIdentification(String identification) {
        throw new RuntimeException("Unimplemented SysUserService service.");
    }
}
