# 通知服务
## 短信服务
#### 支持类型
* 腾讯云
* 阿里云

#### maven pom
阿里云:
```xml
    <dependency>
        <groupId>com.aliyun</groupId>
        <artifactId>aliyun-java-sdk-core</artifactId>
    </dependency>
```
腾讯云:
```xml
    <dependency>
        <groupId>com.github.qcloudsms</groupId>
        <artifactId>qcloudsms</artifactId>
    </dependency>
```

#### 配置文件
`application.yml`
```yaml
zh:
  notify:
    # 短消息模版通知配置
    # 短信息用于通知客户，例如发货短信通知，注意配置格式；template-name，template-templateId
    sms:
      enabled: true
      # tencent / aliyun
      active: aliyun
      sign: lemon
      template:
      - name: paySucceed
        templateId: 156349
      - name: captcha
        templateId: 156433
      - name: ship
        templateId: 158002
      - name: refund
        templateId: 159447
      tencent:
        appid: 111111111
        app-key: xxxxxxxxxxxxxx
      aliyun:
        region-id: xxx
        access-key-id: xxx
        access-key-secret: xxx
```
>每类通知预定义好模板，并配入`template`中

#### 服务类
[NotifySmsService](../../lemon-boot-project/lemon-boot-framework/src/main/java/com/lemon/framework/notify/NotifySmsService.java)
```
@Autowired
private NotifySmsService smsService;
```

## 邮件服务
#### maven pom
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
```

#### 配置文件
`application.yml`
```yaml
zh:
  #通知相关配置
  notify:
    mail:
      # 邮件通知配置,邮箱一般用于接收业务通知例如收到新的订单，sendto 定义邮件接收者，通常为商城运营人员
      enabled: true
      host: smtp.exmail.qq.com
      username: ex@ex.com.cn
      password: XXXXXXXXXXXXX
      send-from: ex@ex.com.cn
      send-to: ex@qq.com
      port: 465
```

#### 服务类
[NotifyMailService](../../lemon-boot-project/lemon-boot-framework/src/main/java/com/lemon/framework/notify/NotifyMailService.java)
```
@Autowired
private NotifyMailService mailService;
```
