# Shiro认证与授权

## 准备工作
#### 自己的服务实现
1. 不要使用默认ShiroKey，请用自己的配置覆盖

    ```java
    @Component(BeanNameConstants.SHIRO_KEY)
    public class ShiroKeyImpl implements ShiroKey {
    
      @Override
      public String rememberMeEncryptKey() {
          return "test_shiro_key";
      }
    
      /**
       * 请求传递的token头变量
       */
      @Override
      public String loginTokenKey() {
          return "X-Test-Token";
      }
    
      @Override
      public String referencedSessionIdSource() {
          return "Stateless request";
      }
    }
    ```
   
   注意：需要指定Bean的名字
   
1. 用户服务实现

    ```java
    @Service(BeanNameConstants.USER_SERVICE)
    public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements ISysAdminService, UserService {

        @Autowired
        private SysAdminMapper sysAdminMapper;
    
        @SuppressWarnings("unchecked")
        @Override
        public List<SysUser> getSysUserByUsernameAndTenant(String username, Long tenantId) {
            return (List) sysAdminMapper.selectList(
                    new LambdaQueryWrapper<SysAdmin>()
                            .eq(SysAdmin::getUsername, username)
                            .eq(SysAdmin::getTenant, tenantId)
            );
        }
    }
    ```
   
1. 角色服务实现

    ```java
    @Service(BeanNameConstants.ROLE_SERVICE)
    public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService, RoleService {
   
       @Autowired
       private SysRoleMapper sysRoleMapper;
   
       @Override
       public Set<String> getNamesByIds(Long[] roleIds) {
           Set<String> result = new HashSet<>();
           getRolesByIds(roleIds).stream().map(SysRole::getName).forEach(result::add);
           return result;
       }
   
       public List<SysRole> getRolesByIds(Long[] roleIds) {
           return baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                   .in(SysRole::getId, roleIds));
       }
   }
    ```
   
1. 权限服务实现

    ```java
    @Service(BeanNameConstants.PERMISSION_SERVICE)
    public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService, PermissionService {
       
        @Override
        public Set<String> getPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
            Set<String> result = new HashSet<>();
            findPermissionsByRoleIds(roleIds, tenantId).stream().map(SysPermission::getPermission).forEach(result::add);
            return result;
        }

        public List<SysPermission> findPermissionsByRoleIds(Long[] roleIds, Long tenantId) {
            return baseMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getTenant, tenantId)
                    .in(SysPermission::getRoleId, roleIds));
        }
    }
    ```

1. 租户服务实现

    ```java
    @Service(BeanNameConstants.TENANT_SERVICE)
    public class TenantServiceImpl implements TenantService {
    
        @Override
        public List<Long> getAllTenantId() {
           return Collections.singleton(0L);
        }
    
        @Override
        public List<String> getAllTenantClientType(Long tenantId) {
            return null;
        }
    
        @Override
        public String getTenantName() {
            return "测试租户";
        }
    }
    ```

#### 配置文件`application.yml`

```yaml
zh:
  shiro:
    # session 超时时间，单位为秒
    session-timeout: 3600
    # rememberMe cookie有效时长（秒），默认一天
    cookie-timeout: 86400
    # 免认证的路径配置，如静态资源等，逗号分隔
    anon-url: /index/index,/actuator/**,/test/**
    # 登录url
    login-url: /auth/login
    # 登录成功后的首页url
    success-url: /auth/index
    # 登出url
    logout-url:  /auth/logout
    # 未授权跳转url
    unauthorized-url: /auth/403
    # redis支持
    redis:
      # 缓存session
      session:
        # session key前缀
        key-prefix: "test-shiro-session:"
        in-memory-enabled: false
        # ession保留在内存中的时间（秒）
        in-memory-timeout: 300
      # 缓存principal
      cache:
        # principal在redis中的时效（秒）
        time-to-live: 3600
        # 最大空闲秒数
        max-idle-time: 3600
        # cache key前缀
        key-prefix: "test-shiro-cache:"
        # 计算key的依据，即principal的主键
        principal-id-field-name: id

```

* `anno-url`里的路径是可以匿名访问的，不需要身份认证。
* `login-url`指定登录的API地址，也是不需要认证直接访问的。

## 身份认证与授权
### 认证

1. 登录的实现

    ```
    @RestController
    @RequestMapping("/auth")
    @Slf4j
    public class AuthController {
        /*
         * uri: /auth/login
         *  { username : value, password : value, tenantId: value }
         */
        @PostMapping("/login")
        public Object login(@RequestBody String body, HttpServletRequest request) {
            String username = JacksonUtils.parseString(body, "username");
            String password = JacksonUtils.parseString(body, "password");
            Long tenantId = JacksonUtils.parseLong(body, "tenantId");
            if (tenantId == null) {
                tenantId = 0L;
            }
    
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new AuthenticationException();
            }
    
            Subject currentUser = SecurityUtils.getSubject();
            try {
                currentUser.login(new TenantUsernamePasswordToken(username, password, tenantId));
            } catch (AuthenticationException e) {
                throw new AuthenticationException();
            }
    
            currentUser = SecurityUtils.getSubject();
            SysAdmin admin = (SysAdmin) currentUser.getPrincipal();
            admin.setLastLoginIp(IpUtils.getIpAddr(request));
            admin.setLastLoginTime(TimestampUtils.now());
            adminService.updateById(admin);
    
            LoggerUtils.info(log, "登录");
    
            // userInfo
            Map<String, Object> adminInfo = new HashMap<>();
            adminInfo.put("nickName", admin.getUsername());
            adminInfo.put("avatar", admin.getAvatar());
    
            Map<Object, Object> result = new HashMap<>();
            result.put("token", currentUser.getSession().getId());
            result.put("adminInfo", adminInfo);
            return result;
        }
    }
    ```
   
1. 登出的实现
   ```
    // uri: /auth/logout
    @RequiresAuthentication
    @PostMapping("/logout")
    public Object logout() {
        Subject currentUser = SecurityUtils.getSubject();

        LoggerUtils.info(log, "退出");
        currentUser.logout();
        return Result.ok();
    }
   ```

1. 获取用户权限角色

   ```
    // uri: /auth/info
    @RequiresAuthentication
    @GetMapping("/info")
    public Object info() {
        Subject currentUser = SecurityUtils.getSubject();
        SysAdmin admin = (SysAdmin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        Long[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.getNamesByIds(roleIds);
        Set<String> permissions = permissionService.getPermissionsByRoleIds(roleIds, admin.getTenant());
        data.put("roles", roles);
        data.put("perms", toApi(permissions, admin.getTenant()));
        return data;
    }
   ```
   
### 定义授权api
在具体的Controller方法上标注相应的注解
* `@RequiresPermissions`标注授权的ID，即告诉shiro这个api需要拥有哪些授权的用户才能访问，此ID全局范围内不能重复。
    <br/>例：`@RequiresPermissions("admin:permission:tree")`
* `@PermissionDescription`定义这个api的菜单与功能行为，支持多级菜单。
    <br/>例：`@PermissionDescription(menu = {"SYS-ADMIN", "SYS-ADMIN-PERMISSION"}, action = "SELECT-ALL")`
* 支持国际化菜单功能，仅需要在message.properties中维护即可

>完整的例子：

1. 标注授权api

    ```java
    @RestController
    @RequestMapping("/permission")
    public class SysPermissionController {
    
        @Autowired
        private PermissionService permissionService;
    
        @Autowired
        private AuthenticationService authenticationService;
    
        @RequiresPermissions("admin:permission:tree")
        @PermissionDescription(menu = {"SYS-ADMIN", "SYS-ADMIN-PERMISSION"}, action = "SYS-ADMIN-PERMISSION-SELECT-ALL-TREE")
        @GetMapping("/tree")
        public List<PermissionTreeNode> getAllPermission() {
            Long tenantId = authenticationService.getPrincipal().getTenant();
            return permissionService.getPermissionTree("shiro", tenantId);
        }
    
        @RequiresPermissions("admin:permission:list")
        @PermissionDescription(menu = {"SYS-ADMIN", "SYS-ADMIN-PERMISSION"}, action = "SYS-ADMIN-PERMISSION-SELECT-ALL-LIST")
        @GetMapping("/list")
        public Set<String> getAllPermissionCode() {
            Long tenantId = authenticationService.getPrincipal().getTenant();
            return permissionService.getAllPermission(tenantId);
        }
    }
    
    ```

1. 维护国际化i18n相应的`message.properties`文件（[参见exception.md](./exception.md)）
    
    ```properties
    # 菜单与功能
    SYS-ADMIN=系统管理
    SYS-ADMIN-PERMISSION=权限管理
    SYS-ADMIN-PERMISSION-SELECT-ALL-TREE=查询
    SYS-ADMIN-PERMISSION-SELECT-ALL-LIST=查询
    ```
   
### 获取已经认证的主题（用户）
* 注入统一身份服务组件

  ```
  @Autowhire
  private AuthenticationService authenticationService;
  ```
  
* 获取Subject

  ```
  Subject subject = (Subject) authenticationService.getSubject();
  // 是否认证过的
  subject.isAuthenticated();
  // 是否拥有某个角色
  subject.hasRole("role");
  // 获得session
  subject.getSession();
  // 等等...
  ```

* 获取Principle

  ```
  User user = authenticationService.getPrincipal();
  // 用户的租户
  Long tenantId = user.getTenant();
  ```

### 获取授权列表
* 注入系统授权服务组件

  ```
  @Autowhire
  private PermissionService permissionService;
  ```
  
* 获取所有系统授权

  ```
  // 在com.app包下搜索所有授权api，返回菜单树形结构
  List<PermissionTreeNode> permissionTree = permissionService.getPermissionTree("com.app");
  
  // 同上，返回平面结构
  List<Permission> permissions = permissionService.getPermissions("com.app");
  ```
  
* 获取当前租户的所有系统授权

  ```
  // 当前Session用户的租户
  Long tenantId = authenticationService.getPrincipal().getTenant();
  
  // 在com.app包下搜索所有当前租户有权分配的授权api，返回菜单树形结构
  List<PermissionTreeNode> permissionTree = permissionService.getPermissionTree("com.app", tenantId);

  // 同上，返回平面结构
  List<Permission> permissions = permissionService.getPermissions("com.app", tenantId);
  
  ```

### 示例
#### shiro测试工程：[lemon-boot-web-test-shiro](../../lemon-boot-tests/lemon-boot-web-tests/lemon-boot-web-test-shiro)
