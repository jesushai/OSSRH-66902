# 对象存储
#### 支持类型
* 本地存储
* 阿里云OSS
* 七牛云
* 腾讯云COS

#### 管理存储对象信息
> 在实际应用中存储对象上传到OSS上，同时还需要将其信息保存在数据库中
* 第一步：pom starter

  在`pom.xml`中加入如下依赖
  
  ```
    <!-- 七牛云starter -->
    <dependency>
        <groupId>io.github.jesushai</groupId>
        <artifactId>lemon-boot-starter-storage-qiniu</artifactId>
    </dependency>
  ```
  
* 第二步：实现存储对象，即数据库表对应的实体 [StorageInfo](../../lemon-boot-project/lemon-boot/src/main/java/com/lemon/framework/storage/model/StorageInfo.java)接口
* 第三步：实现[StorageInfoService](../../lemon-boot-project/lemon-boot/src/main/java/com/lemon/framework/storage/StorageInfoService.java)接口的服务实现类
* 第四步：注册[StorageAutoConfiguration](../../lemon-boot-project/lemon-boot-autoconfiguration/src/main/java/com/lemon/boot/autoconfigure/storage/StorageAutoConfiguration.java)<br/>
  只需要如下代码所示，打上注解即可`@EnableStorage`
  
  ```
  @SpringBootApplication
  @EnableStorage
  public class Application {
      public static void main(String[] args) {
          SpringApplication.run(Application.class, args);
      }
  }
  ```
  
* 第五步：配置OSS相关账号信息 [application-storageProvider.yml](../../lemon-boot-project/lemon-boot/src/main/resources/application-storage.yml)

### 示例
#### 七牛云测试工程：[lemon-boot-storage-test-qiniu](../../lemon-boot-tests/lemon-boot-storage-tests/lemon-boot-storage-test-qiniu)
1. 实现存储对象实体 [(MockStorageEntity.java)](../../lemon-boot-tests/lemon-boot-storage-tests/lemon-boot-storage-test-qiniu/src/main/java/storage/qiniu/entity/MockStorageEntity.java)
1. 实现管理实体的Service [（MockStorageInfoServiceImpl.java）](../../lemon-boot-tests/lemon-boot-storage-tests/lemon-boot-storage-test-qiniu/src/main/java/storage/qiniu/service/MockStorageInfoServiceImpl.java)
1. 调用示例 [（TestQiniuStorage.java）](../../lemon-boot-tests/lemon-boot-storage-tests/lemon-boot-storage-test-qiniu/src/test/java/storage/qiniu/TestQiniuStorage.java)

> 其他示例待完成，基本类似

### 常见问题
1. 七牛云文件删除

  因为CDN缓存的原因，七牛云的文件删除后还能继续访问！<br/>
  所以我在删除文件后自动调用了刷新缓存的SDK接口。<br/>

