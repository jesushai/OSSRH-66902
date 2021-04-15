package shiro.db.service.impl;

import com.lemon.framework.auth.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shiro.db.mapper.SysAdminMapper;

import java.util.List;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Service("tenantService")
public class TenantServiceImpl implements TenantService {

    @Autowired
    private SysAdminMapper sysAdminMapper;

    @Override
    public List<Long> getAllTenantId() {
        return sysAdminMapper.getAllTenant();
    }

    @Override
    public String[] getModules(Long tenantId) {
        return new String[]{};
    }

    @Override
    public String getTenantName(Long id) {
        return "测试租户";
    }
}
