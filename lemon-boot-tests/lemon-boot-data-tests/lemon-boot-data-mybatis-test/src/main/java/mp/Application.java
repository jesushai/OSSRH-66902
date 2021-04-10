package mp;

import com.lemon.boot.autoconfigure.data.mp.EnableDynamicDatasource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <b>名称：</b><br/>
 * <b>描述：</b><br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@SpringBootApplication
@EnableDynamicDatasource
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}