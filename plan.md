# Lingma 项目文档

## 1. 项目概述

**项目名称**: central  
**项目类型**: Maven 多模块微服务项目 + Vue 前端  
**版本**: 1.0.0-SNAPSHOT

## 2. 技术栈

### 后端
- **Java**: 17
- **框架**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **数据库**: MySQL 8.0.33, MyBatis 3.0.3
- **缓存**: Redis (Spring Data Redis)
- **工具库**: Lombok 1.18.30, EasyExcel 3.3.3
- **认证**: JWT (JWTUtil in security模块)

### 前端
- **框架**: Vue 2.6.14
- **UI库**: Element UI 2.15.14
- **HTTP客户端**: Axios 0.27.2
- **路由**: Vue Router 3.5.4
- **构建工具**: Webpack 5

## 3. 项目结构

```
Lingma/
├── pom.xml                    # 父POM, 管理依赖版本
├── common/                    # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/central/common/
│       ├── config/           # Redis配置
│       ├── entity/            # 公共实体
│       ├── result/            # 统一响应Result
│       └── util/              # 工具类(RedisUtil)
├── user/                      # 用户服务模块
│   ├── pom.xml
│   └── src/main/java/com/central/user/
│       ├── controller/
│       ├── mapper/
│       └── service/
├── sale/                      # 销售/交易模块
│   ├── pom.xml
│   └── src/main/java/com/central/sale/
│       ├── controller/
│       ├── dto/
│       ├── mapper/
│       └── service/
├── security/                  # 安全认证模块
│   ├── pom.xml
│   └── src/main/java/com/central/security/
│       ├── config/            # SecurityConfig
│       ├── controller/        # AuthController
│       ├── dto/               # LoginRequest, LoginResponse
│       ├── feign/             # Feign客户端
│       └── util/              # JwtUtil
├── sale-frontend/             # 前端模块
│   ├── package.json
│   └── src/
│       ├── App.vue
│       ├── main.js
│       ├── router/
│       ├── utils/             # request.js
│       └── views/             # 页面组件
└── sql/                       # SQL脚本
```

## 4. 模块说明

### common 模块
- 提供公共配置、实体类、工具类
- Result 统一响应包装
- Redis 配置和工具类

### user 模块
- 用户管理服务
- 提供用户 CRUD 操作

### sale 模块
- 交易/销售相关业务
- DTO: ProductPriceQueryDTO, TradeRecordQueryDTO, PageResult, ProductHoldingInfo

### security 模块
- 认证授权服务
- JWT 工具类
- Feign 远程调用客户端

### sale-frontend 模块
- Vue 2 前端应用
- 页面: Login, Home, Layout, TradeRecordList, ProductList, PriceList

## 5. 关键文件

| 文件 | 说明 |
|------|------|
| `common/.../result/Result.java` | 统一API响应格式 |
| `common/.../util/RedisUtil.java` | Redis操作工具 |
| `security/.../util/JwtUtil.java` | JWT生成与验证 |
| `security/.../controller/AuthController.java` | 认证接口 |
| `sale-frontend/src/utils/request.js` | Axios请求封装 |

## 6. 开发规范

- 包命名: `com.central.{模块名}`
- Controller 层处理请求和响应
- Service 层处理业务逻辑
- Mapper 层进行数据库操作
- 使用 Lombok 减少样板代码
- 统一使用 Result<T> 包装返回值