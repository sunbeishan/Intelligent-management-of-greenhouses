# 智慧农业管理系统 - Java后端

## 项目说明
这是一个使用纯 Java SE 技术实现的简单后端服务，用于支持前端 Vue 项目的农资采购、产品出售和库存管理功能。

## 技术栈
- Java SE 8+
- JDBC (MySQL Connector/J)
- 内置 HTTP Server (com.sun.net.httpserver)

## 项目结构
```
backend/
├── src/
│   ├── db/
│   │   └── DatabaseConfig.java      # 数据库配置
│   ├── model/
│   │   ├── Material.java            # 农资实体
│   │   ├── Purchase.java            # 采购记录实体
│   │   ├── Product.java             # 产品实体
│   │   ├── Sale.java                # 销售记录实体
│   │   └── Inventory.java           # 库存实体
│   ├── dao/
│   │   ├── MaterialDao.java         # 农资数据访问
│   │   ├── PurchaseDao.java         # 采购记录数据访问
│   │   ├── ProductDao.java          # 产品数据访问
│   │   ├── SaleDao.java             # 销售记录数据访问
│   │   └── InventoryDao.java        # 库存数据访问
│   └── server/
│       ├── SimpleHttpServer.java    # HTTP服务器
│       ├── MaterialHandler.java     # 农资API处理器
│       ├── PurchaseHandler.java     # 采购API处理器
│       ├── ProductHandler.java      # 产品API处理器
│       ├── SaleHandler.java         # 销售API处理器
│       └── InventoryHandler.java    # 库存API处理器
├── database.sql                     # 数据库初始化脚本
└── README.md                        # 说明文档
```

## API接口列表

### 农资管理
- `GET /api/materials` - 获取所有农资
- `GET /api/materials/{id}` - 获取单个农资
- `POST /api/materials` - 新增农资
- `PUT /api/materials/{id}` - 更新农资
- `DELETE /api/materials/{id}` - 删除农资

### 采购管理
- `GET /api/purchases` - 获取所有采购记录
- `POST /api/purchases` - 新增采购记录（自动更新库存）

### 产品管理
- `GET /api/products` - 获取所有产品
- `GET /api/products/{id}` - 获取单个产品
- `POST /api/products` - 新增产品
- `PUT /api/products/{id}` - 更新产品
- `DELETE /api/products/{id}` - 删除产品

### 销售管理
- `GET /api/sales` - 获取所有销售记录
- `POST /api/sales` - 新增销售记录（自动更新库存）

### 库存管理
- `GET /api/inventory` - 获取所有库存
- `GET /api/inventory?name=xxx&type=xxx&status=xxx` - 搜索库存

## 使用步骤

### 1. 创建数据库
在 MySQL 中执行 database.sql 脚本：
```bash
mysql -u root -p < database.sql
```

### 2. 下载 MySQL JDBC 驱动
下载 mysql-connector-java-8.0.xx.jar 并放到 backend/lib 目录

### 3. 编译代码
```bash
cd backend
javac -cp "lib/*" -d out src/db/*.java src/model/*.java src/dao/*.java src/server/*.java
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

## 数据库配置
在 `src/db/DatabaseConfig.java` 中修改：
```java
private static final String URL = "jdbc:mysql://localhost:3306/agriculture_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASSWORD = "123456789";
```
