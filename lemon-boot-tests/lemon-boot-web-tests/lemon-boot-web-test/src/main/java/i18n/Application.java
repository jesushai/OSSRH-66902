package i18n;

import com.lemon.boot.autoconfigure.commons.EnableLocaleMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
@EnableLocaleMessage
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
