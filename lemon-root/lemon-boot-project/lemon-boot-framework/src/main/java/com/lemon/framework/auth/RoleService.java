package com.lemon.framework.auth;

import java.util.Set;

/**
 * <b>名称：系统角色服务接口</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public interface RoleService {

    Set<String> getNamesByIds(Long[] roleIds);
}
