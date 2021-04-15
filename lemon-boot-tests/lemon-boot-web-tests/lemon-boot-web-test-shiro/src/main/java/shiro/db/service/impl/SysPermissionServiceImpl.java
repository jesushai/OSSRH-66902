package shiro.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.framework.auth.PermissionService;
import com.lemon.framework.auth.model.Permission;
import com.lemon.framework.auth.model.PermissionTreeNode;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysPermission;
import shiro.db.mapper.SysPermissionMapper;
import shiro.db.service.ISysPermissionService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Service("permissionService")
@CacheConfig(cacheNames = "cache1")
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService, PermissionService {

    @Override
    @Cacheable(keyGenerator = BeanNameConstants.COMMON_KEY_GENERATOR)
    public Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
        System.out.println("==================================没走缓存getPermissionsByRoleIds！");
        Set<String> result = new HashSet<>();
        findPermissionsByRoleIds(roleIds, tenantId).stream().map(SysPermission::getPermission).forEach(result::add);
        return result;
    }

    @Override
    @Cacheable(key = "'getPermissionsByRoleIds:'+#p0")
    public Set<Permission> getAllPermission(Long tenantId) {
        System.out.println("==================================没走缓存getAllPermission！");
        return new HashSet<>(baseMapper.selectPermissionsByTenant(tenantId));
    }

    @Override
    @Cacheable(key = "'getPermissionsByCache:'+#p0+'_'+#p1")
    public List<PermissionTreeNode> getPermissionTree(String basicPackage, Long tenantId) {
        System.out.println("==================================没走缓存getPermissionsByCache！");
        return PermissionService.super.getPermissionTree(basicPackage, tenantId);
//        HashOperations
    }

    public List<SysPermission> findPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
        return baseMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getTenant, tenantId)
                .in(SysPermission::getRoleId, (Object[]) roleIds));
    }
}
