package async;

import com.lemon.boot.autoconfigure.commons.EnableAsyncExecutor;
import com.lemon.boot.autoconfigure.commons.EnableLocaleMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@EnableLocaleMessage
// 开启异步执行器插件
@EnableAsyncExecutor
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
