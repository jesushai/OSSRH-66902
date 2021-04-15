package shiro.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.framework.auth.RoleService;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysRole;
import shiro.db.mapper.SysRoleMapper;
import shiro.db.service.ISysRoleService;

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
@CacheConfig(cacheNames = "cache2")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService, RoleService {

    @Override
    @Cacheable(keyGenerator = BeanNameConstants.COMMON_KEY_GENERATOR)
    public Set<String> getNamesByIds(Long[] roleIds) {
        System.out.println("==================================没走缓存getNamesByIds！");
        Set<String> result = new HashSet<>();
        getRolesByIds(roleIds).stream().map(SysRole::getName).forEach(result::add);
        return result;
    }

    public List<SysRole> getRolesByIds(Long[] roleIds) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, (Object[]) roleIds));
    }
}
