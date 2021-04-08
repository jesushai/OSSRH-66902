package com.lemon.framework.auth;

import com.lemon.framework.auth.model.User;

import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
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
