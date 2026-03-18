# POM.xml 配置模板说明

本目录包含 Maven POM.xml 配置模板，用于快速配置微服务模块的依赖管理。

## 📁 文件说明

- **pom.xml.template**: 通用模板文件，包含所有常用依赖和配置说明

## 🚀 使用方法

### 1. 复制模板

将模板文件复制到需要配置的服务目录下：

```bash
# 示例：为新服务配置 pom.xml
cp backend/templates/pom.xml.template backend/your-service/pom.xml
```

### 2. 修改基本信息

编辑 `pom.xml` 文件，修改以下基本信息：

```xml
<artifactId>your-service-name</artifactId>
<name>your-service-name</name>
<description>你的服务描述</description>
```

### 3. 按需选择依赖

根据服务功能需求，从模板中选择需要的依赖：

#### 基础微服务依赖（所有服务都需要）
- ✅ `spring-boot-starter-web`: Web 服务基础
- ✅ `spring-cloud-starter-netflix-eureka-client`: 服务注册与发现
- ✅ `spring-cloud-starter-config`: 配置中心
- ✅ `spring-cloud-starter-bootstrap`: Bootstrap 配置支持
- ✅ `lombok`: 简化代码

#### 数据库相关（按需添加）
- 📦 `mysql-connector-j`: MySQL 驱动（需要数据库时添加）
- 📦 `mybatis-plus-boot-starter`: MyBatis-Plus ORM（使用 MyBatis 时添加）

#### 中间件（按需添加）
- 📦 `spring-boot-starter-data-redis`: Redis 缓存
- 📦 `spring-kafka`: Kafka 消息队列

#### 工具类（按需添加）
- 📦 `jjwt-*`: JWT Token 认证
- 📦 `hutool-all`: Java 工具类库

## 📋 各服务依赖配置参考

| 服务名称 | 必需依赖 |
|---------|---------|
| **user-service** | 基础依赖 + MySQL + MyBatis-Plus + JWT |
| **product-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis + Kafka |
| **order-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis + Kafka |
| **payment-service** | 基础依赖 + MySQL + MyBatis-Plus + Kafka |
| **shipment-service** | 基础依赖 + MySQL + MyBatis-Plus + Kafka |
| **comment-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis |
| **blog-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis |
| **store-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis |
| **sms-service** | 基础依赖 + Kafka（不需要数据库） |
| **stats-service** | 基础依赖 + MySQL + MyBatis-Plus + Redis + Kafka |
| **eureka-server** | 仅需 `spring-cloud-starter-netflix-eureka-server` |
| **config-server** | 仅需 `spring-cloud-config-server` |

## ⚠️ 重要注意事项

### 1. 版本管理

**所有依赖的版本由父 pom.xml 统一管理**，子模块不需要也不应该指定版本号。

❌ **错误示例**（不要在子模块中声明版本）：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>  <!-- ❌ 错误：不应该指定版本 -->
</dependency>
```

✅ **正确示例**（只声明 groupId 和 artifactId）：
```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <!-- ✅ 正确：版本由父 POM 管理 -->
</dependency>
```

### 2. 依赖范围（scope）

正确使用 scope 可以避免依赖传递和打包问题：

- `compile`（默认）：编译和运行时都需要
- `runtime`：仅运行时需要（如数据库驱动）
- `provided`：由外部容器提供（如 Tomcat）
- `test`：仅测试时需要

示例：
```xml
<!-- MySQL 驱动：仅在运行时需要 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok：不传递到其他模块 -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

### 3. 避免重复依赖

以下依赖已经在父 pom.xml 中声明，**不要**在子模块中重复添加：

- Spring Boot 相关依赖（spring-boot-starter-*）
- Spring Cloud 相关依赖（spring-cloud-starter-*）
- MyBatis-Plus
- JWT (jjwt-*)
- Hutool
- 阿里云 SDK

### 4. 构建插件

所有 Spring Boot 服务都需要添加构建插件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## 🔧 常见问题

### Q1: 为什么要将版本管理放在父 pom.xml？

**A:** 集中管理依赖版本有以下优点：
- ✅ 统一版本，避免版本冲突
- ✅ 便于升级和维护
- ✅ 子模块配置更简洁
- ✅ 遵循 Maven 最佳实践

### Q2: 如何添加新的依赖？

**A:** 
1. 首先在父 pom.xml 的 `<dependencyManagement>` 中添加版本管理
2. 然后在子模块的 `<dependencies>` 中添加依赖（不指定版本）

示例：
```xml
<!-- 父 pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>example-library</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 子模块 pom.xml -->
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>example-library</artifactId>
        <!-- 不指定版本 -->
    </dependency>
</dependencies>
```

### Q3: 出现"重复依赖"警告怎么办？

**A:** 
1. 检查是否在子模块中重复声明了版本号
2. 检查是否添加了父 pom.xml 已管理的依赖
3. 确保只添加当前服务真正需要的依赖

## 📚 参考资源

- [Maven POM 参考文档](https://maven.apache.org/pom.html)
- [Spring Boot 依赖管理](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#dependency-management)
- [Spring Cloud 依赖管理](https://spring.io/projects/spring-cloud)
