package com.lemon.framework.auth;

import java.util.Set;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class RoleServiceDefaultImpl implements RoleService {

    @Override
    public Set<String> getNamesByIds(Long[] roleIds) {
        throw new RuntimeException("Unimplemented SysRoleService service.");
    }
}
