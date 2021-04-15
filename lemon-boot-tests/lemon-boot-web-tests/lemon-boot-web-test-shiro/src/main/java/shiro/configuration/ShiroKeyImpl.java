package shiro.configuration;

import com.lemon.framework.auth.shiro.ShiroKey;
import org.springframework.stereotype.Component;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/5/11
 */
@Component("shiroKey")
public class ShiroKeyImpl implements ShiroKey {

    @Override
    public String rememberMeEncryptKey() {
        return "test_shiro_key";
    }

    @Override
    public String loginTokenKey() {
        return "X-Test-Token";
    }

    @Override
    public String referencedSessionIdSource() {
        return "Stateless request";
    }
}
