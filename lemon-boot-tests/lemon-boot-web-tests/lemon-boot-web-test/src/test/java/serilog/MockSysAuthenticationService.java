package serilog;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.Subject;
import com.lemon.framework.auth.model.support.SubjectSupport;
import com.lemon.framework.constant.BeanNameConstants;
import org.springframework.stereotype.Component;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/5/15
 */
@Component(BeanNameConstants.AUTHENTICATION_SERVICE)
public class MockSysAuthenticationService implements AuthenticationService {

    @Override
    public Subject doGetSubject() {
        SubjectSupport subject = new SubjectSupport();
        MockSysUser user = new MockSysUser();
        user.setTenant(100L);
        user.setUsername("Mock User");
        subject.setPrincipal(user);
        return subject;
    }

}
