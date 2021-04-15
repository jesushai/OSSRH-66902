package redisson;

import com.lemon.framework.util.crypto.password.PasswordEncoderUtil;
import org.springframework.boot.SpringApplication;

//@SpringBootApplication
//@EnableLocaleMessage
//@EnableTransactionManagement
//@EnableAsyncExecutor
public class Application {

    public static void main(String[] args) {
        System.out.println(PasswordEncoderUtil.encryptPassword("1234"));
//        SpringApplication.run(Application.class, args);
    }

}
