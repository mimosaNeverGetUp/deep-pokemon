# 项目上下文

这是一个基于 SpringBoot 后端与 Vue 前端的 web 应用：爬取/分析 Pokemon Showdown 对战大数据并对外展示。

## 架构
- 前端：Vue 3，位于 `/pokemon-front`
- 后端服务（均为 Spring Boot，位于各子模块）：
  - `/pokemon-portal`：面向前端提供 API（Spring Web + Spring Security + OpenFeign 调用爬虫服务）
  - `/pokemon-crawler`：数据爬取与对战分析（定时任务、事件解析、标签生成）
  - `/pokemon-common`：公共实体类集合
  - `/eureka-server`：服务注册中心，**当前未使用**（代码中仅保留相关配置）
- 数据库：持久化 MongoDB，缓存 Redis
- 服务调用：portal 通过 OpenFeign 调用服务名 `http://pokemon-crawler:8081`（docker swarm 内部 DNS），不走 eureka
- 部署：Docker Compose / Docker Swarm（生产实际用 swarm），生产环境部署在腾讯云

## 技术栈与版本
- JDK 17 / Maven 多模块
- Spring Boot 3.2.7，Spring Cloud 2023.0.1
- 前端：Vue 3.5 + Vite 5 + PrimeVue 3.53 + Tailwind 4 + vue-router + vue-i18n
- 测试：Testcontainers + MongoDB + JaCoCo 覆盖率

## 构建与运行
- Maven 多模块，根 pom 聚合构建；前端基于 npm
- 前端：在 `/pokemon-front` 执行 `npm run dev`（另见 `build` / `preview`）
- 后端：启动各模块的 `*Application` 主类，两种方式等价——通过 IDE 运行主类（可在 Run Config 选择 profile），或命令行 `mvn spring-boot:run`
- 测试：见下方 `## 测试` 章节

## 测试
- 框架：JUnit 5（junit-jupiter）+ Spring Boot Test；覆盖率 JaCoCo（Maven test 阶段生成 HTML/XML）
- 执行方式：两种等价——通过 IDE 的 JUnit runner 直接运行测试类（复用同一 pom 依赖解析，不经 Maven test 阶段），或命令行 `mvn test`（若全局配置了跳过测试执行 `skipTests`，需去掉该参数才会真正执行）
- 回归/子集测试：定位某个测试子包或类，用 Maven 包筛选（等价于直接调 JUnit Platform + 选包）
- 常用回归示例——crawler 的 `analyzer` 子包（纯 `@SpringBootTest` 上下文测试，不依赖 Testcontainers/Docker，已实测通过）：
  ```
  mvn -pl pokemon-common install -DskipTests   # common 已在本地仓库时可跳过此步
  mvn -pl pokemon-crawler test \
    -Dtest='com.mimosa.deeppokemon.analyzer.**' \
    -DskipTests=false -DargLine='-ea' -Deureka.client.enabled=false
  ```
- 待补充：部分用例通过 Testcontainers 启动 MongoDB，需要本机可用的 Docker 环境（相关命令后续补充）

## 约定
- 保证项目可读性和可维护性，每个新添加的类遵循单一职责，遵循设计模式
- 坚持简洁优先原则
- 只修改必要内容

## 重要说明
- 没有指示，不要直接修改文件
- 遇到不确定的内容主动提问，不要凭空猜测