package com.lemon.framework.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
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
 * 名称：阿里云对象存储服务<p>
 * 描述：<p>
 *
 * @author hai-zhang
 * @since 2020/4/28
 */
@Slf4j
@Data
public class AliyunStorageProvider implements StorageProvider {

    private String endpoint;
    // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    private OSS getOSSClient() {
        return new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);
    }

    private String getBaseUrl() {
        return "https://" + bucketName + "." + endpoint + "/";
    }

    /**
     * 阿里云OSS对象存储简单上传实现
     *
     * @param inputStream   流
     * @param contentLength 长度
     * @param contentType   类型
     * @param keyName       关键字
     */
    @Override
    public void store(InputStream inputStream, long contentLength, String contentType, String keyName) {

        try {
            // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
            // 可选上传时的存储类型与访问权限
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(contentLength);
            objectMetadata.setContentType(contentType);
// objectMetadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
// objectMetadata.setObjectAcl(CannedAccessControlList.Private);
            // 对象键（Key）是对象在存储桶中的唯一标识。
            // <keyName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, keyName, inputStream, objectMetadata);
            OSS ossClient = getOSSClient();

            try {
                ossClient.putObject(putObjectRequest);
            } finally {
                ossClient.shutdown();
            }
        } catch (Exception ex) {
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
            URL url = new URL(getBaseUrl() + keyName);
            Resource resource = new UrlResource(url);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void delete(String keyName) {
        OSS ossClient = getOSSClient();
        try {
            ossClient.deleteObject(bucketName, keyName);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public String generateUrl(String keyName) {
        return getBaseUrl() + keyName;
    }
}
