### 名词
* Schema：定义了GraphQL API的类型系统。它完整描述了客户端可以访问的所有数据（对象、成员变量、关系、任何类型）。
* Introspection：客户端可以通过“自省”获取关于schema的信息。
* Meta：描述Schema的元数据。
* Field：Schema中定义的数据字段。
* Id：实体的主键。
* Key：表述实体的Key，可能内容比id要多，用于Redis缓存等。
* ForeignKey：外键。
* Argument：字段的附加数据。
* Mutation：变化，即增删改操作。
* Input Object：Mutation时传入的数据，由Input field组成，即RequestDTO。
* Payload Object：Mutation时服务器返回的数据，由Return field组成，即ResponseDTO。
* Variables：Query的变量，用于Argument中的表达式的带入。
* Validate：定义Schema的验证规则。
* Specification：Mutation的复杂业务规范。
* TypeHandler：类型解析器。
* Resolver：Query的解析器扩展。
* Repository：Query工厂。

### 定义
#### Schema
* Meta
    * Key
    * Id
    * Field
    * 多实体关联关系
* Optional
    * 排序分页模式
    * 筛选条件
* Validate
    * 验证规则
    * 直接SQL
    * 缓存规则

### 实现
1. 通用的Controller
    * introspection 只获取scheme
    * query 查询操作
    * mutation 增删改操作
1. schema定义：


参考：
https://www.zhihu.com/question/264629587
