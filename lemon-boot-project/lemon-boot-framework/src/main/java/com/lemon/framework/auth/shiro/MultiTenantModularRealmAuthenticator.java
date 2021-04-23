package com.lemon.framework.auth.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.List;

/**
 * 名称：多租户Realm选择认证器<p>
 * 描述：<p>
 * 暂时不需要
 *
 * @author hai-zhang
 * @since 2020-5-10
 */
@Slf4j
@Deprecated
@SuppressWarnings("unchecked")
public class MultiTenantModularRealmAuthenticator extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        assertRealmsConfigured();
        if (!(authenticationToken instanceof TenantUsernamePasswordToken)) {
            throw new AuthenticationException("Unrecognized token , not a typeof TenantUsernamePasswordToken.");
        }
        TenantUsernamePasswordToken userToken = (TenantUsernamePasswordToken) authenticationToken;
        // 登录者的租户
        long tenantId = userToken.getTenantId();

//        List<Realm> tenantRealms = lookupRealms(tenantId);
        List<Realm> tenantRealms = (List) getRealms();

        // 判断是单Realm还是多Realm
        if (tenantRealms.size() == 1) {
            return doSingleRealmAuthentication(tenantRealms.get(0), userToken);
        } else if (tenantRealms.size() <= 0) {
            throw new AuthenticationException("User tenant [" + tenantId + "] is not in the realms.");
        } else {
            return doMultiRealmAuthentication(tenantRealms, userToken);
        }
    }

//    /**
//     * 获取对应租户的所有Realm
//     * 登录者没有提供租户则全部realm可用
//     *
//     * @param tenantId 租户ID
//     */
//    private List<Realm> lookupRealms(long tenantId) {
//        Collection<Realm> realms = getRealms();
//        List<Realm> result = new ArrayList<>();
//        for (Realm realm : realms) {
//            if (realm instanceof TenantUserAuthorizingRealm) {
//                // 租户realm只包含符合的
//                if (((TenantUserAuthorizingRealm) realm).getTenantId() == tenantId) {
//                    result.add(realm);
//                }
//            } else {
//                // 其他非租户的realm包含进来
//                result.add(realm);
//            }
//        }
//        return result;
//    }
}
