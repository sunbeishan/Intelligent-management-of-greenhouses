# 智慧农业系统

## 项目概述

智慧农业系统是一个基于Java后端和Vue前端的现代化农业管理平台，旨在帮助农业从业者高效管理农田、设备、灌溉计划等农业生产活动。

## 技术栈

### 后端
- Java 21
- MySQL 8.0+
- Java HTTP Server (内置)

### 前端
- Vue 3
- Element Plus
- ECharts
- Vite

## 功能模块

### 1. 统计分析
- 农田总面积统计
- 灌溉计划统计
- 专家咨询统计
- 环境指标趋势图表

### 2. 环境监测
- 实时温度、湿度、光照、CO2监测
- 历史数据查询与分析
- 监测点状态管理
- 权限控制：普通用户只能查看自己管理农田的数据

### 3. 作物识别
- 图片上传与识别
- 识别历史记录管理
- 识别结果展示

### 4. 农田信息管理
- 农田基本信息管理
- 农田状态管理
- 作物种植信息管理

### 5. 灌溉管理
- 设备管理（管理员专属）
- 灌溉计划管理
- 灌溉记录查询
- 手动灌溉操作
- 权限控制：普通用户只能管理自己负责农田的灌溉计划

### 6. 专家咨询
- 专家信息管理
- 咨询申请提交
- 消息通信
- 咨询状态跟踪

### 7. 农资管理
- 农资采购管理
- 产品出售管理
- 库存管理

### 8. 系统管理
- 用户管理（含审批功能）
- 角色管理
- 专家管理
- 系统设置

## 用户权限

### 管理员
- 可访问所有模块
- 可管理所有用户、农田、设备
- 可审批新用户注册

### 普通用户
- 注册后需管理员审批才能登录
- 只能访问：环境监测、作物识别、灌溉管理、专家咨询
- 只能查看和管理自己负责的农田数据

## 安装部署

### 环境要求
- JDK 21+
- MySQL 8.0+
- Node.js 20+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE agriculture_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库初始化脚本：
```bash
mysql -u root -p < backend/database.sql
```

### 后端启动

```bash
cd backend
javac -d out -cp "lib/*" src/dao/*.java src/db/*.java src/model/*.java src/server/*.java
java -cp "out:lib/*" server.SimpleHttpServer
```

后端服务将在 `http://localhost:8080` 启动。

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

## 登录信息

### 管理员账户
- 用户名：admin
- 密码：123456

### 测试用户
- 用户名：user1
- 密码：123456

## 项目结构

```
智慧农业系统/
├── backend/                    # 后端代码
│   ├── src/
│   │   ├── dao/               # 数据访问层
│   │   ├── db/                # 数据库配置
│   │   ├── model/             # 实体模型
│   │   └── server/            # 服务器处理
│   ├── lib/                   # 依赖库
│   ├── out/                   # 编译输出
│   ├── database.sql           # 数据库脚本
│   └── migrate_user_farmland.sql  # 数据迁移脚本
├── frontend/                   # 前端代码
│   ├── src/
│   │   ├── views/             # 页面组件
│   │   ├── stores/            # 状态管理
│   │   ├── router/            # 路由配置
│   │   └── App.vue            # 根组件
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## API接口

### 用户相关
- `POST /api/register` - 用户注册
- `POST /api/login` - 用户登录
- `GET /api/users` - 获取用户列表
- `GET /api/users/{id}` - 获取用户详情
- `PUT /api/users/{id}` - 更新用户信息
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/user/info?username={username}` - 获取用户详细信息

### 灌溉相关
- `GET /api/irrigation/plans` - 获取灌溉计划列表
- `POST /api/irrigation/plans` - 添加灌溉计划
- `PUT /api/irrigation/plans/{id}` - 更新灌溉计划
- `DELETE /api/irrigation/plans/{id}` - 删除灌溉计划
- `GET /api/irrigation/records` - 获取灌溉记录
- `POST /api/irrigation/records` - 添加灌溉记录
- `GET /api/irrigation/devices` - 获取设备列表

### 农田相关
- `GET /api/farmland` - 获取农田列表
- `POST /api/farmland` - 添加农田
- `PUT /api/farmland/{id}` - 更新农田
- `DELETE /api/farmland/{id}` - 删除农田

### 环境监测
- `GET /api/environment/data` - 获取环境数据
- `GET /api/environment/history` - 获取历史数据

### 作物识别
- `POST /api/plant/recognize` - 作物识别

### 专家咨询
- `GET /api/experts` - 获取专家列表
- `POST /api/consultations` - 创建咨询
- `GET /api/consultations` - 获取咨询列表
- `POST /api/messages` - 发送消息

## 特色功能

1. **用户审批机制**：新用户注册后需管理员审批才能登录
2. **农田权限控制**：用户可选择管理的农田，每个农田最多2人管理
3. **数据隔离**：普通用户只能查看和管理自己负责农田的数据
4. **自动灌溉**：支持定时自动灌溉计划
5. **实时监测**：环境数据实时监测与展示

## 开发说明

### 代码规范
- Java代码遵循阿里巴巴Java开发规范
- Vue代码使用Composition API
- 变量命名使用驼峰命名法

### 数据库设计
- 使用MySQL InnoDB引擎
- 表名使用小写+下划线命名
- 主键使用自增ID

## 许可证

MIT License

## 联系方式

如有问题或建议，请联系开发团队。