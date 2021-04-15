package shiro.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shiro.db.entity.Admin;
import shiro.db.mapper.AdminMapper;

import java.util.List;

/**
 * <b>名称：后台管理系统的用户服务实现</b><br/>
 * <b>描述：</b><br/>
 * 用于授权与认证
 *
 * @author hai-zhang
 * @since 2020/6/12
 */
@Service(BeanNameConstants.USER_SERVICE)
@SuppressWarnings({"unchecked", "rawtypes"})
public class AdminUserServiceImpl implements UserService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<User> getUserByIdentificationAndTenant(String identification, Long tenantId) {
        return (List) adminMapper.selectList(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, identification)
                        .eq(Admin::getTenant, tenantId)
        );
    }

    @Override
    public List<User> getAllUserByIdentification(String identification) {
        return (List) adminMapper.selectList(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, identification)
        );
    }

}
