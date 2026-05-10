-- 创建数据库
CREATE DATABASE IF NOT EXISTS agriculture_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE agriculture_db;

-- 农资表
CREATE TABLE IF NOT EXISTS materials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '农资名称',
    type VARCHAR(50) COMMENT '类型（化肥、农药、种子、农具）',
    spec VARCHAR(100) COMMENT '规格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    unit VARCHAR(20) COMMENT '单位',
    supplier VARCHAR(100) COMMENT '供应商',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 采购记录表
CREATE TABLE IF NOT EXISTS purchases (
    id VARCHAR(20) PRIMARY KEY COMMENT '采购编号',
    material_name VARCHAR(100) NOT NULL COMMENT '农资名称',
    quantity INT NOT NULL COMMENT '采购数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    supplier VARCHAR(100) COMMENT '供应商',
    purchase_date DATE COMMENT '采购日期',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 产品表
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '产品名称',
    type VARCHAR(50) COMMENT '类型（蔬菜、水果、谷物、其他）',
    spec VARCHAR(100) COMMENT '规格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) COMMENT '单价',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 销售记录表
CREATE TABLE IF NOT EXISTS sales (
    id VARCHAR(20) PRIMARY KEY COMMENT '销售编号',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    quantity INT NOT NULL COMMENT '销售数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    customer VARCHAR(100) COMMENT '客户',
    sale_date DATE COMMENT '销售日期',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 库存表（统一视图）
CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '名称',
    type VARCHAR(50) COMMENT '类型（农资/产品）',
    category VARCHAR(50) COMMENT '类别',
    spec VARCHAR(100) COMMENT '规格',
    stock INT DEFAULT 0 COMMENT '库存数量',
    unit VARCHAR(20) COMMENT '单位',
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name_type (name, type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据 - 农资
INSERT INTO materials (name, type, spec, stock, unit, supplier) VALUES
('尿素', '化肥', '46%含量', 500, 'kg', '农业物资公司'),
('复合肥', '化肥', 'NPK 15-15-15', 300, 'kg', '农业物资公司'),
('杀虫剂', '农药', '100ml/瓶', 100, '瓶', '植保公司'),
('西红柿种子', '种子', '杂交种', 50, '袋', '种业公司'),
('锄头', '农具', '铁制', 20, '把', '农具厂');

-- 插入测试数据 - 产品
INSERT INTO products (name, type, spec, stock, unit, price) VALUES
('西红柿', '蔬菜', '新鲜', 500, 'kg', 3.50),
('黄瓜', '蔬菜', '新鲜', 300, 'kg', 2.80),
('茄子', '蔬菜', '新鲜', 200, 'kg', 2.20),
('玉米', '谷物', '新鲜', 1000, 'kg', 1.80),
('小麦', '谷物', '优质', 2000, 'kg', 1.50);

-- 插入测试数据 - 采购记录
INSERT INTO purchases (id, material_name, quantity, unit_price, total_price, supplier, purchase_date) VALUES
('P20260425001', '尿素', 200, 2.50, 500.00, '农业物资公司', '2026-04-25'),
('P20260420002', '复合肥', 150, 3.00, 450.00, '农业物资公司', '2026-04-20'),
('P20260415003', '杀虫剂', 50, 15.00, 750.00, '植保公司', '2026-04-15');

-- 插入测试数据 - 销售记录
INSERT INTO sales (id, product_name, quantity, unit_price, total_price, customer, sale_date) VALUES
('S20260425001', '西红柿', 100, 3.50, 350.00, '超市A', '2026-04-25'),
('S20260424002', '黄瓜', 150, 2.80, 420.00, '超市B', '2026-04-24'),
('S20260423003', '玉米', 500, 1.80, 900.00, '饲料厂', '2026-04-23');

-- 插入测试数据 - 库存
INSERT INTO inventory (name, type, category, spec, stock, unit) VALUES
('尿素', '农资', '化肥', '46%含量', 500, 'kg'),
('复合肥', '农资', '化肥', 'NPK 15-15-15', 300, 'kg'),
('杀虫剂', '农资', '农药', '100ml/瓶', 100, '瓶'),
('西红柿种子', '农资', '种子', '杂交种', 50, '袋'),
('锄头', '农资', '农具', '铁制', 20, '把'),
('西红柿', '产品', '蔬菜', '新鲜', 500, 'kg'),
('黄瓜', '产品', '蔬菜', '新鲜', 300, 'kg'),
('茄子', '产品', '蔬菜', '新鲜', 200, 'kg'),
('玉米', '产品', '谷物', '新鲜', 1000, 'kg'),
('小麦', '产品', '谷物', '优质', 2000, 'kg');

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) COMMENT '姓名',
    role VARCHAR(50) DEFAULT '普通用户' COMMENT '角色',
    status VARCHAR(20) DEFAULT '启用' COMMENT '状态（启用/禁用）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE users MODIFY COLUMN avatar TEXT COMMENT '头像路径';

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    description VARCHAR(200) COMMENT '描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据 - 角色
INSERT INTO roles (name, description) VALUES
('管理员', '系统管理员，拥有所有权限'),
('普通用户', '普通用户，拥有基本操作权限');

-- 插入测试数据 - 用户（密码加密使用简单方式，实际应使用BCrypt）
INSERT INTO users (username, password, name, role, status) VALUES
('admin', '123456', '管理员', '管理员', '启用'),
('user1', '123456', '用户1', '普通用户', '启用'),
('user2', '123456', '用户2', '普通用户', '禁用');

-- 植物识别记录表
CREATE TABLE IF NOT EXISTS plant_recognition (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image_name VARCHAR(200) COMMENT '图片名称',
    image_path VARCHAR(500) COMMENT '图片路径',
    recognition_result VARCHAR(200) COMMENT '识别结果',
    confidence DECIMAL(5,4) COMMENT '置信度',
    recognize_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '识别时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS farmland (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '农田名称',
    area VARCHAR(50) COMMENT '区域（东区、西区、南区、北区）',
    acreage DECIMAL(10,2) COMMENT '面积（亩）',
    soil_type VARCHAR(50) COMMENT '土壤类型',
    crop VARCHAR(100) COMMENT '种植作物',
    status VARCHAR(20) DEFAULT '种植中' COMMENT '状态（种植中/休耕）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO farmland (name, area, acreage, soil_type, crop, status) VALUES
('大棚A', '东区', 50, '沙壤土', '西红柿', '种植中'),
('大棚B', '东区', 45, '壤土', '黄瓜', '种植中'),
('大棚C', '西区', 60, '黏土', '茄子', '种植中'),
('露天农田1', '南区', 120, '沙壤土', '玉米', '种植中'),
('露天农田2', '北区', 100, '壤土', '小麦', '休耕');

CREATE TABLE IF NOT EXISTS expert (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    name VARCHAR(100) COMMENT '姓名',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    specialty VARCHAR(200) COMMENT '专业领域',
    status VARCHAR(20) DEFAULT '启用' COMMENT '状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO expert (username, password, name, phone, email, specialty) VALUES
('expert1', '123456', '张专家', '13800138001', 'zhang@example.com', '病虫害防治'),
('expert2', '123456', '李专家', '13800138002', 'li@example.com', '土壤改良');

INSERT INTO roles (name, description) VALUES ('专家', '专家角色');

ALTER TABLE expert ADD COLUMN avatar VARCHAR(500) COMMENT '头像路径';

CREATE TABLE IF NOT EXISTS consultations (
    id VARCHAR(50) PRIMARY KEY COMMENT '咨询编号',
    expert_id INT COMMENT '专家ID',
    expert_name VARCHAR(100) COMMENT '专家姓名',
    user_name VARCHAR(100) COMMENT '用户姓名',
    subject VARCHAR(200) COMMENT '咨询主题',
    status VARCHAR(20) DEFAULT '待处理' COMMENT '状态',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (expert_id) REFERENCES expert(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consultation_id VARCHAR(50) COMMENT '咨询编号',
    sender VARCHAR(20) COMMENT '发送者',
    content TEXT COMMENT '消息内容',
    send_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (consultation_id) REFERENCES consultations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 活动记录表
CREATE TABLE IF NOT EXISTS activities (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) COMMENT '类型（环境监测、专家咨询、农资采购、产品出售、农田管理、系统管理）',
    content VARCHAR(500) COMMENT '活动内容',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据 - 活动记录
INSERT INTO activities (type, content, create_time) VALUES
('环境监测', '大棚A温度正常', '2026-05-07 08:30:00'),
('环境监测', '大棚B湿度正常', '2026-05-07 08:25:00'),
('专家咨询', '病虫害防治咨询已提交', '2026-05-07 08:20:00'),
('农资采购', '尿素采购单已创建', '2026-05-07 08:15:00'),
('产品出售', '西红柿销售订单已完成', '2026-05-07 08:10:00'),
('农田管理', '农田信息已更新', '2026-05-07 08:05:00'),
('环境监测', '大棚C光照正常', '2026-05-07 07:55:00'),
('系统管理', '用户登录成功', '2026-05-07 07:30:00');

-- 灌溉设备表
CREATE TABLE IF NOT EXISTS irrigation_devices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    farmland_id INT NOT NULL COMMENT '所属农田ID',
    device_type VARCHAR(50) COMMENT '设备类型（喷灌/滴灌/漫灌）',
    status VARCHAR(20) DEFAULT '正常' COMMENT '设备状态（正常/故障/维护中）',
    water_flow DECIMAL(10,2) COMMENT '水流量(升/分钟)',
    coverage_area DECIMAL(10,2) COMMENT '覆盖面积(亩)',
    install_date DATE COMMENT '安装日期',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmland_id) REFERENCES farmland(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 灌溉计划表
CREATE TABLE IF NOT EXISTS irrigation_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    farmland_id INT NOT NULL COMMENT '农田ID',
    device_id INT COMMENT '设备ID',
    plan_name VARCHAR(100) COMMENT '计划名称',
    start_time TIME COMMENT '开始时间',
    duration INT COMMENT '持续时间(分钟)',
    water_amount DECIMAL(10,2) COMMENT '用水量(升)',
    frequency VARCHAR(20) COMMENT '频率（每天/每周/自定义）',
    week_days VARCHAR(50) COMMENT '执行日期（1,2,3...7代表周一到周日）',
    status VARCHAR(20) DEFAULT '启用' COMMENT '状态（启用/禁用）',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (farmland_id) REFERENCES farmland(id),
    FOREIGN KEY (device_id) REFERENCES irrigation_devices(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 灌溉记录表
CREATE TABLE IF NOT EXISTS irrigation_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    farmland_id INT NOT NULL COMMENT '农田ID',
    device_id INT COMMENT '设备ID',
    plan_id INT COMMENT '计划ID（手动灌溉为空）',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    duration INT COMMENT '持续时间(分钟)',
    water_amount DECIMAL(10,2) COMMENT '用水量(升)',
    type VARCHAR(20) COMMENT '类型（自动/手动）',
    status VARCHAR(20) DEFAULT '已完成' COMMENT '状态（进行中/已完成/已取消）',
    operator VARCHAR(50) COMMENT '操作人',
    remark TEXT COMMENT '备注',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (farmland_id) REFERENCES farmland(id),
    FOREIGN KEY (device_id) REFERENCES irrigation_devices(id),
    FOREIGN KEY (plan_id) REFERENCES irrigation_plans(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据 - 灌溉设备
INSERT INTO irrigation_devices (name, farmland_id, device_type, status, water_flow, coverage_area, install_date) VALUES
('大棚A喷灌系统', 1, '喷灌', '正常', 50.0, 50, '2026-01-15'),
('大棚B滴灌系统', 2, '滴灌', '正常', 30.0, 45, '2026-01-20'),
('大棚C漫灌系统', 3, '漫灌', '正常', 80.0, 60, '2026-02-01'),
('露天农田1喷灌', 4, '喷灌', '正常', 100.0, 120, '2026-02-10');

-- 插入测试数据 - 灌溉计划
INSERT INTO irrigation_plans (farmland_id, device_id, plan_name, start_time, duration, water_amount, frequency, week_days, status) VALUES
(1, 1, '大棚A日常灌溉', '08:00:00', 30, 1500.0, '每天', '1,2,3,4,5,6,7', '启用'),
(2, 2, '大棚B定期灌溉', '09:00:00', 25, 750.0, '每周', '1,3,5', '启用'),
(4, 4, '露天农田灌溉计划', '07:00:00', 45, 4500.0, '每天', '1,2,3,4,5,6,7', '启用');

-- 插入测试数据 - 灌溉记录
INSERT INTO irrigation_records (farmland_id, device_id, plan_id, start_time, end_time, duration, water_amount, type, status, operator, remark) VALUES
(1, 1, 1, '2026-05-07 08:00:00', '2026-05-07 08:30:00', 30, 1500.0, '自动', '已完成', '系统', '按计划执行'),
(2, 2, 2, '2026-05-07 09:00:00', '2026-05-07 09:25:00', 25, 750.0, '自动', '已完成', '系统', '按计划执行'),
(4, 4, 3, '2026-05-07 07:00:00', '2026-05-07 07:45:00', 45, 4500.0, '自动', '已完成', '系统', '按计划执行'),
(1, 1, NULL, '2026-05-06 16:30:00', '2026-05-06 17:00:00', 30, 1500.0, '手动', '已完成', '管理员', '补充灌溉');

USE agriculture_db;

-- 为users表添加farmlands字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS farmlands TEXT COMMENT '管理的农田ID（逗号分隔）';

-- 监测点表
CREATE TABLE IF NOT EXISTS monitor_points (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '监测点名称',
    location VARCHAR(200) COMMENT '位置',
    farmland_id INT COMMENT '所属农田ID',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态（正常/警告/异常）',
    temperature DECIMAL(5,1) DEFAULT 0 COMMENT '温度',
    humidity INT DEFAULT 0 COMMENT '湿度',
    light INT DEFAULT 0 COMMENT '光照(lux)',
    co2 INT DEFAULT 0 COMMENT 'CO2(ppm)',
    last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (farmland_id) REFERENCES farmland(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据 - 监测点
INSERT INTO monitor_points (name, location, farmland_id, status, temperature, humidity, light, co2) VALUES
('大棚A', '东区1号', 1, '正常', 24.0, 74, 8023, 422),
('大棚B', '东区2号', 2, '正常', 23.5, 72, 7850, 418),
('大棚C', '西区1号', 3, '正常', 25.2, 68, 8200, 425),
('露天农田', '南区', 4, '正常', 22.8, 65, 8500, 405);