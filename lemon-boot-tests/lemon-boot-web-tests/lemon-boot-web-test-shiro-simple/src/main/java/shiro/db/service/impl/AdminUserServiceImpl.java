package shiro.db.service.impl;

import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;

import java.util.List;

/**
 * 名称：后台管理系统的用户服务实现<br/>
 * 描述：<br/>
 * 用于授权与认证
 *
 * @author hai-zhang
 * @since 2020/6/12
 */
//@Service(BeanNameConstants.USER_SERVICE)
public class AdminUserServiceImpl implements UserService {

    @Override
    public List<User> getUserByIdentificationAndTenant(String identification, Long tenantId) {
        return SysAdminServiceImpl.getUsers();
    }

    @Override
    public List<User> getAllUserByIdentification(String identification) {
        return SysAdminServiceImpl.getUsers();
    }

}
