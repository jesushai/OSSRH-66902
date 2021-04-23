package com.lemon.framework.auth;

import java.util.Set;

/**
 * 名称：系统角色服务接口<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/8
 */
public interface RoleService {

    Set<String> getNamesByIds(Long[] roleIds);
}
