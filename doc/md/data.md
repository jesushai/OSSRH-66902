# 实体框架
## Spring Boot JPA
### maven pom

```xml
<dependency>
    <groupId>io.github.jesushai</groupId>
    <artifactId>lemon-boot-starter-data-jpa</artifactId>
    <version>1.0</version>
</dependency>
```

### 实体
#### 1. 自定义ID生成策略
> 启用配置<br/>
**`@EnableCustomJpaRepositories("jpa.repository")`**<br/>
>`jpa.repository`请换成你的repository包

* 所有实体不能用混合主键，且必须为Long，框架会在新增的时候自动生成分布式唯一主键

```java
@Entity
@Table(name = "base_region")
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class Region {

  @Id
  @Column(name = "id_")
  private Long id;

}
```

可选 **application.yml** 文件配置分布式ID的节点，如果省略则根据服务器IP地址自动计算。

```yaml
zh:
  db:
    sequence:
      # 分布式ID的服务节点，如果不设置则根据服务器IP自动计算节点
      # 要么所有服务器都不设置，要指定就全部指定但不能重复
      # 范围：0-1023之间
      node-id: 100
```

#### 2. 更新忽略null字段
调用`save()`方法的时候，如果有些字段不想更新，可以设置为null。

#### 3. 实体枚举
* 表中的枚举字段设置为int类型，varchar类型请自己重写DbFieldEnum。
* 创建你的枚举

```java
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RegionTypeEnum implements DbFieldEnum {

    Province(1, "省"),
    City(2, "市"),
    District(3, "县区");

    //@EnumValue //mybatis-plus下写入到数据库里的枚举值，jpa不需要
    private final int value;
    private final String display;

    RegionTypeEnum(int value, String display) {
        this.value = value;
        this.display = display;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    /**
     * Json反序列化为枚举的时候需要依据Json中code的值来决定反序列化为哪个枚举
     */
    @JsonCreator
    public static RegionTypeEnum forValues(@JsonProperty("value") int value) throws Exception {
        return DbFieldEnum.fromValue(RegionTypeEnum.class, value);
    }
}
```

* 实体枚举字段（枚举value不是从0开始的自定义序数，不推荐）

```java
  @Entity
  @Table(name = "base_region")
  @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
  @Data
  @ToString
  @EqualsAndHashCode(callSuper = false)
  public class Region {

    @Type(type = "DbEnumType")
    @Column(name = "type_")
    private RegionTypeEnum type;

  }
```

* 实体枚举字段（枚举value是从0开始有序整数，虽然反人类但却是最佳实践）

```java
  @Entity
  @Table(name = "base_region")
  @JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
  @Data
  @ToString
  @EqualsAndHashCode(callSuper = false)
  public class Region {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type_")
    private RegionTypeEnum type;

  }
```

#### 4. 逻辑删除
JPA实现逻辑删除没有MybatisPlus那么简单封装好，因为时间关系已有的想法暂时没有实现。<br/>
目前简单粗暴的在实体上增加以下注解

```java
@Entity
@Table(name = "base_brand")
@SQLDelete(sql = "update base_brand set deleted_ = 1 where id_ = ? and update_time_ = ?")
@Where(clause = "deleted_ = 0")
public class Brand {
    
    @Column(name = "update_time_")
    @Version
    private Timestamp updateTime;

    @Column(name = "deleted_")
    @JsonIgnore
    private Boolean deleted;

}
```

* 所有通过Repository对数据表的操作都会被拦截<br/>
  其中删除操作会被`@SQLDelete`指定SQL替换，如果有乐观锁字段必须加到where子句中，否则报错<br/>
  其他操作会自动在where子句中加入`@Where`指定的条件
* 逻辑删除字段不应该让前端知晓，所以需要`@JsonIgnore`
* 好的设计逻辑删除字段的类型一般都是bit或tiny(1)，这两个类型都是0或1，但通过persistence/mybatis反射都会自动转换成Boolean类型

### 示例
#### 测试工程
1. [lemon-boot-data-mybatis-test](../../lemon-boot-tests/lemon-boot-data-tests/lemon-boot-data-mybatis-test)

----------------------------------------------
## Mybatis-plus
### maven pom

```xml
<dependency>
    <groupId>io.github.jesushai</groupId>
    <artifactId>lemon-boot-starter-data-mybatis</artifactId>
    <version>1.0</version>
</dependency>
```
### 1. Model
#### 1.1 Mybatis-Plus代码生成器

```xml
    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
    </dependency>
```

首先通过【[代码生成器](../../lemon-boot-project/lemon-boot-framework/src/test/java/com/lemon/framework/db/tools/CodeGenerator.java)】反向生成mapper、entity、service、controller<br/>
> 代码生成器自动生成下列内容
* 使用自定义主键生成策略<br/>
  > 自动配置类（自动注入无需处理）<br/>
  > `SequenceGeneratorAutoConfiguration`<br/>
  
  实体类的主键设置`@TableId`的`type`为`IdType.ASSIGN_ID`
  
  ```java
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @TableName(value = "base_region")
    public class Region extends Model<Region> implements Serializable {
      @TableId(value = "id_", type = IdType.ASSIGN_ID)
      private Long id;
    }
  ```
  
* 填充审计信息

  ```java
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @TableName(value = "base_region")
    public class Region extends Model<Region> implements Serializable {
      /**
       * 创建时间
       */
      @TableField(value = "add_time_", fill = FieldFill.INSERT)
      private Timestamp addTime;
      
      /**
       * 更新时间
       */
      @TableField(value = "update_time_", fill = FieldFill.INSERT_UPDATE)
      @Version // 乐观锁版本
      private Timestamp updateTime;
    }
  ```
  
* 逻辑删除字段

  mybatis的删除操作都只是更新删除字段，并不会真正删除物理数据。<br/>
  所有查询（非直接SQL语句）也都会自动过滤掉删除的记录。
  
  ```java
    @Data
    @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @TableName(value = "base_region")
    public class Region extends Model<Region> implements Serializable {
      /**
       * 逻辑删除
       */
      @TableField("deleted_")
      @TableLogic
      private Boolean deleted;
    }
  ```
  
* 自动类型转换

**注意：**
> 可能存在争抢资源的表乐观锁建议用bigint(20)

#### 1.2 使用枚举
1. `application.yml`中配置枚举扫描包

    ```yaml
    mybatis-plus:
      # 实体枚举扫描
      type-enums-package: com.yourpackage.enums
    ```
   
1. 将表中的枚举字段设置为整型，并确定其意义（例子为区域类型）
1. 新建字段对应的枚举类，枚举将会作为对象序列化成Json

    ```java
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum RegionTypeEnum implements DbFieldEnum {
    
        Province(1, "省"),
        City(2, "市"),
        District(3, "县区");
    
        @EnumValue //写入到数据库里的枚举值
        private final int value;
        private final String display;
    
        RegionTypeEnum(int value, String display) {
            this.value = value;
            this.display = display;
        }
    
        @Override
        public int getValue() {
            return value;
        }
    
        @Override
        public String getDisplay() {
            return display;
        }
    
        /**
         * Json反序列化为枚举的时候需要依据Json中code的值来决定反序列化为哪个枚举
         */
        @JsonCreator
        public static RegionTypeEnum forValues(@JsonProperty("value") int value) throws Exception {
            return DbFieldEnum.fromValue(RegionTypeEnum.class, value);
        }
    }
    ```
   
    代码详见[RegionTypeEnum.java](../../lemon-boot-tests/lemon-boot-data-tests/lemon-boot-data-mybatis-test/src/main/java/mp/enums/RegionTypeEnum.java)

#### 1.3 类型转换封装

1. 实体类注解 `@TableName(autoResultMap=true)`开启自动转换

    ```
    @TableName(value = "base_category", autoResultMap = true) //字段类型转换必须开启
    ```
   
1. 字段注解增加`Handler`

    ```
    /**
     * 类目关键字，以JSON数组格式
     */
    @TableField(value = "keywords_", typeHandler = JsonStringArrayTypeHandler.class)
    private String[] keywords;
    ```
   
1. 编写自定义`Handler`

    ```
   public class JsonStringArrayTypeHandler extends BaseTypeHandler<String[]> {
       private static final ObjectMapper mapper = new ObjectMapper();
   
       @Override
       public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
           ps.setString(i, toJson(parameter));
       }
   
       @Override
       public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
           return this.toObject(rs.getString(columnName));
       }
   
       @Override
       public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
           return this.toObject(rs.getString(columnIndex));
       }
   
       @Override
       public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
           return this.toObject(cs.getString(columnIndex));
       }
   
       private String toJson(String[] params) {
           try {
               return mapper.writeValueAsString(params);
           } catch (Exception e) {
               e.printStackTrace();
           }
           return "[]";
       }
   
       private String[] toObject(String content) {
           if (content != null && !content.isEmpty()) {
               try {
                   return (String[]) mapper.readValue(content, String[].class);
               } catch (Exception e) {
                   throw new RuntimeException(e);
               }
           } else {
               return null;
           }
       }
   }
    ```

#### 1.4 Mybatis-plus配置类

这里配置分页同时配置了租户的解析器，从此执行SQL语句可以忽略租户了！

如果在程序中，有部分SQL不需要加上租户ID的表示，需要过滤特定的sql:
通过租户注解的形式

```
public interface UserMapper extends BaseMapper<User> {
    @SqlParser(filter = true)
    int updateByMyWrapper(@Param(Constants.WRAPPER) Wrapper<User> userWrapper, @Param("user") User user);
}
```

```java
@EnableTransactionManagement
@Configuration
@MapperScan("com.lemon.framework.db.test.mp.*.mapper*") //扫描包路径
public class MybatisPlusConfig {

   @Resource
   private AuthenticationService authenticationService;

   /**
    * 不包含租户字段的表需要排除掉
    */
   private static final String excludeTenantTableNames = "sys_coding_category,sys_menu,sys_storage,sys_api_limit,sys_tenant";

    /**
     * 3.4新特性
     * 拦截方式改变了
     * <p>
     * 使用多个功能需要注意顺序关系,建议使用如下顺序
     * <p>
     * 多租户,动态表名
     * 分页,乐观锁
     * sql性能规范,防止全表更新与删除
     * 总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 多租户插件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 从请求中获得租户
                User user = authenticationService.getPrincipal();
                Expression tenantId;
                if (null == user)
                    tenantId = new LongValue(0L);
                else
                    tenantId = new LongValue(user.getTenant());
                return tenantId;
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                // 是否需要需要过滤某一张表
                return excludeTenantTableNames.contains(tableName + ',');
            }

            // 设置租户的字段，默认是tenant_id
            @Override
            public String getTenantIdColumn() {
                return "tenant_";
            }
        }));

        // 动态表名
//        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
//        HashMap<String, TableNameHandler> map = new HashMap<String, TableNameHandler>(2) {{
//            put("user", (sql, tableName) -> {
//                String year = "_2018";
//                int random = new Random().nextInt(10);
//                if (random % 2 == 1) {
//                    year = "_2019";
//                }
//                return tableName + year;
//            });
//        }};
//
//        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(map);
//        interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);

        // 分页插件
        // 必须放在租户插件之后！
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 防止全表更新
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

   /**
    * 自动填充审计信息
    */
   @Bean
   public MyFillMetaObjectHandler myFillMetaObjectHandler() {
       return new MyFillMetaObjectHandler(authenticationService);
   }

}
```

自动填充处理器：

```java
public class MyFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Timestamp now = TimestampUtils.now();
        User user = authenticationService.getPrincipal();

        this.strictInsertFill(metaObject, ENTITY_CREATE_DATE_FIELD, Timestamp.class, now);
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_DATE_FIELD, Timestamp.class, now);

        this.strictInsertFill(metaObject, ENTITY_CREATE_BY_FIELD, Long.class,
                null == user ? 0L : user.getId());
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_BY_FIELD, Long.class,
                null == user ? 0L : user.getId());

        this.strictInsertFill(metaObject, ENTITY_CREATE_NAME_BY_FIELD, String.class,
                null == user ? StringUtils.EMPTY : user.getUsername());
        this.strictInsertFill(metaObject, ENTITY_MODIFIED_NAME_BY_FIELD, String.class,
                null == user ? StringUtils.EMPTY : user.getUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Timestamp now = TimestampUtils.now();
        User user = authenticationService.getPrincipal();
        // setFieldValByName不管实体是否持有这些属性，都强制更新
        // strictUpdateFill不会强制更新，只有属性为null时才会更新
        this.setFieldValByName(ENTITY_MODIFIED_DATE_FIELD, now, metaObject);
        this.setFieldValByName(ENTITY_MODIFIED_BY_FIELD, null == user ? 0L : user.getId(), metaObject);
        this.setFieldValByName(ENTITY_MODIFIED_NAME_BY_FIELD, null == user ? StringUtils.EMPTY : user.getUsername(), metaObject);
    }
}
```

### 2. 查询
#### 1.1 通过Service直接查询
* `service.lambdaQuery()`

    ```
    List<Admin> adminList = adminService.lambdaQuery()
            .eq(Admin::getUsername, username)
            .list();
    ```
  
  SQL: `where username=:username`
  
* `service.list(Wrapper...)`

    ```
    ```

#### 1.2 通过Mapper查询
* `LambdaQueryWrapper`

    ```
    baseMapper.selectList(
        new LambdaQueryWrapper<GoodsProduct>()
                 .eq(GoodsProduct::getGoodsId, gid)
    );
    ```
  
  SQL: `where goodsId=:gid`

#### 1.3 主从分离（多数据源）
参见第4小节

### 3. 增删改操作
#### 1.1 新增
* Service

  ```
  @Resource
  private GoodsService goodsService;
  
  .... {
    Goods goods = new Goods();
    ...setter
    goodsService.save(goods);
  }
  ```
  
> save方法失败会抛出异常，同时回滚事务。

* Mapper

  ```
  @Resource
  private GoodsMapper goodsMapper;
  
  .... {
    Goods goods = new Goods();
    ...setter
    goodsMapper.insert(goods);
  }
  ```
  
> insert方法失败会抛出异常，同时回滚事务。

#### 1.2 修改
* Service

  ```
  boolean success = goodsService.updateById(goods);
  if (!success) {
    throw new RuntimeException("更新数据失败");
  }
  ```
  ```
  boolean success = goodsService.saveOrUpdate(goods);
  if (!success) {
    throw new RuntimeException("更新数据失败");
  }
  ```
  
> 如果确切知道是更新不是插入，请不要使用saveOrUpdate，避免不必要的反射操作。

* 关于Update的机制
    * 先查询实体再执行`updateById`来修改会判断乐观锁
    * 根据上一条的原则，如果只更新很少的字段，请new一个新实体，然后将主键字段、乐观锁字段与要更新的字段复制过去即可。
    * 直接new实体执行`updateById`不会判断乐观锁！因为乐观锁的属性为null
    * 实体中null属性不会被update
    * 乐观锁仅以条件出现在where子句中，所以乐观锁被改变不会抛出异常，只是返回值为0！


**注意：**
> 所有的更新语句涉及到乐观锁失败的，都不会抛出异常！<br/>
> 这与JPA有本质上的不同，所以必须自己判断返回值是否true！<br/>
> 因为mybatis更新是where条件带乐观锁字段，所以乐观锁变更的时候只是update记录数=0而已，并不会抛出异常。

### 4. 多租户多数据源动态切换
1. maven pom 依赖

    ```xml
    <dependencies>
        <dependency>
            <groupId>io.github.jesushai</groupId>
            <artifactId>lemon-boot-starter-data-mybatis</artifactId>
        </dependency>
       
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
    ```
   
1. 开启动态数据源注解`@EnableDynamicDatasource`

    ```java
    @SpringBootApplication
    @EnableDynamicDatasource
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```  
   
1. 配置信息`application.xml`

    ```yaml
      datasource:
        druid:
          ...
        dynamic:
          primary: tenant100-master
          strict: true #设置严格模式,默认false不启动. 启动后在未匹配到指定数据源时候回抛出异常,不启动会使用默认数据源.
          druid: #druid全局默认配置
            ...
          datasource:
            # 多租户主从数据源（高级）
            # 固定格式："tenant"+"租户ID"+"-分库数据源"+"_分组序号"
            tenant100-master:
              url: ...
              driver-class-name:  com.mysql.jdbc.Driver
              username: ...
              password: ...
            tenant100-slave_1:
              url: ...
              driver-class-name:  com.mysql.jdbc.Driver
              username: ...
              password: ...
    ```
   
   注意：
   * 多租户的名命必须严格按照规则定义
   * datasource.dynamic.primary必须指定，且必须存在
   
1. Service、Repository(JPA)、Mapper(Mybatis)指定数据源表达式

    例子：
    
    ```java
    @Service
    public class RegionService {
    
        @Resource
        private RegionRepository regionRepository;
    
        //from principal.
        //tenant100-master
        @DS("#principal.tenant + '-master'")
        public Region save(Region region) {
            return regionRepository.save(region);
        }
    
        //from header.
        //tenant100-master
        @DS("#header.tenant + '-master'")
        public Region save(Region region) {
            return regionRepository.save(region);
        }
    
        //from session.
        //tenant100Slave_n
        @DS("#session.tenant + '-slave'")
        public List<Region> query1() {
            return regionRepository.findAll();
        }
   
        //from param.
        //tenant100Slave_n
        @DS("'tenant' + #tenantId + '-slave'")
        public List<Region> query2(Long tenantId) {
           //...
        }
   
        //from param.
        //tenant100Slave_n
        @DS("'tenant' + #user.tenantId + '-slave'")
        public List<Region> query2(User user) {
           //...
        }
    }
    ```
   
   说明：
   * principal: 由当前操作员所在租户动态决定使用哪个数据源
   * header: 由前端请求header.tenant属性指定当前的租户`#header.tenant`
   * session: 由session中的tenant属性指定
   * spel表达式: 由参数决定使用哪个数据源

详见测试工程 [lemon-boot-data-mybatis-test](../../lemon-boot-tests/lemon-boot-data-tests/lemon-boot-data-mybatis-test)
