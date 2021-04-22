package com.lemon.framework.auth;

import java.util.Collections;
import java.util.List;

/**
 * 名称：默认租户0<br/>
 * 描述：<br/>
 * 多租户系统请覆盖它
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
