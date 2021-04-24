# 日志相关
### 普通系统日志
1. 类上加注解 `@Slf4j`
1. 写日志
    `LoggerUtils.info(log, "your name is {}", "zhansong")`

1. 配置信息`application.yml`

    ```yaml
    logging:
      level:
        root:  ERROR
        org.springframework:  ERROR
        org.mybatis:  ERROR
        com.lemon:  DEBUG
        yourpackage: level...
    ```
   
### Serilogj
1. 在服务器上安装seq服务，如果需要SSL则需要申请证书。
1. maven pom 依赖

    ```xml
    <dependency>
        <groupId>org.serilogj.serilogj</groupId>
        <artifactId>serilogj</artifactId>
    </dependency>
    ```
   
1. 配置信息`application.yml`

    ```yaml
    zh:
      log:
        #日志服务类型：serilog/可扩展其他
        active: serilog
        serilog:
          #是否输出到控制台（自带颜色根据level）
          enabled-console: true
          #是否写入log文件
          enabled-rolling-file: false
          output-template: "[{Timestamp} {Level}]: {Message} {NewLine}{Exception}"
          #log文件的名字
          rolling-file: "test-{Date}.log"
          # 每个文件上限大小，默认1G
          file-size-limit-bytes: 1073741824
          # 最多保留多少文件，默认31个
          retained-file-count-limit: 31
          #seq服务地址（默认port=5341）
          seq-url: "http://host:port"
    ```

1. 开启异步支持 `@EnableAsync`

1. 输出日志

   ```
   SysUserEntity user = new SysUserEntity();
   user.setUsername("Tony");
   Log.forContext(LogService.class).warning("Hello {name} from {@user}", "World", user);
   ```
   
   ```
   Log.error(ex, "An error occurred");
   ```

[ELK实时日志分析平台的搭建部署及使用](https://www.jianshu.com/p/b0454b6e654f)

[ELK+filebeat+redis日志分析平台](https://www.jianshu.com/p/bf2793e85d18)