package shiro.db.service.impl;

import com.lemon.framework.auth.RoleService;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysRole;
import shiro.db.service.ISysRoleService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Service("roleService")
public class SysRoleServiceImpl implements ISysRoleService, RoleService {

    @Override
    public Set<String> getNamesByIds(Long[] roleIds) {
        return new HashSet<String>(1) {{
           add("admin");
        }};
    }

    public List<SysRole> getRolesByIds(Long[] roleIds) {
        return new ArrayList<SysRole>(1) {{
           add(new SysRole().setId(1L).setName("admin").setActive(true).setTenant(0L).setDeleted(false));
        }};
    }
}
