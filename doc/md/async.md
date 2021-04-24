# 异步执行与事件
## 异步执行器
#### 准备
1. 开启异步执行器

    `@EnableAsyncExecutor`
  
1. `application.yml`线程池调优
    
    ```yaml
    zh:
      async:
        core-pool-size: 5
        queue-capacity: 30
        max-pool-size: 50
        keep-alive-seconds: 30
        thread-name-prefix: Test-Async-Thread
        await-termination-seconds: 60
        rejected-execution-handler: CallerRunsPolicy
    ```
   
   * `core-pool-size`: 核心线程数会一直存在池中，优先级最高
   * `queue-capacity`: 队列容量，核心线程队列达到这个数量时才会新建线程，直到最大线程数满为止。
   * `max-pool-size`: 最大线程数，当核心线程全部队列满时才会新建线程，直到最大值
   * `keep-alive-seconds`: 非核心线程活跃时间（秒），请严格控制在线程中运行的任务时间为毫秒级。
   * `thread-name-prefix`: 线程名称前缀
   * `await-termination-seconds`: 多少秒后中断线程的执行
   * `rejected-execution-handler`: 拒绝策略: 即核心线程、最大线程都满且队列也都满的情况下走拒绝策略<br/>
   
      >拒绝策略可选择如下：
      * `AbortPolicy`: 不执行新任务，直接抛出异常，提示线程池已满
      * `DisCardPolicy`: 不执行新任务，也不抛出异常，直接忽略
      * `DisCardOldSetPolicy`: 将消息队列中的第一个任务替换为当前新进来的任务执行
      * `CallerRunsPolicy`: 直接调用execute来执行当前任务

   参考资料：[https://www.cnblogs.com/monkay/p/11170421.html](https://www.cnblogs.com/monkay/p/11170421.html)
   <br/>建议：
   >请根据实际情况优化这些配置，原则上不建议把队列设置太高，这样会提高运行负载最大线程的设置将会失去意义。<br/>
   >

1. 具体项目遇到需要单独开启线程池时，可以参照我的实现来扩展
   * [ThreadPoolTaskExecutorProperties](../../lemon-boot-project/lemon-boot-autoconfiguration/src/main/java/com/lemon/boot/autoconfigure/commons/properties/ThreadPoolTaskExecutorProperties.java)
   * [ThreadPoolTaskExecutorAutoConfiguration](../../lemon-boot-project/lemon-boot-autoconfiguration/src/main/java/com/lemon/boot/autoconfigure/commons/ThreadPoolTaskExecutorAutoConfiguration.java)

#### 异步服务调用

  1. 在主线程中注入上下文变量
 
     `AppContextHolder.getContext().set("Key", "Test shared info.");`
  
  2. 定义异步服务方法
  
     ```
        @Async(BeanNameConstants.CORE_ASYNC_POOL)
        public void log(String msg) throws InterruptedException {
            // 模拟耗时的操作
            Thread.sleep(100L);
            // 输出子线程信息与上下文变量
            System.out.printf("[%30s] - %s: %s\n",
                    Thread.currentThread().getName(),
                    AppContextHolder.getContext().get("Key"),
                    msg);
        }
     ```

  注意：
  >上文讲到的`application.yml`中的配置即核心异步线程池的名称就是 `AppConstants.CORE_ASYNC_POOL`<br/>
  >所以必须在`@Async`注解上明确指定使用的线程池<br/>
  >你也可以指定你自己扩展的异步线程池 `@Async("YourAsyncPool")`

#### 异步事件监听

  1. 定义事件
  
        ```java
        @Data
        @AllArgsConstructor
        public class AsyncEvent implements DomainEvent<String> {
        
            private String eventSource;
        
            @Override
            public boolean sameEventAs(String other) {
                return false;
            }
        
            @Override
            public String getEventState() {
                return null;
            }
        
            @Override
            public void setEventState(String eventState) {
            }
        }
        ```

  1. 异步事件监听器
  
        ```java
        @Component
        public class AsyncEventListener {
        
            // 指定异步线程池来处理监听到的事件
            @Async(BeanNameConstants.CORE_ASYNC_POOL)
            @EventListener
            public void handle(AsyncEvent event) {
                // 输出子线程名称、事件信息与上下文变量
                System.out.printf("@Log [%30s] - %s: %s\n",
                        Thread.currentThread().getName(),
                        AppContextHolder.getContext().get("Key"),
                        event.getEventSource());
            }
        }
        ```
     
     >同理，需要明确指定异步线程池的名称

  1. 发送事件
  
        ```
        // 事件发布器
        @Autowired
        private DefaultEventPublisher eventPublisher;
     
        // 明确指定异步线程池
        public void foo(String msg) {
            // 异步发送事件
            eventPublisher.publish(new AsyncEvent("SubThread AsyncEvent" + msg));
        }
        ```

## 线程间传递
#### 定制自己的`Application Context`
1. 定义**`Context`**
    
    ```java
    public class AppContext {
    
        private final ConcurrentHashMap<String, Object> values = new ConcurrentHashMap<>();
    
        /**
         * 附件，会在服务间传递
         */
        private final ConcurrentHashMap<String, String> attachments = new ConcurrentHashMap<>();
    
        public Object get(String key) {
            return values.get(checkKey(key));
        }
    
        public <T> T get(String key, Class<T> clazz) {
            Object object = get(checkKey(key));
            return (object != null && clazz.isAssignableFrom(object.getClass())) ? (T) object : null;
        }
    
        public AppContext set(String key, Object value) {
            String k = checkKey(key);
            if (value == null) {
                values.remove(k);
            } else {
                values.put(k, value);
            }
            return this;
        }
    
        public AppContext remove(String key) {
            values.remove(checkKey(key));
            return this;
        }
    
        public Map<String, String> getAttachments() {
            return attachments;
        }
    
        public String getAttachment(String key) {
            return attachments.get(checkKey(key));
        }
    
        public AppContext setAttachment(String key, String value) {
            String k = checkKey(key);
            if (value == null) {
                attachments.remove(k);
            } else {
                attachments.put(k, value);
            }
            return this;
        }
    
        public AppContext removeAttachment(String key) {
            attachments.remove(checkKey(key));
            return this;
        }
    
        public AppContext setAttachments(Map<String, String> attachment) {
            this.attachments.clear();
            if (attachment != null && attachment.size() > 0) {
                attachment.keySet().forEach(k -> {
                    attachments.put(k.toUpperCase(), attachment.get(k));
                });
            }
            return this;
        }
    
        public void clearAttachments() {
            this.attachments.clear();
        }
    
        public void clear() {
            this.values.clear();
            this.attachments.clear();
        }
    
        private String checkKey(String key) {
            if (null == key || "".equals(key))
                throw new SystemException("AppContext: Key cannot be empty.");
            return key;
        }
    
    }
    ```

1. 定义**`Context Holder`**

   ```java
        @Slf4j
        public class AppContextHolder {
        
            private static final TransmittableThreadLocal<AppContext> holder = new TransmittableThreadLocal<AppContext>() {
                /**
                 * 定制化备份方法，这里是复制的引用
                 */
                @Override
                public AppContext copy(AppContext parentValue) {
                    return super.copy(parentValue);
                }
        
                @Override
                protected void beforeExecute() {
                    super.beforeExecute();
                }
        
                @Override
                protected void afterExecute() {
                    super.afterExecute();
                }
        
                /**
                 * 重写初始化方法
                 */
                @Override
                protected AppContext initialValue() {
                    return new AppContext();
                }
            };
        
            /**
             * 如果是Session级的缓存，避免内存泄漏请在Session结束的时候清理holder
             */
            public static void clearContext() {
               holder.get().clear();
                holder.remove();
            }
        
            public static AppContext getContext() {
                AppContext context = holder.get();
                if (context == null) {
                    context = new AppContext();
                    holder.set(context);
                }
                return context;
            }
        
            public static void setContext(AppContext context) {
                Assert.notNull(context, "Only non-AppContext instances are permitted");
                holder.set(context);
            }
        }
    ```

    注意：
    * 便于上下文变量的传递例子中使用了TTL
    * 可以简单的使用TTL<br/>
        `private static final TransmittableThreadLocal<AppContext> holder = new TransmittableThreadLocal<AppContext>();`
    * 也可以如例子中实现一些定制化操作，例如引用可以改为深度克隆等等

## 示例
#### 测试工程：[lemon-boot-web-test](../../lemon-boot-tests/lemon-boot-web-tests/lemon-boot-web-test)
