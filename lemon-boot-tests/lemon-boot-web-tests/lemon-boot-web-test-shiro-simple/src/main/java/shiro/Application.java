package shiro;

import com.lemon.boot.autoconfigure.commons.EnableAsyncExecutor;
import com.lemon.boot.autoconfigure.commons.EnableLocaleMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLocaleMessage
@EnableAsyncExecutor
//@EnableEncryptableProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
