# 传统分布式购物平台 - 数据库脚本说明

## 快速开始

**第一次使用？按照以下步骤快速初始化数据库：**

1. **安装依赖**
   ```bash
   python -m pip install -r requirements.txt
   ```

2. **配置数据库连接**
   
   编辑 `db_config.properties` 文件，修改 MySQL 密码：
   ```properties
   db.password=您的 MySQL 密码
   ```

3. **运行初始化脚本**
   ```bash
   python db_init.py
   ```

4. **验证数据库**
   ```bash
   python db_verify.py
   ```

看到"✓ 数据库验证通过！"即表示初始化成功！

---

## 目录结构

```
数据库脚本/
├── README.md                          # 本说明文件
├── 00_create_database.sql             # 创建数据库
├── 01_user_auth.sql                   # 用户与认证模块
├── 02_product_inventory.sql           # 商品与库存模块
├── 03_order_payment.sql               # 订单与支付模块
├── 04_logistics_comment.sql           # 物流与评论模块
├── 05_content_stats.sql               # 内容与统计模块
├── 06_store.sql                       # 店铺模块
├── 99_test_data.sql                   # 测试数据（可选）
├── verify_database.sql                # 数据库验证脚本
├── db_init.py                         # Python 初始化脚本（推荐）
├── db_verify.py                       # Python 验证脚本（推荐）
├── requirements.txt                   # Python 依赖包列表
└── db_config.properties.example       # 数据库配置示例
```

## 执行顺序

数据库脚本按照以下顺序执行：

1. **00_create_database.sql** - 创建数据库
2. **01_user_auth.sql** - 用户与认证模块（users, addresses, sms_templates, sms_records）
3. **02_product_inventory.sql** - 商品与库存模块（categories, products, product_images, product_skus 等）
4. **03_order_payment.sql** - 订单与支付模块（carts, cart_items, orders, order_items 等）
5. **04_logistics_comment.sql** - 物流与评论模块（shipments, comments, favorites）
6. **05_content_stats.sql** - 内容与统计模块（blogs, blog_categories, blog_tags, blog_comments, stats_records）
7. **06_store.sql** - 店铺模块（stores, store_follows, store_ratings）
8. **99_test_data.sql** - 测试数据（可选，用于开发和测试环境）

## 执行方式

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

复制配置文件并修改：
```bash
copy db_config.properties.example db_config.properties
```

编辑 `db_config.properties`，修改数据库连接信息：
```properties
db.url=jdbc:mysql://localhost:3306/shopping_platform_db
db.username=root
db.password=您的 MySQL 密码
```

**3. 执行数据库初始化**

运行初始化脚本：
```bash
python db_init.py
```

脚本会自动完成以下操作：
- 连接到 MySQL 服务器
- 创建数据库 `shopping_platform_db`
- 依次执行所有 SQL 脚本（00_create_database.sql 到 06_store.sql）
- 询问是否插入测试数据（输入 `y` 插入，输入 `n` 跳过）

**4. 验证数据库**

初始化完成后，运行验证脚本：
```bash
python db_verify.py
```

验证脚本会检查：
- 所有表是否存在
- 表总数是否正确（应为 30 个）
- 各模块表数量分布
- 默认数据是否存在
- 测试数据是否存在（如果选择了插入）

### 方式二：手动逐个执行 SQL 脚本

在 MySQL 命令行或 MySQL Workbench 中依次执行每个 SQL 文件：

```bash
# 1. 创建数据库
mysql -u root -p < 00_create_database.sql

# 2. 切换到数据库
mysql -u root -p shopping_platform_db

# 3. 执行各模块脚本
mysql -u root -p shopping_platform_db < 01_user_auth.sql
mysql -u root -p shopping_platform_db < 02_product_inventory.sql
mysql -u root -p shopping_platform_db < 03_order_payment.sql
mysql -u root -p shopping_platform_db < 04_logistics_comment.sql
mysql -u root -p shopping_platform_db < 05_content_stats.sql
mysql -u root -p shopping_platform_db < 06_store.sql

# 4. （可选）插入测试数据
mysql -u root -p shopping_platform_db < 99_test_data.sql
```

### 方式三：使用 MySQL Workbench

1. 打开 MySQL Workbench
2. 连接到数据库
3. 依次打开每个 SQL 文件并执行（File -> Open SQL Script）
4. 按照执行顺序依次执行

## 数据库验证

如果使用 Python 脚本初始化，直接使用 db_verify.py 验证即可。如果手动执行 SQL 脚本，可以使用以下方法验证：

### 使用 Python 验证脚本（推荐）

```bash
python db_verify.py
```

### 手动验证

在 MySQL 命令行中执行：
```bash
mysql -u root -p shopping_platform_db < verify_database.sql
```

验证脚本会检查：
- 所有表是否存在
- 表总数是否正确（应为 30 个）
- 各模块表数量分布
- 默认数据是否存在
- 数据统计汇总

## 数据库配置

项目中提供了数据库配置示例文件 `db_config.properties.example`，包含：
- 数据库连接配置
- 连接池配置（HikariCP）
- Redis 配置
- MyBatis 配置
- 日志配置

使用方法：
1. 复制 `db_config.properties.example` 为 `db_config.properties`
2. 根据实际情况修改配置参数（主要是数据库用户名和密码）
3. 在应用程序中引用此配置文件

**注意**：`db_config.properties` 文件不会被提交到版本控制系统，请妥善保管。

## Python 脚本依赖

使用 Python 脚本需要安装 MySQL 连接器。项目中已提供 `requirements.txt` 文件。

### 安装方法

**Windows 系统：**

方法一：使用 python -m pip（推荐）
```bash
python -m pip install -r requirements.txt
```

方法二：直接使用 pip
```bash
pip install -r requirements.txt
```

如果提示"pip 不是可识别的命令"，请使用方法一。

**Linux/Mac 系统：**

```bash
pip install -r requirements.txt
```

或者：
```bash
pip3 install -r requirements.txt
```

**手动安装：**

也可以直接安装所需的包：
```bash
pip install mysql-connector-python
```

## 数据库配置

- **数据库名称**: shopping_platform_db
- **字符集**: utf8mb4
- **排序规则**: utf8mb4_unicode_ci
- **存储引擎**: InnoDB
- **默认端口**: 3306

## 默认数据

执行脚本后，数据库中会包含以下默认数据：

### 用户数据
- 管理员账户：admin（密码已加密）

### 短信模板
- 登录验证码模板
- 注册验证码模板
- 订单通知模板

### 商品分类
- 电子产品（一级分类）
  - 手机（二级分类）
  - 电脑（二级分类）
- 服装（一级分类）
  - 男装（二级分类）
  - 女装（二级分类）

### 博客分类
- 技术文章
  - 前端开发
  - 后端开发
- 购物指南
- 生活分享

### 博客标签
- Java, Vue, 购物，教程，技巧

### 店铺数据
- 官方旗舰店
- 数码专营店

## 测试数据（可选）

如果执行了 `99_test_data.sql` 脚本，数据库中还会包含以下测试数据：

### 用户数据
- 测试用户：user1, user2
- 店铺管理员：store_user

### 商品数据
- 智能手机 X1（含多个规格）
- 智能手机 Pro（含多个规格）
- 轻薄笔记本电脑
- 男士休闲 T 恤
- 女士连衣裙

### 其他测试数据
- 购物车数据：2 个
- 订单数据：2 个
- 商品评论：2 条
- 博客文章：3 篇
- 秒杀活动：2 个
- 秒杀商品：3 个

## 注意事项

1. **执行顺序**: 必须严格按照上述顺序执行，因为表之间存在外键依赖关系
2. **权限要求**: 执行脚本需要 MySQL 的 root 权限或具有 CREATE DATABASE 和 CREATE TABLE 权限的用户
3. **数据备份**: 如果数据库已存在，执行脚本会删除原有数据库，请提前备份重要数据
4. **字符集**: 确保 MySQL 服务器支持 utf8mb4 字符集
5. **外键约束**: 脚本启用了外键约束，确保数据完整性
6. **密码安全**: 生产环境中请修改默认密码，不要使用示例密码
7. **配置文件安全**: `db_config.properties` 包含敏感信息，不应提交到版本控制系统

## 数据库表总览

| 模块 | 表数量 | 表名 |
|------|--------|------|
| 用户与认证 | 4 | users, addresses, sms_templates, sms_records |
| 商品与库存 | 9 | categories, products, product_images, product_skus, product_attributes, product_attribute_values, product_sku_attributes, seckill_products, seckill_activities |
| 订单与支付 | 6 | carts, cart_items, orders, order_items, seckill_orders, payments |
| 物流与评论 | 3 | shipments, comments, favorites |
| 内容与统计 | 5 | blogs, blog_categories, blog_tags, blog_comments, stats_records |
| 店铺 | 3 | stores, store_follows, store_ratings |
| **总计** | **30** | |

## 常见问题

### Q1: 执行脚本时遇到外键错误怎么办？
A: 请确保按照正确的顺序执行脚本，因为某些表依赖于其他表的存在。Python 初始化脚本会自动处理执行顺序。

### Q2: 如何验证数据库创建成功？
A: 运行 Python 验证脚本：
```bash
python db_verify.py
```

或者在 MySQL 命令行中执行：
```sql
USE shopping_platform_db;
SHOW TABLES;
```

应该看到 30 个表。

### Q3: 如何重置数据库？
A: 重新运行 Python 初始化脚本，会先删除现有数据库再重新创建：
```bash
python db_init.py
```

### Q4: 提示"pip 不是可识别的命令"怎么办？
A: 使用 `python -m pip` 代替 `pip`：
```bash
python -m pip install -r requirements.txt
```

### Q5: 连接 MySQL 失败，提示"Access denied"怎么办？
A: 检查 `db_config.properties` 文件中的用户名和密码是否正确。如果忘记了 MySQL 密码，需要重置 MySQL root 密码。

### Q6: 如何只创建数据库结构，不插入测试数据？
A: 运行 `python db_init.py` 后，在提示"是否要插入测试数据？"时输入 `n` 即可。

### Q7: 如何查看数据库中有多少条测试数据？
A: 运行验证脚本 `python db_verify.py`，会显示各表的数据统计信息。

## 联系与支持

如有问题，请参考项目文档或联系开发团队。
