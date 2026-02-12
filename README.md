# Sticker

一个简洁的表情包下载工具，支持从多个平台搜索和下载表情包。

## 功能特性

- **多平台搜索**：支持从抖音、微信等多个平台搜索表情包
- **快速下载**：一键下载表情包，简单快捷
- **收藏管理**：收藏喜欢的表情包，方便查看
- **简洁界面**：清爽的界面设计，操作直观
- **自定义设置**：可配置浏览器路径、保存位置等

## 技术栈

- **Java 17**：主要开发语言
- **JavaFX**：桌面应用框架
- **Maven**：项目构建工具
- **Playwright**：浏览器自动化，用于抓取表情包
- **FastJSON2**：JSON 处理
- **Lombok**：简化 Java 代码
- **Logback**：日志框架

## 项目结构

```
stickers/
├── src/
│   ├── main/
│   │   ├── java/cn/bitloom/
│   │   │   ├── controller/       # 控制器
│   │   │   ├── service/         # 数据源服务
│   │   │   ├── enums/           # 枚举类
│   │   │   ├── model/           # 数据模型
│   │   │   ├── vm/             # 视图模型
│   │   │   ├── util/            # 工具类
│   │   │   ├── constant/        # 常量
│   │   │   ├── holder/          # 持有者接口
│   │   │   ├── router/          # 路由
│   │   │   ├── store/           # 全局状态存储
│   │   │   └── StickersApplication.java
│   │   └── resources/cn/bitloom/
│   │       ├── view/            # 页面 FXML
│   │       ├── components/      # 组件 FXML
│   │       ├── images/          # 图片资源
│   │       └── index.css        # 样式文件
├── docs/                       # 文档
└── pom.xml                     # Maven 配置
```

## 数据源

- **抖音**：从抖音平台搜索表情包，需要 Playwright 浏览器
- **微信**：从微信平台搜索表情包，需要 Playwright 浏览器

## 使用方法

### 环境要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本

### 构建项目

```bash
mvn clean package
```

### 运行应用

```bash
mvn javafx:run
```

或者运行打包后的 JAR 文件：

```bash
java -jar target/stickers-1.0-SNAPSHOT.jar
```

## 配置说明

应用首次运行时会自动创建以下目录：

- 应用配置目录：`~/.stickers/`
- 日志目录：`~/.stickers/logs/`
- 临时文件目录：`~/.stickers/temp/`
- 收藏目录：`~/.stickers/favorites/`

### 设置项

- **浏览器路径**：用于抓取表情包的浏览器可执行文件路径
- **保存位置**：下载的表情包保存目录
- **抓取源**：选择启用的数据源（抖音、微信）

## 版本

当前版本：1.0.0

## 许可证

© 2026 bitloom

## 贡献

欢迎提交 Issue 和 Pull Request！
