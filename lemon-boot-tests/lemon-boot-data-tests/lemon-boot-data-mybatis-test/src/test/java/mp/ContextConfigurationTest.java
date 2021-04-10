package mp;

import com.lemon.framework.util.spring.SpringContextUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020-5-3
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ContextConfigurationTest {

    @Test
    public void testSpringContextUtil() {
        Assert.assertNotNull(SpringContextUtils.getApplicationContext());
    }
}
