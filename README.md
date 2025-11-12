# VIP Management System（大学生Java科目作业）

一个基于 Java Swing 和 SQLite 开发的桌面端会员管理系统，专为中小型美发门店设计。系统提供会员管理、预约、充值/消费记录和优惠套餐设置等全流程管理功能。

## 功能

- **会员管理**：添加、编辑、删除会员信息，查看会员详情。
- **预约管理**：支持会员预约服务，管理预约时间和状态。
- **充值/消费记录**：记录会员的充值和消费历史，支持余额查询。
- **优惠套餐设置**：创建和管理优惠套餐，适用于不同会员等级。

## 环境要求

- **Java**：JDK 8 或以上
- **Maven**：用于管理项目依赖
- **SQLite**：用于本地数据存储

## 安装

1. 确保安装了 JDK 8 或以上。
2. 安装 Maven（或通过 IDE 如 IntelliJ IDEA 内置的 Maven 支持）。
3. 克隆本仓库：git clone https://github.com/idy-Q/VIP_Management.git。
4. 在 IntelliJ IDEA（或支持 Maven 的 IDE）中打开项目，Maven 会自动下载 `pom.xml` 中定义的依赖（包括 SQLite JDBC 驱动）。
5. 运行主程序（`Login.java`）。

## 依赖

- 项目依赖定义在 `pom.xml` 中，Maven 会自动下载。
- 核心技术：
  - Java Swing（内置于 JDK）
  - SQLite（通过 SQLite JDBC 驱动连接数据库）

## 使用说明

1. 启动程序后，进入主界面。
2. 通过界面操作进行会员管理、预约等功能。
3. 数据存储在本地 SQLite 数据库文件（确保 `.db` 文件可写）。

## 贡献

欢迎提交问题或拉取请求！
