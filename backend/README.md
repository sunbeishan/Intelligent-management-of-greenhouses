# 智慧农业管理系统 - Java后端

## 项目说明

这是智慧农业系统的后端服务，使用纯 Java SE 技术实现，为前端 Vue 项目提供完整的农业管理功能支持。

## 技术栈

- Java SE 21
- JDBC (MySQL Connector/J 8.0+)
- 内置 HTTP Server (com.sun.net.httpserver)

## 项目结构

```
backend/
├── src/
│   ├── db/
│   │   └── DatabaseConfig.java      # 数据库配置
│   ├── model/
│   │   ├── User.java                # 用户实体
│   │   ├── Role.java                # 角色实体
│   │   ├── Farmland.java            # 农田实体
│   │   ├── Material.java            # 农资实体
│   │   ├── Purchase.java            # 采购记录实体
│   │   ├── Product.java             # 产品实体
│   │   ├── Sale.java                # 销售记录实体
│   │   ├── Inventory.java           # 库存实体
│   │   ├── IrrigationDevice.java    # 灌溉设备实体
│   │   ├── IrrigationPlan.java      # 灌溉计划实体
│   │   ├── IrrigationRecord.java    # 灌溉记录实体
│   │   ├── Expert.java              # 专家实体
│   │   ├── Consultation.java        # 咨询实体
│   │   ├── Message.java             # 消息实体
│   │   ├── Activity.java            # 活动记录实体
│   │   └── PlantRecognition.java    # 植物识别实体
│   ├── dao/
│   │   ├── UserDAO.java              # 用户数据访问
│   │   ├── RoleDAO.java              # 角色数据访问
│   │   ├── FarmlandDAO.java          # 农田数据访问
│   │   ├── UserFarmlandDAO.java      # 用户农田关联数据访问
│   │   ├── MaterialDAO.java          # 农资数据访问
│   │   ├── PurchaseDAO.java          # 采购记录数据访问
│   │   ├── ProductDAO.java           # 产品数据访问
│   │   ├── SaleDAO.java              # 销售记录数据访问
│   │   ├── InventoryDAO.java         # 库存数据访问
│   │   ├── IrrigationDeviceDAO.java  # 灌溉设备数据访问
│   │   ├── IrrigationPlanDAO.java    # 灌溉计划数据访问
│   │   ├── IrrigationRecordDAO.java  # 灌溉记录数据访问
│   │   ├── ExpertDAO.java            # 专家数据访问
│   │   ├── ConsultationDAO.java      # 咨询数据访问
│   │   ├── MessageDAO.java           # 消息数据访问
│   │   ├── ActivityDAO.java          # 活动记录数据访问
│   │   └── PlantRecognitionDAO.java  # 植物识别数据访问
│   └── server/
│       ├── SimpleHttpServer.java     # HTTP服务器
│       ├── RegisterHandler.java      # 用户注册处理器
│       ├── LoginHandler.java         # 用户登录处理器
│       ├── UserHandler.java          # 用户管理处理器
│       ├── UserInfoHandler.java      # 用户信息处理器
│       ├── RoleHandler.java          # 角色管理处理器
│       ├── FarmlandHandler.java      # 农田管理处理器
│       ├── MaterialHandler.java      # 农资管理处理器
│       ├── PurchaseHandler.java      # 采购管理处理器
│       ├── ProductHandler.java       # 产品管理处理器
│       ├── SaleHandler.java          # 销售管理处理器
│       ├── InventoryHandler.java     # 库存管理处理器
│       ├── IrrigationDeviceHandler.java   # 灌溉设备处理器
│       ├── IrrigationPlanHandler.java     # 灌溉计划处理器
│       ├── IrrigationRecordHandler.java   # 灌溉记录处理器
│       ├── ExpertHandler.java        # 专家管理处理器
│       ├── ConsultationHandler.java  # 咨询管理处理器
│       ├── MessageHandler.java       # 消息处理器
│       ├── ActivityHandler.java      # 活动记录处理器
│       └── PlantRecognitionHandler.java   # 植物识别处理器
├── lib/                             # 依赖库
│   └── mysql-connector-java-8.0.xx.jar
├── out/                             # 编译输出
├── database.sql                     # 数据库初始化脚本
├── update_users_farmlands.sql       # 用户农田字段更新脚本
├── migrate_user_farmland.sql        # 数据迁移脚本
└── README.md                        # 说明文档
```

## API接口列表

### 用户管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/register` | POST | 用户注册 |
| `/api/login` | POST | 用户登录 |
| `/api/users` | GET | 获取用户列表 |
| `/api/users/{id}` | GET | 获取单个用户 |
| `/api/users/{id}` | PUT | 更新用户信息 |
| `/api/users/{id}` | DELETE | 删除用户 |
| `/api/user/info?username=xxx` | GET | 获取用户详细信息（含农田） |

### 角色管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/roles` | GET | 获取角色列表 |
| `/api/roles` | POST | 新增角色 |
| `/api/roles/{id}` | PUT | 更新角色 |
| `/api/roles/{id}` | DELETE | 删除角色 |

### 农田管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/farmland` | GET | 获取农田列表 |
| `/api/farmland` | POST | 新增农田 |
| `/api/farmland/{id}` | PUT | 更新农田 |
| `/api/farmland/{id}` | DELETE | 删除农田 |

### 农资管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/materials` | GET | 获取所有农资 |
| `/api/materials/{id}` | GET | 获取单个农资 |
| `/api/materials` | POST | 新增农资 |
| `/api/materials/{id}` | PUT | 更新农资 |
| `/api/materials/{id}` | DELETE | 删除农资 |

### 采购管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/purchases` | GET | 获取所有采购记录 |
| `/api/purchases` | POST | 新增采购记录（自动更新库存） |

### 产品管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/products` | GET | 获取所有产品 |
| `/api/products/{id}` | GET | 获取单个产品 |
| `/api/products` | POST | 新增产品 |
| `/api/products/{id}` | PUT | 更新产品 |
| `/api/products/{id}` | DELETE | 删除产品 |

### 销售管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/sales` | GET | 获取所有销售记录 |
| `/api/sales` | POST | 新增销售记录（自动更新库存） |

### 库存管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/inventory` | GET | 获取所有库存 |
| `/api/inventory?name=xxx&type=xxx&status=xxx` | GET | 搜索库存 |

### 灌溉设备管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/irrigation/devices` | GET | 获取设备列表 |
| `/api/irrigation/devices` | POST | 新增设备 |
| `/api/irrigation/devices/{id}` | PUT | 更新设备 |
| `/api/irrigation/devices/{id}` | DELETE | 删除设备 |

### 灌溉计划管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/irrigation/plans` | GET | 获取计划列表 |
| `/api/irrigation/plans?farmlandIds=1,2,3` | GET | 按农田ID过滤计划 |
| `/api/irrigation/plans` | POST | 新增计划 |
| `/api/irrigation/plans/{id}` | PUT | 更新计划 |
| `/api/irrigation/plans/{id}` | DELETE | 删除计划 |

### 灌溉记录管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/irrigation/records` | GET | 获取记录列表 |
| `/api/irrigation/records?farmlandIds=1,2,3` | GET | 按农田ID过滤记录 |
| `/api/irrigation/records` | POST | 新增记录 |
| `/api/irrigation/records/statistics?startDate=xxx&endDate=xxx` | GET | 获取统计数据 |

### 专家管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/experts` | GET | 获取专家列表 |
| `/api/experts` | POST | 新增专家 |
| `/api/experts/{id}` | PUT | 更新专家 |
| `/api/experts/{id}` | DELETE | 删除专家 |

### 咨询管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/consultations` | GET | 获取咨询列表 |
| `/api/consultations` | POST | 创建咨询 |
| `/api/consultations/{id}` | PUT | 更新咨询状态 |

### 消息管理

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/messages` | GET | 获取消息列表 |
| `/api/messages` | POST | 发送消息 |

### 活动记录

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/activities` | GET | 获取活动记录 |
| `/api/activities` | POST | 新增活动记录 |

### 植物识别

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/plant/recognize` | POST | 植物识别 |
| `/api/plant/records` | GET | 获取识别记录 |

## 使用步骤

### 1. 创建数据库

在 MySQL 中执行 database.sql 脚本：

```bash
mysql -u root -p < database.sql
```

### 2. 配置数据库连接

修改 `src/db/DatabaseConfig.java`：

```java
private static final String URL = "jdbc:mysql://localhost:3306/agriculture_db?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "123456";
```

### 3. 编译代码

```bash
cd backend
javac -d out -cp "lib/*" src/dao/*.java src/db/*.java src/model/*.java src/server/*.java
```

### 4. 运行服务器

```bash
java -cp "out;lib/*" server.SimpleHttpServer
```

服务器将在 8080 端口启动。

### 5. 配置前端代理

修改前端 vite.config.js，添加代理：

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 功能特性

### 用户权限控制
- 用户注册需管理员审批才能登录
- 管理员可访问所有模块
- 普通用户只能访问环境监测、作物识别、灌溉管理、专家咨询
- 用户只能查看和管理自己负责的农田数据

### 农田管理
- 用户可选择管理的农田
- 每个农田最多2人管理
- 农田信息直接存储在用户表中

### 灌溉管理
- 支持定时自动灌溉计划
- 灌溉记录统计
- 手动灌溉操作

### 数据隔离
- 基于用户角色的数据访问控制
- 农田级别的数据权限

## 数据库表结构

### 核心表

| 表名 | 说明 |
|------|------|
| users | 用户表（含农田字段） |
| roles | 角色表 |
| farmland | 农田表 |
| irrigation_devices | 灌溉设备表 |
| irrigation_plans | 灌溉计划表 |
| irrigation_records | 灌溉记录表 |
| expert | 专家表 |
| consultations | 咨询表 |
| messages | 消息表 |
| materials | 农资表 |
| products | 产品表 |
| purchases | 采购记录表 |
| sales | 销售记录表 |
| inventory | 库存表 |
| activities | 活动记录表 |
| plant_recognition | 植物识别记录表 |

## 许可证

MIT License