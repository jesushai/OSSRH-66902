package storage.qiniu;

import com.lemon.framework.storage.StorageService;
import com.lemon.framework.storage.model.StorageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import storage.qiniu.entity.MockStorageEntity;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
@Slf4j
public class TestQiniuStorage {

    @Autowired
    private StorageService<MockStorageEntity> storageService;

    @Test
    public void testQiniuUploadFile() throws Exception {
        String path = System.getProperty("user.dir");
        String filepath = path + "/src/test/java/resources-i18n.png";
        File f = new File(filepath);
        StorageInfo storageInfo;
        try (FileInputStream fis = new FileInputStream(f)) {
            storageInfo = storageService.store(
                    fis, f.length(), "image/png", "resources-i18n.png");
            Assertions.assertEquals((Long) f.length(), storageInfo.getSize());
            System.out.println("生成的key=" + storageInfo.getKey());
        }
        storageService.delete(storageInfo.getKey());
    }
}
