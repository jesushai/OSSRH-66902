package shiro.db.mapper;

import shiro.db.entity.SysAdmin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 管理员表 Mapper 接口
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
public interface SysAdminMapper extends BaseMapper<SysAdmin> {

    List<Long> getAllTenant();
}
