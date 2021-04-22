package storage.qiniu;

import com.lemon.boot.autoconfigure.storage.EnableStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 名称：测试七牛云starter<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/30
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
//        LocaleAutoConfiguration.class
})
@EnableStorage
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
