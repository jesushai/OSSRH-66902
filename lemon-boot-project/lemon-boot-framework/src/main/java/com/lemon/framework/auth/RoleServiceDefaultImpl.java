package com.lemon.framework.auth;

import java.util.Set;

/**
 * 名称：默认的角色服务<br/>
 * 描述：<br/>
 * 请覆盖它
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
