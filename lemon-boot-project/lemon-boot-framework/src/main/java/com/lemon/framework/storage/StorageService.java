package com.lemon.framework.storage;

import com.lemon.framework.storage.model.StorageInfo;
import com.lemon.framework.util.CharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 名称：提供存储服务类，所有存储服务均由该类对外提供<br/>
 * 描述：<br/>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Slf4j
@SuppressWarnings("unused")
public class StorageService<T extends StorageInfo> {

    /**
     * local/aliyun/tencent/qiniu
     */
//    @Value("${zh.storage.active}")
//    private String active;

    @Autowired(required = false)
    @Qualifier("storageProvider")
    private StorageProvider storageProvider;

    @Autowired(required = false)
    private StorageInfoService<T> storageInfoService;

    private void checkStorageInfoService() {
        if (storageInfoService == null) {
            throw new RuntimeException("The OSS (StorageInfoService) is not registered!");
        }
    }

    private void checkStorageProvider() {
        if (storageProvider == null) {
            throw new RuntimeException("The OSS (StorageProvider) is not registered!");
        }
    }

    /**
     * 存储一个文件对象
     *
     * @param inputStream   文件输入流
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param filename      文件索引名
     * @return 存储对象
     */
    public T store(InputStream inputStream, long contentLength, String contentType, String filename) {

        checkStorageInfoService();
        checkStorageProvider();

        String key = generateKey(filename);
        storageProvider.store(inputStream, contentLength, contentType, key);

        String url = generateUrl(key);
        return storageInfoService.save(filename, contentLength, contentType, key, url);
    }

    private String generateKey(String originalFilename) {
        // 扩展名
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key;
        boolean exists;

        // 生成的key为20位随机数，并反复检查是否重复
        // TODO: key的计算应该改变一下
        do {
            key = CharUtils.getRandomString(20) + suffix;
            exists = storageInfoService.existsByKey(key);
        }
        while (exists);

        return key;
    }

    public Stream<Path> loadAll() {
        checkStorageProvider();
        return storageProvider.loadAll();
    }

    public Path load(String keyName) {
        checkStorageProvider();
        return storageProvider.load(keyName);
    }

    public Resource loadAsResource(String keyName) {
        checkStorageProvider();
        return storageProvider.loadAsResource(keyName);
    }

    public void delete(String keyName) {
        checkStorageProvider();
        storageProvider.delete(keyName);
    }

    private String generateUrl(String keyName) {
        checkStorageProvider();
        return storageProvider.generateUrl(keyName);
    }
}
