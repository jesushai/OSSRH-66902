package serilog;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import serilog.service.LogService;

/**
 * 名称：<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020-5-14
 */
@SpringBootTest
@Slf4j
public class TestSerilog {

    @Autowired
    private LogService logService;

    @Test
    public void testSerilogTemplate() {
        logService.log();
    }
}
