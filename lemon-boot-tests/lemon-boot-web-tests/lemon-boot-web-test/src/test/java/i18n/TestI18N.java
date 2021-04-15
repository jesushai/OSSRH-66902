package i18n;

import com.lemon.framework.exception.BusinessException;
import com.lemon.framework.exception.ExceptionBuilder;
import com.lemon.framework.exception.SystemException;
import com.lemon.framework.handler.MessageSourceHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@SpringBootTest
@Slf4j
public class TestI18N {

    @Test
    public void testMessage() {
        String msg = MessageSourceHandler.getMessage("TEST-001", "filename", "1234");
        Assertions.assertEquals(msg, "这是一条消息: filename, 1234。");
        msg = MessageSourceHandler.getMessage("TEST-001", "filename", "1234");
        Assertions.assertEquals(msg, "This is a message: filename, 1234.");
    }

    @Test
    public void testExceptionMessage() {
        try {
            new ExceptionBuilder<>().code("TEST-001").args("error", "...").throwIt();
        } catch (BusinessException e) {
            Assertions.assertEquals(e.getMessage(), "This is a message: error, ....");
        }
        try {
            new ExceptionBuilder<>(SystemException.class).code("oip").throwIt();
        } catch (SystemException e) {
            Assertions.assertEquals(e.getMessage(), "oip");
        }
    }

}
