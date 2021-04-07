package com.lemon.framework.auth;

import java.util.Collections;
import java.util.List;

/**
 * <b>名称：默认租户0</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
public class TenantServiceDefaultImpl implements TenantService {

    @Override
    public List<Long> getAllTenantId() {
        return Collections.singletonList(0L);
    }

    @Override
    public String[] getModules(Long tenantId) {
        return new String[]{};
    }

    @Override
    public String getTenantName(Long id) {
        return "";
    }
}
