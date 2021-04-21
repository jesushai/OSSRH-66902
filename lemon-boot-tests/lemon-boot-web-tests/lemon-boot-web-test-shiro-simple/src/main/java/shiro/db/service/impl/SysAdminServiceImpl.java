package shiro.db.service.impl;

import com.lemon.framework.auth.UserService;
import com.lemon.framework.auth.model.User;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.stereotype.Service;
import shiro.db.entity.SysAdmin;
import shiro.db.service.ISysAdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author hai-zhang
 * @since 2020-05-11
 */
@Service(BeanNameConstants.USER_SERVICE)
public class SysAdminServiceImpl implements ISysAdminService, UserService {

    @Override
    public List<User> getUserByIdentificationAndTenant(String identification, Long tenantId) {
        return SysAdminServiceImpl.getUsers().stream()
                .filter(x -> x.getTenant().equals(tenantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUserByIdentification(String identification) {
        return SysAdminServiceImpl.getUsers();
    }

    static List<User> getUsers() {
        return new ArrayList<User>(1) {{
            add(new SysAdmin()
                    .setId(1L)
                    .setUsername("admin")
                    .setTenant(0L)
                    .setPassword("$2a$10$Ns2OoXoRyyMwY0ykxWy6iOQ/dkdPzS0lZd7MBAadbgyfTHk56.df6")
                    .setRoleIds(new Long[]{1L})
                    .setActive(true)
                    .setDeleted(false));
        }};
    }
}
