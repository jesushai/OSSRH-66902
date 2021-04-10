package com.lemon.boot.autoconfigure.storage;

import com.lemon.boot.autoconfigure.storage.properties.StorageProperties;
import com.lemon.framework.storage.*;
import com.lemon.framework.util.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class StorageAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(StorageProperties.class)
    public static class StorageProviderAutoConfiguration {

        private final StorageProperties properties;

        public StorageProviderAutoConfiguration(StorageProperties properties) {
            this.properties = properties;
        }

        @Bean("storageProvider")
        @ConditionalOnMissingBean
        @ConditionalOnMissingClass({
                "com.aliyun.oss.OSS",
                "com.qcloud.cos.COSClient",
                "com.qiniu.storage.Configuration"
        })
        @ConditionalOnProperty(prefix = "zh.storage", name = "active", havingValue = "local")
        public StorageProvider localStorage() {
            LocalStorageProvider localStorage = new LocalStorageProvider();
            StorageProperties.Local local = properties.getLocal();
            localStorage.setAddress(local.getAddress());
            localStorage.setStoragePath(local.getStoragePath());
            LoggerUtils.debug(log, "LocalStorageProvider in applicationContext");
            return localStorage;
        }

        @Bean("storageProvider")
        @ConditionalOnMissingBean
        @ConditionalOnClass(name = "com.aliyun.oss.OSS")
        @ConditionalOnProperty(prefix = "zh.storage", name = "active", havingValue = "aliyun")
        public StorageProvider aliyunStorage() {
            AliyunStorageProvider aliyunStorage = new AliyunStorageProvider();
            StorageProperties.Aliyun aliyun = properties.getAliyun();
            aliyunStorage.setAccessKeyId(aliyun.getAccessKeyId());
            aliyunStorage.setAccessKeySecret(aliyun.getAccessKeySecret());
            aliyunStorage.setBucketName(aliyun.getBucketName());
            aliyunStorage.setEndpoint(aliyun.getEndpoint());
            LoggerUtils.debug(log, "AliyunStorageProvider in applicationContext");
            return aliyunStorage;
        }

        @Bean("storageProvider")
        @ConditionalOnMissingBean
        @ConditionalOnClass(name = "com.qcloud.cos.COSClient")
        @ConditionalOnProperty(prefix = "zh.storage", name = "active", havingValue = "tencent")
        public StorageProvider tencentStorage() {
            TencentStorageProvider tencentStorage = new TencentStorageProvider();
            StorageProperties.Tencent tencent = properties.getTencent();
            tencentStorage.setSecretId(tencent.getSecretId());
            tencentStorage.setSecretKey(tencent.getSecretKey());
            tencentStorage.setBucketName(tencent.getBucketName());
            tencentStorage.setRegion(tencent.getRegion());
            LoggerUtils.debug(log, "TencentStorageProvider in applicationContext");
            return tencentStorage;
        }

        @Bean("storageProvider")
        @ConditionalOnMissingBean
        @ConditionalOnClass(name = "com.qiniu.storage.Configuration")
        @ConditionalOnProperty(prefix = "zh.storage", name = "active", havingValue = "qiniu")
        public StorageProvider qiniuStorage() {
            QiniuStorageProvider qiniuStorage = new QiniuStorageProvider();
            StorageProperties.Qiniu qiniu = properties.getQiniu();
            qiniuStorage.setAccessKey(qiniu.getAccessKey());
            qiniuStorage.setSecretKey(qiniu.getSecretKey());
            qiniuStorage.setBucketName(qiniu.getBucketName());
            qiniuStorage.setEndpoint(qiniu.getEndpoint());
            LoggerUtils.debug(log, "QiniuStorageProvider in applicationContext");
            return qiniuStorage;
        }
    }

    @Configuration
    @AutoConfigureAfter({StorageProviderAutoConfiguration.class})
    @Import(StorageService.class)
    public static class StorageServiceAutoConfiguration {
    }

}
