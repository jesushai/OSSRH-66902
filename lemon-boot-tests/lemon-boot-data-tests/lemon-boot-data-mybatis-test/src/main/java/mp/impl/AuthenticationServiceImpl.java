package mp.impl;

import com.lemon.framework.auth.AuthenticationService;
import com.lemon.framework.auth.model.Subject;
import org.springframework.stereotype.Component;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/6/22
 */
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public Subject doGetSubject() {
        return null;
    }
}
