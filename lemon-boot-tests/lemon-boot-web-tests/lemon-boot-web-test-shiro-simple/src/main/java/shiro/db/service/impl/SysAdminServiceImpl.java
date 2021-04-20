package shiro.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysAdmin;
import shiro.db.mapper.SysAdminMapper;
import shiro.db.service.ISysAdminService;

import java.util.List;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Service(BeanNameConstants.USER_SERVICE)
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements ISysAdminService, UserService {

    @Autowired
    private SysAdminMapper sysAdminMapper;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<User> getUserByIdentificationAndTenant(String identification, Long tenantId) {
        return (List) sysAdminMapper.selectList(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, identification)
                        .eq(SysAdmin::getTenant, tenantId)
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public List<User> getAllUserByIdentification(String identification) {
        return (List) sysAdminMapper.selectList(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, identification)
        );
    }
}
