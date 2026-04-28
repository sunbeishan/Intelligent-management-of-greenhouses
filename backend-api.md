# 智慧农业管理系统后端API接口文档

## 1. 认证管理

### 1.1 用户登录
- **路径**: `/api/auth/login`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "username": "string",
    "password": "string",
    "type": "string"  // user 或 expert
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "token": "string",
      "user": {
        "id": "number",
        "username": "string",
        "name": "string",
        "role": "string"
      }
    },
    "message": "登录成功"
  }
  ```

### 1.2 退出登录
- **路径**: `/api/auth/logout`
- **方法**: POST
- **响应**:
  ```json
  {
    "code": 200,
    "message": "退出成功"
  }
  ```

### 1.3 刷新Token
- **路径**: `/api/auth/refresh`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "token": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "token": "string"
    },
    "message": "刷新成功"
  }
  ```

## 2. 统计分析

### 2.1 数据概览
- **路径**: `/api/statistics/overview`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "totalFarmArea": 1250,
      "monitorPoints": 32,
      "monthlyYield": 85,
      "expertConsultations": 12
    }
  }
  ```

### 2.2 产量趋势
- **路径**: `/api/statistics/yield-trend`
- **方法**: GET
- **请求参数**:
  - `days`: 天数 (7, 30, 90)
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "dates": ["4/20", "4/21", ...],
      "values": [2.5, 2.3, ...]
    }
  }
  ```

### 2.3 环境指标
- **路径**: `/api/statistics/environment-indicators`
- **方法**: GET
- **请求参数**:
  - `days`: 天数 (7, 30, 90)
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "temperature": {
        "dates": ["4/20", "4/21", ...],
        "values": [25.5, 26.1, ...]
      },
      "humidity": {
        "dates": ["4/20", "4/21", ...],
        "values": [65, 63, ...]
      }
    }
  }
  ```

### 2.4 最近活动
- **路径**: `/api/statistics/recent-activities`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "time": "2026-04-26 14:30",
        "type": "环境监测",
        "content": "大棚A温度异常，已发送预警"
      },
      ...
    ]
  }
  ```

## 3. 环境监测

### 3.1 实时环境数据
- **路径**: `/api/environment/real-time`
- **方法**: GET
- **请求参数**:
  - `monitorPoint`: 监测点ID
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "temperature": 25.5,
      "temperatureStatus": "正常",
      "humidity": 65,
      "humidityStatus": "正常",
      "light": 8000,
      "lightStatus": "正常",
      "co2": 450,
      "co2Status": "正常"
    }
  }
  ```

### 3.2 历史环境数据
- **路径**: `/api/environment/history`
- **方法**: GET
- **请求参数**:
  - `indicator`: 指标类型 (temperature, humidity, light, co2)
  - `startDate`: 开始日期 (YYYY-MM-DD)
  - `endDate`: 结束日期 (YYYY-MM-DD)
  - `monitorPoint`: 监测点ID
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "dates": ["4/20", "4/21", ...],
      "values": [25.5, 26.1, ...]
    }
  }
  ```

### 3.3 监测点列表
- **路径**: `/api/environment/points`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "name": "大棚A",
        "location": "东区1号",
        "status": "正常",
        "lastUpdate": "2026-04-26 14:30"
      },
      ...
    ]
  }
  ```

### 3.4 新增监测点
- **路径**: `/api/environment/points`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "name": "string",
    "location": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "大棚A",
      "location": "东区1号"
    },
    "message": "添加成功"
  }
  ```

### 3.5 更新监测点
- **路径**: `/api/environment/points/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "location": "string",
    "status": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 3.6 删除监测点
- **路径**: `/api/environment/points/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

## 4. 农田信息管理

### 4.1 农田列表
- **路径**: `/api/farms`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `name`: 农田名称
  - `area`: 区域
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 5,
      "list": [
        {
          "id": 1,
          "name": "大棚A",
          "area": "东区",
          "areaSize": 50,
          "soilType": "沙壤土",
          "crop": "西红柿",
          "status": "种植中"
        },
        ...
      ]
    }
  }
  ```

### 4.2 新增农田
- **路径**: `/api/farms`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "name": "string",
    "area": "string",
    "areaSize": 50,
    "soilType": "string",
    "crop": "string",
    "status": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "大棚A",
      "areaSize": 50
    },
    "message": "添加成功"
  }
  ```

### 4.3 更新农田
- **路径**: `/api/farms/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "area": "string",
    "areaSize": 50,
    "soilType": "string",
    "crop": "string",
    "status": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 4.4 删除农田
- **路径**: `/api/farms/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 4.5 导出农田数据
- **路径**: `/api/farms/export`
- **方法**: GET
- **响应**: 文件流 (Excel)

### 4.6 导入农田数据
- **路径**: `/api/farms/import`
- **方法**: POST
- **请求参数**: `multipart/form-data` (file)
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "success": 5,
      "failed": 0
    },
    "message": "导入成功"
  }
  ```

## 5. 专家咨询

### 5.1 专家列表
- **路径**: `/api/experts`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "name": "张教授",
        "title": "农业专家",
        "specialty": "病虫害防治",
        "avatar": "string"
      },
      ...
    ]
  }
  ```

### 5.2 发起咨询
- **路径**: `/api/consultations`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "expertId": 1,
    "subject": "string",
    "content": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": "C20260426001",
      "subject": "string",
      "status": "待处理"
    },
    "message": "咨询成功"
  }
  ```

### 5.3 用户咨询记录
- **路径**: `/api/consultations`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `status`: 状态
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 2,
      "list": [
        {
          "id": "C20260426001",
          "expertName": "张教授",
          "subject": "病虫害防治",
          "status": "处理中",
          "createTime": "2026-04-26 11:15"
        },
        ...
      ]
    }
  }
  ```

### 5.4 咨询详情
- **路径**: `/api/consultations/{id}`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": "C20260426001",
      "expertId": 1,
      "expertName": "张教授",
      "subject": "病虫害防治",
      "status": "处理中",
      "createTime": "2026-04-26 11:15",
      "messages": [
        {
          "sender": "user",
          "content": "我的西红柿叶子发黄，怎么回事？",
          "time": "2026-04-26 11:15"
        },
        ...
      ]
    }
  }
  ```

### 5.5 发送消息
- **路径**: `/api/consultations/{id}/messages`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "content": "string",
    "sender": "string"  // user 或 expert
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "content": "string",
      "time": "2026-04-26 11:30"
    },
    "message": "发送成功"
  }
  ```

### 5.6 更新咨询状态
- **路径**: `/api/consultations/{id}/status`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "status": "string"  // 待处理, 处理中, 已回复
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "状态更新成功"
  }
  ```

### 5.7 专家待回复咨询
- **路径**: `/api/experts/{id}/consultations`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `status`: 状态
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 2,
      "list": [
        {
          "id": "C20260426001",
          "userName": "农户A",
          "subject": "病虫害防治",
          "status": "待处理",
          "createTime": "2026-04-26 11:15"
        },
        ...
      ]
    }
  }
  ```

## 6. 农资管理

### 6.1 农资库存
- **路径**: `/api/materials`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `name`: 农资名称
  - `type`: 类型
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 5,
      "list": [
        {
          "id": 1,
          "name": "尿素",
          "type": "化肥",
          "spec": "46%含量",
          "stock": 500,
          "unit": "kg",
          "supplier": "农业物资公司"
        },
        ...
      ]
    }
  }
  ```

### 6.2 新增农资
- **路径**: `/api/materials`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "name": "string",
    "type": "string",
    "spec": "string",
    "stock": 500,
    "unit": "string",
    "supplier": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "尿素",
      "stock": 500
    },
    "message": "添加成功"
  }
  ```

### 6.3 更新农资
- **路径**: `/api/materials/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "type": "string",
    "spec": "string",
    "stock": 500,
    "unit": "string",
    "supplier": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 6.4 删除农资
- **路径**: `/api/materials/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 6.5 采购记录
- **路径**: `/api/purchases`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `startDate`: 开始日期
  - `endDate`: 结束日期
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 3,
      "list": [
        {
          "id": "P20260425001",
          "materialName": "尿素",
          "quantity": 200,
          "unitPrice": 2.5,
          "totalPrice": 500,
          "supplier": "农业物资公司",
          "purchaseDate": "2026-04-25"
        },
        ...
      ]
    }
  }
  ```

### 6.6 新增采购记录
- **路径**: `/api/purchases`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "materialId": 1,
    "quantity": 200,
    "unitPrice": 2.5,
    "supplier": "string",
    "purchaseDate": "2026-04-25"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": "P20260425001",
      "materialName": "尿素",
      "quantity": 200
    },
    "message": "采购成功"
  }
  ```

## 7. 产品管理

### 7.1 产品库存
- **路径**: `/api/products`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `name`: 产品名称
  - `type`: 类型
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 3,
      "list": [
        {
          "id": 1,
          "name": "西红柿",
          "type": "蔬菜",
          "stock": 1000,
          "unit": "kg",
          "price": 3.5
        },
        ...
      ]
    }
  }
  ```

### 7.2 新增产品
- **路径**: `/api/products`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "name": "string",
    "type": "string",
    "stock": 1000,
    "unit": "string",
    "price": 3.5
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "西红柿",
      "stock": 1000
    },
    "message": "添加成功"
  }
  ```

### 7.3 更新产品
- **路径**: `/api/products/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "type": "string",
    "stock": 1000,
    "unit": "string",
    "price": 3.5
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 7.4 删除产品
- **路径**: `/api/products/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 7.5 销售记录
- **路径**: `/api/sales`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `startDate`: 开始日期
  - `endDate`: 结束日期
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 3,
      "list": [
        {
          "id": "S20260425001",
          "productName": "西红柿",
          "quantity": 100,
          "unitPrice": 3.5,
          "totalPrice": 350,
          "customer": "超市A",
          "saleDate": "2026-04-25"
        },
        ...
      ]
    }
  }
  ```

### 7.6 新增销售记录
- **路径**: `/api/sales`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "productId": 1,
    "quantity": 100,
    "unitPrice": 3.5,
    "customer": "string",
    "saleDate": "2026-04-25"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": "S20260425001",
      "productName": "西红柿",
      "quantity": 100
    },
    "message": "销售成功"
  }
  ```

## 8. 系统管理

### 8.1 用户列表
- **路径**: `/api/users`
- **方法**: GET
- **请求参数**:
  - `page`: 页码
  - `size`: 每页大小
  - `keyword`: 关键词
  - `role`: 角色
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "total": 10,
      "list": [
        {
          "id": 1,
          "username": "admin",
          "name": "管理员",
          "role": "admin",
          "status": "启用"
        },
        ...
      ]
    }
  }
  ```

### 8.2 新增用户
- **路径**: `/api/users`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "username": "string",
    "password": "string",
    "name": "string",
    "role": "string",
    "status": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "username": "admin",
      "name": "管理员"
    },
    "message": "添加成功"
  }
  ```

### 8.3 更新用户
- **路径**: `/api/users/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "role": "string",
    "status": "string",
    "password": "string"  // 可选
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 8.4 删除用户
- **路径**: `/api/users/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 8.5 角色列表
- **路径**: `/api/roles`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "name": "admin",
        "description": "管理员"
      },
      ...
    ]
  }
  ```

### 8.6 新增角色
- **路径**: `/api/roles`
- **方法**: POST
- **请求参数**:
  ```json
  {
    "name": "string",
    "description": "string",
    "permissions": ["string"]
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "id": 1,
      "name": "admin"
    },
    "message": "添加成功"
  }
  ```

### 8.7 更新角色
- **路径**: `/api/roles/{id}`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "description": "string",
    "permissions": ["string"]
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "更新成功"
  }
  ```

### 8.8 删除角色
- **路径**: `/api/roles/{id}`
- **方法**: DELETE
- **响应**:
  ```json
  {
    "code": 200,
    "message": "删除成功"
  }
  ```

### 8.9 系统设置
- **路径**: `/api/settings`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "systemName": "智慧农业管理系统",
      "logo": "string",
      "theme": "light",
      "notification": true
    }
  }
  ```

### 8.10 更新系统设置
- **路径**: `/api/settings`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "systemName": "string",
    "logo": "string",
    "theme": "string",
    "notification": true
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "设置更新成功"
  }
  ```

## 9. 个人中心

### 9.1 获取个人信息
- **路径**: `/api/profile`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "username": "admin",
      "name": "管理员",
      "email": "admin@example.com",
      "phone": "13800138000",
      "role": "管理员",
      "lastLogin": "2026-04-26 10:00"
    }
  }
  ```

### 9.2 更新个人信息
- **路径**: `/api/profile`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "name": "string",
    "email": "string",
    "phone": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "个人信息更新成功"
  }
  ```

### 9.3 修改密码
- **路径**: `/api/profile/password`
- **方法**: PUT
- **请求参数**:
  ```json
  {
    "oldPassword": "string",
    "newPassword": "string",
    "confirmPassword": "string"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "密码修改成功"
  }
  ```

## 10. 作物识别

### 10.1 上传图片识别
- **路径**: `/api/crop/identify`
- **方法**: POST
- **请求参数**: `multipart/form-data` (image)
- **响应**:
  ```json
  {
    "code": 200,
    "data": {
      "result": "西红柿",
      "confidence": 0.9856,
      "category": "蔬菜类",
      "description": "西红柿是茄科番茄属一年生或多年生草本植物，是常见的蔬菜作物。",
      "suggestions": [
        "适宜温度：20-25℃",
        "土壤要求：肥沃、排水良好",
        "种植密度：每亩2000-3000株"
      ]
    }
  }
  ```

### 10.2 识别历史记录
- **路径**: `/api/crop/history`
- **方法**: GET
- **响应**:
  ```json
  {
    "code": 200,
    "data": [
      {
        "id": 1,
        "image": "string",
        "result": "西红柿",
        "confidence": 0.9856,
        "category": "蔬菜类",
        "identifyTime": "2026-04-26 15:00"
      },
      ...
    ]
  }
  ```

## 11. 响应格式

所有API接口的响应格式统一为：

```json
{
  "code": 200,  // 状态码
  "data": {},  // 数据（可选）
  "message": "操作成功"  // 消息
}
```

## 12. 错误码

| 错误码 | 描述 |
|-------|------|
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 13. 技术实现建议

1. **框架选择**：Spring Boot 3.x + Spring Security 6.x + MyBatis-Plus
2. **数据库**：MySQL 8.0+
3. **认证**：JWT
4. **API文档**：SpringDoc OpenAPI 3.0
5. **缓存**：Redis（可选）
6. **跨域**：CORS配置
7. **日志**：SLF4J + Logback
8. **异常处理**：统一异常处理器

## 14. 注意事项

1. 所有接口返回统一的响应格式
2. 实现基于角色的访问控制（RBAC）
3. 对敏感操作进行日志记录
4. 实现数据验证，确保请求参数的合法性
5. 考虑接口性能，对查询操作进行优化
6. 实现事务管理，确保数据一致性
7. 对文件上传功能进行大小限制和类型验证
8. 实现限流和防攻击措施