package shiro.db.service.impl;

import com.lemon.framework.auth.TenantService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Service("tenantService")
public class TenantServiceImpl implements TenantService {

    @Override
    public List<Long> getAllTenantId() {
        return new ArrayList<Long>(1) {{
            add(0L);
        }};
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
