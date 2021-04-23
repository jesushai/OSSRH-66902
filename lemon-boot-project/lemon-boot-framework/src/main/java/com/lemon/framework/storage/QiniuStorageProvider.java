package com.lemon.framework.storage;

import com.qiniu.cdn.CdnManager;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * 名称：七牛云对象存储服务<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Slf4j
@Data
public class QiniuStorageProvider implements StorageProvider {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;

    /**
     * 七牛云OSS对象存储简单上传实现
     *
     * @param inputStream   流
     * @param contentLength 长度
     * @param contentType   类型
     * @param keyName       关键字
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {

        if (uploadManager == null) {
            if (auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            uploadManager = new UploadManager(new Configuration());
        }

        try {
            String upToken = auth.uploadToken(bucketName);
            uploadManager.put(inputStream, keyName, upToken, null, contentType);
        } catch (QiniuException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String keyName) {
        return null;
    }

    @Override
    public Resource loadAsResource(String keyName) {
        try {
            URL url = new URL(generateUrl(keyName));
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void delete(String keyName) {
        if (bucketManager == null) {
            if (auth == null) {
                auth = Auth.create(accessKey, secretKey);
            }
            bucketManager = new BucketManager(auth, new Configuration(Region.region0()));
        }

        try {
            bucketManager.delete(bucketName, keyName);
            // 删除后强制刷新
            refresh(keyName);
        } catch (QiniuException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void refresh(String... keyNames) {
        CdnManager cdnManager = new CdnManager(auth);
        String[] urls = new String[keyNames.length];
        for (int i = 0; i < keyNames.length; i++) {
            urls[i] = generateUrl(keyNames[i]);
        }
        try {
            //单次方法调用刷新的链接不可以超过100个
            cdnManager.refreshUrls(urls);
            // 成功code=200
//            System.out.println(result.code);
            //获取其他的回复内容
        } catch (QiniuException e) {
            System.err.println(e.response.toString());
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return endpoint + "/" + keyName;
    }
}
