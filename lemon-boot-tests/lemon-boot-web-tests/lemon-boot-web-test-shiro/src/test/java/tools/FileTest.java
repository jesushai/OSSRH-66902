package tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

/**
 * 名称：<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2021/5/12
 */
class FileTest {

    @Test
    void test1() throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("templates/entity.java.ftl")) {
            byte[] bytes = new byte[7];
            inputStream.read(bytes, 0, 7);
            Assertions.assertEquals(new String(bytes), "package");
        }

        // jar类库中的资源文件
        Resource resource1 = new DefaultResourceLoader().getResource("templates/entity.java.ftl");
        try (InputStream inputStream = resource1.getInputStream()) {
            byte[] bytes = new byte[7];
            inputStream.read(bytes, 0, 7);
            Assertions.assertEquals(new String(bytes), "package");
        }

        resource1 = new FileSystemResourceLoader().getResource("classpath:templates/entity.java.ftl");
        try (InputStream inputStream = resource1.getInputStream()) {
            byte[] bytes = new byte[7];
            inputStream.read(bytes, 0, 7);
            Assertions.assertEquals(new String(bytes), "package");
        }

        // 获取当前运行路径，如果是jar文件运行则为jar所在路径
        String path;
        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
            JarFile jarFile = ((JarURLConnection) connection).getJarFile();
            path = jarFile.getName();
            System.out.println("jar path = " + path);
            int separator = path.indexOf("!/");
            if (separator > 0) {
                path = path.substring(0, separator);
            }
        } else {
            path = url.getPath();
        }
        System.out.println(path);
    }
}
