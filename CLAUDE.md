# Claude Code 编码规范 - Lingma 项目

## 1. 项目结构规范

### 包命名
```
com.central.{模块名}
```
- `com.central.common` - 公共模块
- `com.central.user` - 用户模块
- `com.central.sale` - 销售模块
- `com.central.security` - 安全模块

### 模块层级
```
{模块}/
├── controller/    # 控制层
├── service/       # 服务层
├── mapper/        # 数据访问层
├── entity/        # 实体类
├── dto/           # 数据传输对象
├── config/        # 配置类
├── util/          # 工具类
├── feign/         # Feign客户端
└── exception/     # 异常类
```

## 2. Java 编码规范

### 类和接口命名
- **Controller**: `XxxController` - 例: `UserController`
- **Service**: `XxxService` - 例: `UserService`
- **Mapper**: `XxxMapper` - 例: `UserMapper`
- **Entity**: `Xxx` - 例: `User`
- **DTO**: `XxxDTO` - 例: `ProductPriceQueryDTO`
- **Result**: `Result<T>` - 统一响应包装类

### 方法命名
- Controller: `handleXxx`, `getXxx`, `postXxx`, `putXxx`, `deleteXxx`
- Service: `getXxx`, `saveXxx`, `updateXxx`, `deleteXxx`, `listXxx`
- Mapper: `selectXxx`, `insertXxx`, `updateXxx`, `deleteXxx`

### 代码风格
- 使用 Lombok 注解 (`@Data`, `@Service`, `@Mapper` 等)
- 构造方法使用 `@RequiredArgsConstructor` 或 `@AllArgsConstructor`
- 日志使用 `log.info()`, `log.error()` 等
- 异常处理: 业务异常使用自定义异常类

### 字段命名
- Java 字段: 驼峰命名 - 例: `userName`, `createTime`
- 数据库字段: 下划线命名 - 例: `user_name`, `create_time`

## 3. API 响应规范

### 统一响应格式 (Result<T>)
```java
{
  "code": 200,        // 状态码
  "message": "success", // 消息
  "data": {}          // 数据
}
```

### 状态码约定
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 禁止访问
- `500` - 服务器内部错误

## 4. 前端编码规范 (Vue 2)

### 组件结构
```vue
<template>
  <!-- 模板 -->
</template>

<script>
export default {
  name: 'XxxComponent',
  data() { return {} },
  methods: {},
  created() {}
}
</script>

<style scoped>
/* 样式 */
</style>
```

### 文件命名
- 组件: `Xxx.vue` (大驼峰)
- 工具类: `request.js`, `xxx.js`
- 路由: `index.js`

### 目录结构
```
src/
├── views/          # 页面组件
├── router/        # 路由配置
├── utils/          # 工具函数
└── assets/         # 静态资源
```

## 5. Git 提交规范

### 提交信息格式
```
<type>: <subject>

<body>
```

### Type 类型
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档变更
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具变更

## 6. 数据库规范

### 表命名
- 使用下划线分隔 - 例: `sys_user`, `sale_trade_record`
- 保持简洁，控制在30字符以内

### 字段命名
- 使用下划线命名 - 例: `user_name`, `create_time`
- 主键: `id`
- 创建时间: `create_time`
- 更新时间: `update_time`
- 逻辑删除: `deleted`

## 7. 配置文件规范

### application.yml
```yaml
server:
  port: 8080

spring:
  application:
    name: {模块名}
  datasource:
    url: jdbc:mysql://localhost:3306/{db}
    username: {user}
    password: {password}
  redis:
    host: localhost
    port: 6379
```

## 8. 注释规范

### 类注释
```java
/**
 * 描述类功能
 *
 * @author author
 * @date 2024-01-01
 */
```

### 方法注释
```java
/**
 * 方法描述
 *
 * @param param1 参数1描述
 * @return 返回值描述
 */
public Result<String> method(String param1) {}
```