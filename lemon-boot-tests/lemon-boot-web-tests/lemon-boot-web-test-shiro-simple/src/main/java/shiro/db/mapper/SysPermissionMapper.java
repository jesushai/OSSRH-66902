package shiro.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lemon.framework.auth.model.Permission;
import shiro.db.entity.SysPermission;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    List<String> selectPermissionIdsByTenant(Long tenantId);

    List<Permission> selectPermissionsByTenant(Long tenantId);

}
