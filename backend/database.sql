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
('茄子', '蔬菜', '紫皮', 200, 'kg', 2.20),
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
('茄子', '产品', '蔬菜', '紫皮', 200, 'kg'),
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
