# 配置文件加密

* pom.xml添加依赖
```
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
</dependency>
```

* 运行org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI
参数：
```
input="你要加密的串" password="盐" algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator
```

* 将上一步加密的结果写入application.yml中
```
username: "ENC(加密后的结果)"
password: "ENC(加密后的结果)"
```

* application.yml中增加配置
```
# yml配置文件加密的盐
jasypt:
  encryptor:
    iv-generator-classname: org.jasypt.iv.RandomIvGenerator
```

* 主Application加入
@EnableEncryptableProperties
```java
@SpringBootApplication
@EnableEncryptableProperties
public class YourApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourApplication.class, args);
    }
}
```
