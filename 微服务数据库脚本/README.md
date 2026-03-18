# 微服务数据库脚本说明

## 📚 概述

本目录包含分布式在线购物平台的**微服务数据库初始化脚本**，按照服务拆分原则，将数据库按照服务职责进行拆分，每个服务对应独立的数据库。

## 🏗️ 架构设计

### 数据库拆分

| 服务名称 | 数据库名 | 主要表 | 目录 |
|---------|---------|--------|------|
| 用户服务 | user_db | users, addresses | user-db/ |
| 商品服务 | product_db | categories, products, product_skus 等 | product-db/ |
| 订单服务 | order_db | carts, orders, order_items 等 | order-db/ |
| 支付服务 | payment_db | payments | payment-db/ |
| 物流服务 | shipment_db | shipments | shipment-db/ |
| 评论服务 | comment_db | comments, favorites | comment-db/ |
| 博客服务 | blog_db | blogs, blog_categories, blog_tags 等 | blog-db/ |
| 短信服务 | sms_db | sms_templates, sms_records | sms-db/ |
| 店铺服务 | store_db | stores, store_follows, store_ratings | store-db/ |

### 微服务架构特点

1. **数据库独立**：每个微服务使用独立的数据库，避免耦合
2. **无跨库外键**：移除跨服务的外键约束，改用逻辑关联
3. **服务间通信**：通过 API 调用或消息队列实现数据一致性
4. **可扩展性**：支持水平扩展和分库分表

## 📁 目录结构

```
微服务数据库脚本/
├── README.md                          # 本说明文件
├── db_config.properties               # 数据库配置文件
├── db_config.properties.example       # 数据库配置示例
├── db_init.py                         # Python 初始化脚本（推荐）
├── db_verify.py                       # Python 验证脚本（推荐）
├── requirements.txt                   # Python 依赖包列表
├── user-db/                           # 用户服务数据库
│   ├── 01_create_database.sql         # 创建数据库
│   ├── 02_tables.sql                  # 创建数据表
│   └── 03_init_data.sql               # 初始化数据
├── product-db/                        # 商品服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── order-db/                          # 订单服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── payment-db/                        # 支付服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── shipment-db/                       # 物流服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── comment-db/                        # 评论服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── blog-db/                           # 博客服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
├── sms-db/                            # 短信服务数据库
│   ├── 01_create_database.sql
│   ├── 02_tables.sql
│   └── 03_init_data.sql
└── store-db/                          # 店铺服务数据库
    ├── 01_create_database.sql
    ├── 02_tables.sql
    └── 03_init_data.sql
```

## 🚀 快速开始

### 第一次使用？按照以下步骤快速初始化数据库：

#### 1. 安装依赖

```bash
python -m pip install -r requirements.txt
```

#### 2. 配置数据库连接

编辑 `db_config.properties` 文件，修改 MySQL 密码：

```properties
db.password=你的 MySQL 密码
```

#### 3. 运行初始化脚本

```bash
python db_init.py
```

#### 4. 验证数据库

```bash
python db_verify.py
```

看到"✓ 所有数据库验证通过！"即表示初始化成功！

---

## 📝 执行方式

### 方式一：使用 Python 脚本（推荐）

**前提条件：**
- 安装 Python 3.6 或更高版本
- 安装 MySQL 服务器并启动

**1. 安装 Python 依赖**

```bash
python -m pip install -r requirements.txt
```

如果遇到"pip 不是可识别的命令"错误，请使用 `python -m pip` 而不是 `pip`。

**2. 配置数据库连接**

编辑 `db_config.properties`，修改数据库连接信息：

```properties
db.username=root
db.password=你的 MySQL 密码
```

**3. 执行数据库初始化**

运行初始化脚本：

```bash
python db_init.py
```

**4. 验证数据库**

运行验证脚本：

```bash
python db_verify.py
```

### 方式二：手动执行 SQL 脚本

如果需要手动执行 SQL 脚本，请按照以下顺序：

**对于每个服务数据库：**

```bash
# 1. 创建数据库
mysql -u root -p < user-db/01_create_database.sql

# 2. 创建数据表
mysql -u root -p user_db < user-db/02_tables.sql

# 3. 初始化数据（可选）
mysql -u root -p user_db < user-db/03_init_data.sql
```

**执行顺序：**

1. user-db
2. product-db
3. store-db（依赖 product-db）
4. order-db
5. payment-db
6. shipment-db
7. comment-db
8. blog-db
9. sms-db

---

## 🔧 配置说明

### db_config.properties

```properties
# 公共配置
db.driver=com.mysql.cj.jdbc.Driver
db.username=root
db.password=你的 MySQL 密码

# 各服务数据库 URL（一般不需要修改）
user.db.url=jdbc:mysql://localhost:3306/user_db
product.db.url=jdbc:mysql://localhost:3306/product_db
# ... 其他服务配置
```

---

## 📊 数据表说明

### user_db（用户服务）

| 表名 | 说明 |
|------|------|
| users | 用户信息表 |
| addresses | 用户地址表 |

### product_db（商品服务）

| 表名 | 说明 |
|------|------|
| categories | 商品分类表 |
| products | 商品信息表 |
| product_images | 商品图片表 |
| product_skus | 商品规格表 |
| product_attributes | 商品属性表 |
| product_attribute_values | 商品属性值表 |
| product_sku_attributes | 商品规格属性关联表 |
| seckill_activities | 秒杀活动表 |
| seckill_products | 秒杀商品表 |

### order_db（订单服务）

| 表名 | 说明 |
|------|------|
| carts | 购物车表 |
| cart_items | 购物车商品项表 |
| orders | 订单表 |
| order_items | 订单商品项表 |
| seckill_orders | 秒杀订单表 |

### payment_db（支付服务）

| 表名 | 说明 |
|------|------|
| payments | 支付记录表 |

### shipment_db（物流服务）

| 表名 | 说明 |
|------|------|
| shipments | 物流信息表 |

### comment_db（评论服务）

| 表名 | 说明 |
|------|------|
| comments | 商品评论表 |
| favorites | 用户收藏表 |

### blog_db（博客服务）

| 表名 | 说明 |
|------|------|
| blogs | 博客文章表 |
| blog_categories | 博客分类表 |
| blog_tags | 博客标签表 |
| blog_comments | 博客评论表 |

### sms_db（短信服务）

| 表名 | 说明 |
|------|------|
| sms_templates | 短信模板表 |
| sms_records | 短信发送记录表 |

### store_db（店铺服务）

| 表名 | 说明 |
|------|------|
| stores | 店铺信息表 |
| store_follows | 店铺关注表 |
| store_ratings | 店铺评分表 |

---

## ⚠️ 重要说明

### 1. 跨服务数据一致性

由于移除了跨服务的外键约束，需要在应用层保证数据一致性：

- 使用分布式事务（如 Saga 模式）
- 通过消息队列实现最终一致性
- 在代码层进行数据验证

### 2. 服务间调用

服务间的数据关联通过以下方式实现：

- **REST API**：同步调用
- **消息队列**：异步通知
- **数据冗余**：在订单表中存储用户 ID、店铺 ID 等

### 3. 初始化数据

`03_init_data.sql` 包含测试数据，生产环境请根据实际情况修改或删除。

---

## 🛠️ 故障排查

### 问题 1：MySQL 连接失败

**解决方案：**
1. 确保 MySQL 服务已启动
2. 检查 `db_config.properties` 中的用户名和密码
3. 确认 MySQL 端口（默认 3306）未被防火墙阻止

### 问题 2：权限不足

**解决方案：**
```sql
-- 授予用户权限
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
```

### 问题 3：字符集问题

**解决方案：**
确保 MySQL 服务器配置了 utf8mb4 字符集：
```ini
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
```

---

## 📝 更新日志

- **2026-03-18**：初始版本，完成 9 个微服务数据库设计
  - 移除跨服务外键约束
  - 添加 Python 初始化和验证脚本
  - 完善配置说明文档

---

## 📄 许可证

本数据库脚本为项目内部使用。

---

## 📞 联系方式

如有问题，请联系开发团队。
