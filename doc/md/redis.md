# Redis

## Redis Cache
#### 配置`application.yml`

1. 默认cache配置

    ```yaml
    spring:
      cache:
        type: redis
        redis:
          use-key-prefix: true
          cache-null-values: true
          key-prefix: lemon
    ```
    
1. 特定的cacheName配置

    ```yaml
    zh:
      cache:
        names:
          cache1:
            time-to-live: 40s
            max-idle-time: 30s
            use-key-prefix: true
            cache-null-values: true
            key-prefix: "lemon"
          cache2:
            time-to-live: 60s
            max-idle-time: 30s
            use-key-prefix: true
            cache-null-values: true
            key-prefix: "lemon"
    ```
    
    * names下面就是每个缓存的名字，以及每个缓存不同的策略
    * 可以实现不同场景对缓存的时效性要求
    * 前缀建议统一，默认的key规则是：`keyPrefix:cacheName:key`

#### 使用示例

    ```java
    @Service("yourService")
    @CacheConfig(cacheNames = "cache1")
    public class YourServiceImpl implements YourService {
    
        @Override
        @Cacheable(keyGenerator = BeanNameConstants.COMMON_KEY_GENERATOR)
        public Set<String> getOne(Long[] arg1, Long arg2) {
            return "One";
        }
    
        @Override
        @Cacheable(key = "'hello-'+#p0")
        public Set<String> getTwo(Long arg1) {
            return "Two";
        }
    
        //...
    }
    ```

* 可以在类上或方法上标注所属的cacheName，如果未配置在`application.yml`中则使用默认cache。
* keyGenerator的用法不细说，这里默认提供一个，不满足请自行扩展。
* 其他`@Cache`注解相关用法请百度。

## Redis操作
#### 直接使用`RedisService`

直接注入即可使用

   ```
    @Resource
    private RedisService redisService;
   ```

#### 内置的RedisTemplate

直接注入即可使用

* `JacksonRedisTemplate`

    对应的类型`RedisTemplate<String, Object>`

   ```
    @Resource
    private JacksonRedisTemplate redisTemplate;
   ```

* `ByteArrayRedisTemplate`

    对应的类型`RedisTemplate<byte[], byte[]>`

    ```
    @Resource
    private ByteArrayRedisTemplate redisTemplate;
    ```

* `StringByteArrayRedisTemplate`

    对应的类型`RedisTemplate<String, byte[]>`

    ```
    @Resource
    private StringByteArrayRedisTemplate redisTemplate;
    ```

# Redisson

#### Maven

```xml
<dependency>
    <groupId>io.github.jesushai</groupId>
    <artifactId>lemon-boot-starter-data-redisson</artifactId>
</dependency>
```

#### 配置`application.yml`

```
spring:
  redis:
    redisson:
      # 指定redisson的配置文件路径
      # 一旦指定了配置文件，以上的redis相关配置均失效
      config: "classpath:redisson.yml"
```

> 直接指定redisson的config文件，那么spring.redis中的其他配置都会失效<br/>
> 配置文件支持.json与.yml格式

#### 配置`redisson.yml`

```
#Redisson配置
#=================>哨兵模式
sentinelServersConfig:
  # 哨兵节点地址
  # 可以用"rediss://"来启用SSL连接
  sentinelAddresses:
    - "redis://172.16.40.54:26391"
    - "redis://172.16.40.54:26392"
    - "redis://172.16.40.54:26393"
  # 主服务器的名称是哨兵进程中用来监测主从服务切换情况的
  masterName: mymaster
  # 尝试连接的数据库编号0~15，默认值：0
  database: 7
  # 集群扫描间隔时间(毫秒)，默认1000
  scanInterval: 1000
#  checkSentinelsList:
  # 连接空闲超时，默认值：10000毫秒
  # 连接池里超过最小连接数的连接达到多少秒后会被关闭
  idleConnectionTimeout: 10000
  # 连接超时，默认值：10000毫秒
  # 同任何节点建立连接时的等待超时
  connectTimeout: 10000
  # 命令等待超时，默认值：3000毫秒
  timeout: 3000
  # 命令失败重试次数，默认值：3次
  retryAttempts: 3
  # 命令重试发送时间间隔，默认值：1500毫秒
  retryInterval: 1500
#  password: null
#  username:
  # 在Redis节点里显示的客户端名称
#  clientName: null
  # 启用SSL终端识别，默认值：true
  sslEnableEndpointIdentification: true
  # 确定采用哪种方式（JDK或OPENSSL）来实现SSL连接
  #  sslProvider: "JDK"
  # 指定SSL信任证书库的路径
  #  sslTruststore:
  # SSL信任证书库密码
  #  sslTruststorePassword:
  # 指定SSL钥匙库的路径
  #  sslKeystore:
  # 指定SSL钥匙库的密码
  #  sslKeystorePassword:
#  keepAlive:
#  tcpNoDelay:
  # 从节点最小空闲连接数，默认值：24
  slaveConnectionMinimumIdleSize: 50
  # 从节点连接池大小，默认值：64
  # 每个从服务节点里用于普通操作（非发布和订阅）连接的连接池最大容量
  slaveConnectionPoolSize: 300
#  failedSlaveReconnectionInterval: 3000
#  failedSlaveCheckInterval: 180000
  # 主节点最小空闲连接数，默认值：24
  masterConnectionMinimumIdleSize: 50
  # 主节点连接池大小，默认值：64
  masterConnectionPoolSize: 300
  # 读取操作的负载均衡模式：MASTER/SLAVE/MASTER_SLAVE，默认SLAVE
  # 在从服务节点里读取的数据说明已经至少有两个节点保存了该数据，确保了数据的高可用性。
  readMode: "SLAVE"
  # 订阅操作的负载均衡模式：MASTER/SLAVE，默认SLAVE只在从服务节点里订阅
  subscriptionMode: "SLAVE"
  # 单个连接最大订阅数量，默认值：5
  subscriptionsPerConnection: 5
  # 从节点发布和订阅连接的最小空闲连接数，默认值：1
  subscriptionConnectionMinimumIdleSize: 1
  # 从节点发布和订阅连接池大小，默认值：50
  subscriptionConnectionPoolSize: 50
  # DNS监控间隔，默认值：5000毫秒
  # 用来指定检查节点DNS变化的时间间隔
  # 使用的时候应该确保JVM里的DNS数据的缓存时间保持在足够低的范围才有意义
  # 用-1来禁用该功能
  dnsMonitoringInterval: 5000
  # 负载均衡算法类的选择，默认RoundRobinLoadBalancer
  # org.redisson.connection.balancer.WeightedRoundRobinBalancer - 权重轮询调度算法
  # org.redisson.connection.balancer.RoundRobinLoadBalancer - 轮询调度算法
  # org.redisson.connection.balancer.RandomLoadBalancer - 随机调度算法
  loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> {}
#=================>通用配置
# 线程池数量，默认值: 当前处理核数量 * 2
#threads: 0
# Netty线程池数量，默认值: 当前处理核数量 * 2
#nettyThreads: 0
# 编码，默认是JsonJacksonCodec，可以自行扩展
codec: !<org.redisson.codec.JsonJacksonCodec> {}
#  class: "org.redisson.codec.JsonJacksonCodec"
# 传输模式：NIO,EPOLL,KQUEUE
transportMode: "NIO"
# 线程池
#executor:
referenceEnabled: true
# 看门狗超时，默认30000毫秒
lockWatchdogTimeout: 30000
# 保持订阅发布顺序，默认true
keepPubSubOrder: true
#decodeInExecutor: false
#useScriptCache: false
#minCleanUpDelay: 5
#maxCleanUpDelay: 1800
#cleanUpKeysAmount: 100
#useThreadClassLoader: true

# 更多的配置参见：https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95
```

#### Cache
如果使用了redisson客户端，那么spring默认的cacheName是无效的，必须使用特定的cacheName

#### Shiro Session
1. 为了提升性能建议开启`zh.shiro.redis.session.in-memory-enabled`，但是`in-memory-timeout`不要设置过大，建议1-2秒即可。
1. 同样为了避免过多的刷新session到redis里，建议将`session-refresh-policy`设置为`Near`

## Redisson 分布式锁

### 1. tryLock手动方式

直接注入即可使用
```
@Autowired
private DistributedLocker distributedLocker;
```

### 2. 自动注解方式

```java
@Service
public class YourBusinessService {
    @DistributedLock(expression = "'goods-stock['+#goods.id+']'", releaseTime = 2)
    public void doSomethingAfterLock(Goods goods) {
        // do something.
    }
}
```

步骤：
  1. 进入service的方法前会先获取redisson可重入锁，并指定了锁的自动释放时间（秒）
  1. 随后执行方法的业务逻辑
  1. 最后释放锁

注意：
  1. 锁资源的名称尽量细粒化，否则会影响性能。例子中使用了商品的ID，这是一个商品库存的锁。
  1. Lease Time 请谨慎设置，根据真实的场景，你的逻辑运行时间再加上一些时间，防止业务逻辑还没跑完锁已经释放了。
  1. Redisson提供了看门狗的配置，可以自动延长锁的时效。


## 其他资料

https://cloud.tencent.com/developer/article/1543023 主从复制
https://cloud.tencent.com/developer/article/1543021 哨兵
https://cloud.tencent.com/developer/article/1543020 集群

https://cloud.tencent.com/developer/article/1543019 缓存雪崩与穿透

  雪崩 - hystrix限流
  
https://blog.csdn.net/wutengfei_java/article/details/100699538 解决redis分布式锁过期时间到了业务没执行完问题
https://mp.weixin.qq.com/s?__biz=MzU0OTk3ODQ3Ng==&mid=2247483777&idx=1&sn=f144ee415bb743f5422314907fc7d819&chksm=fba6e982ccd160945aa5c53434e933e2f03e89d9faf634f0b573c1f7b6f4d6d2635cad091276&scene=21#wechat_redirect 【性能优化之道】每秒上万并发下的Spring Cloud参数优化实战
https://mp.weixin.qq.com/s/RLeujAj5rwZGNYMD0uLbrg 每秒上千订单场景下的分布式锁高并发优化实践！【石杉的架构笔记】
