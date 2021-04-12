package storage.qiniu.service;

import com.lemon.framework.storage.StorageInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import storage.qiniu.entity.MockStorageEntity;

import java.util.Random;

@Slf4j
@Component
public class MockStorageInfoServiceImpl implements StorageInfoService<MockStorageEntity> {

    @Override
    public MockStorageEntity save(String fileName, Long contentLength, String contentType, String key, String url) {
        return newStorageEntity(fileName, contentLength, contentType, key, url);
    }

    private MockStorageEntity newStorageEntity(String fileName, Long contentLength, String contentType, String key, String url) {
        MockStorageEntity entity = new MockStorageEntity();
        entity.setId(new Random().nextLong());
        entity.setName(fileName);
        entity.setKey(key);
        entity.setType(contentType);
        entity.setSize(contentLength);
        entity.setUrl(url);
        log.info("模拟保存对象存储信息到数据库中：{}", key);
        return entity;
    }

    private MockStorageEntity newStorageEntity(String key) {
        MockStorageEntity entity = new MockStorageEntity();
        entity.setId(new Random().nextLong());
        entity.setName("fileName");
        entity.setKey(key);
        entity.setType("image/png");
        entity.setSize(102400L);
        entity.setUrl("http://...");
        return entity;
    }

    @Override
    public MockStorageEntity findByKey(String key) {
        log.info("模拟查询对象存储数据从数据库中: {}", key);
        return newStorageEntity(key);
    }

    @Override
    public boolean existsByKey(String key) {
        return false;
    }
}
