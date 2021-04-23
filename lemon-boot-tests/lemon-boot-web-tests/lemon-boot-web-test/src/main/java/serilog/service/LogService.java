package serilog.service;

import org.springframework.stereotype.Service;
import serilog.entity.SysUserEntity;
import serilogj.Log;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020-5-14
 */
@Service
public class LogService {

    //    @Async
    public void log() {
        try {
//            try (AutoCloseable property = LogContext.pushProperty("Operation", 1)) {
//                Log.information("In outer operation");
//                try (AutoCloseable other = LogContext.pushProperty("Operation", 2)) {
//                    Log.information("In inner operation");
//                }
//            }

//            for (int i = 0; i < 10; ++i) {
//                Log.debug("Running iteration {Number}", i);
//                Thread.sleep(2000);
//            }

//            User user = new User();
//            user.setUserName(System.getProperty("user.name"));
//            Log.forContext(JavaConsole.class).warning("Hello {name} from {@user}", "World", user);

            SysUserEntity sysUser = new SysUserEntity();
            sysUser.setUsername("Tony");
            Log.forContext(LogService.class).warning("Hello {name} from {@sysUser}", "World", sysUser);
            throw new Exception("Something went wrong");
        } catch (Exception ex) {
            Log.error(ex, "An error occurred");
        }
    }

}
