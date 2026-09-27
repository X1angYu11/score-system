# 📊 学生学业预警系统（Spring Boot 版）

面向高校教务场景的学生成绩管理与**学业预警**后端：在成绩增删改查与统计分析的基础上，按预警规则识别"不及格 / 成绩下滑"的学生，帮助教师及时干预。

基于 **Spring Boot + MySQL**，采用 **Controller / Service / Dao 三层架构**，对外提供 RESTful 接口，并自带网页操作界面。

![界面截图](screenshot.png)

## ✨ 功能

- **登录鉴权**：登录后获得会话（Session），未登录访问业务接口返回 401
- 学生信息**增删改查**（新增 / 按学号查询 / 按姓名查询 / 修改成绩 / 删除）
- **分页查询**（`page` / `size` 参数，含边界校验）
- **成绩统计**（总人数 / 平均分 / 最高分 / 最低分 / 及格人数 / 及格率）
- **成绩排行榜**（前 N 名，支持并列名次）
- **批量导入**：一次提交多个学生，由事务保证**全部成功或全部回滚**
- 全部学生列表、按成绩降序排列
- 资源不存在返回标准 **404**，错误响应格式统一
- 自带网页界面，浏览器即可完成全部操作

## 🔌 接口一览

| 功能 | 接口 | 方法 | 是否需要登录 |
|---|---|---|---|
| 登录 | `/login` | POST（JSON） | 否 |
| 退出登录 | `/logout` | POST | 否 |
| 查询全部学生 | `/students` | GET | 是 |
| 按学号查询 | `/students/{id}` | GET | 是 |
| 按姓名查询 | `/students/search?name=xxx` | GET | 是 |
| 按成绩降序排列 | `/students/sort` | GET | 是 |
| 分页查询 | `/students/page?page=1&size=3` | GET | 是 |
| 成绩统计 | `/students/stats` | GET | 是 |
| 成绩排行榜 | `/students/top?n=3` | GET | 是 |
| 添加学生 | `/students` | POST（JSON body） | 是 |
| 批量导入学生 | `/students/batch` | POST（JSON 数组） | 是 |
| 修改成绩 | `/students/{id}/score?score=xx` | PUT | 是 |
| 删除学生 | `/students/{id}` | DELETE | 是 |

**统一错误响应格式**（由 `@RestControllerAdvice` 全局异常处理器返回）：

```json
{"code": 404, "message": "学生YYY不存在"}
```

**鉴权说明**：登录状态保存在服务端 **Session** 中，浏览器/客户端通过 Cookie（`JSESSIONID`）携带会话标识。
未登录访问受保护接口时，由 `LoginInterceptor` 拦截并返回 `401`。

## 🧱 技术栈

| 分类 | 技术 |
|---|---|
| 语言 / 框架 | Java 25、Spring Boot 4.1.1、Spring MVC、Spring JDBC、Spring 事务管理 |
| 鉴权 | HttpSession + HandlerInterceptor（拦截器） |
| 数据库 | MySQL 8（HikariCP 连接池） |
| 构建 / 工具 | Maven、Git |
| 前端 | 原生 HTML + Fetch API |

## 🚀 快速开始

### 1. 初始化数据库

```sql
CREATE DATABASE IF NOT EXISTS score_db;
USE score_db;
CREATE TABLE IF NOT EXISTS student (
    id    VARCHAR(10) PRIMARY KEY,
    name  VARCHAR(20),
    score DOUBLE
);
```

### 2. 配置数据库连接

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/score_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 启动

运行 `src/main/java/com/example/scoresystem/ScoreSystemApplication.java`，
控制台出现 `Tomcat started on port 8080` 即启动成功。

### 4. 使用

- **网页界面**：浏览器打开 <http://localhost:8080/index.html>
  - 默认账号：`zxy` / `123456`（定义在 `LoginController` 中，实际项目应存入数据库并加密）
  - 登录后即可使用统计、分页、排行榜、增删改、批量导入等全部功能
- **接口调用**（PowerShell 示例）：

```powershell
# 1. 先登录：把会话保存在变量 $sess 中（浏览器里由 Cookie 自动完成）
$r = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '{"username":"zxy","password":"123456"}' -SessionVariable sess
$r.Content      # 登录成功：zxy

# 2. 带会话调用业务接口
Invoke-RestMethod -Uri "http://localhost:8080/students" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/stats" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/page?page=1&size=3" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/top?n=3" -WebSession $sess

# 3. 添加学生
Invoke-RestMethod -Uri "http://localhost:8080/students" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '{"id":"007","name":"小明","score":88.5}' -WebSession $sess

# 4. 批量导入
Invoke-RestMethod -Uri "http://localhost:8080/students/batch" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '[{"id":"008","name":"小红","score":91},{"id":"009","name":"小刚","score":76}]' -WebSession $sess

# 5. 修改成绩 / 删除
Invoke-RestMethod -Uri "http://localhost:8080/students/007/score?score=90" -Method Put -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/007" -Method Delete -WebSession $sess
```

> 不带会话直接访问受保护接口（例如 `curl.exe http://localhost:8080/students`）会得到 `401 {"code":401,"message":"请先登录"}`。

## 📁 项目结构

```
src/main/java/com/example/scoresystem/
├── ScoreSystemApplication.java    启动类
├── Student.java                   实体类
├── StudentController.java         控制层：接收 HTTP 请求、返回响应
├── StudentService.java            业务层接口
├── StudentServiceImpl.java        业务层实现（分页边界校验、统计计算、@Transactional 批量导入）
├── StudentDao.java                数据层接口
├── StudentDaoImpl.java            数据层实现（JdbcTemplate + RowMapper）
├── LoginController.java           登录 / 退出登录（Session 读写）
├── LoginInterceptor.java          登录拦截器（preHandle 中校验会话）
├── WebConfig.java                 注册拦截器与拦截路径
├── StudentNotFoundException.java  自定义业务异常（404）
├── UnauthorizedException.java     自定义鉴权异常（401）
└── GlobalExceptionHandler.java    全局异常处理器（@RestControllerAdvice）
src/main/resources/
├── application.properties         配置（数据库连接、JSON 格式化）
└── static/index.html              网页界面
```

**请求处理链**：

```
浏览器 / curl
     ↓
LoginInterceptor（受保护路径先校验会话，未登录 → 401）      ← 鉴权
     ↓
Controller（接收请求、组装响应）                            ← 协议
     ↓
Service（业务规则、边界校验、事务边界）                     ← 业务
     ↓
Dao（SQL 与数据映射）                                      ← 数据
     ↓
MySQL
     ↑
GlobalExceptionHandler（异常统一转成 {code, message}）      ← 横切
```

## 📈 版本演进

| 版本 | 形态 | 说明 |
|---|---|---|
| **v1.0** | Java + JDBC 控制台程序 | 命令行菜单实现增删改查，启动自动加载数据、增删改实时同步数据库 |
| **v2.0** | Spring Boot 三层架构 RESTful 后端 | 重构为 Controller/Service/Dao 分层 + RESTful 接口 + 网页界面，**功能零回归** |
| **v2.1** | 健壮性增强 | 全局异常处理、批量导入事务、按影响行数返回 404 |
| **v2.2** | 功能与安全完善 | 新增登录鉴权（Session + 拦截器）、分页查询、成绩统计、排行榜；前端接入全部接口并统一错误提示 |

**关键改进说明**：

1. **架构分层（v2.0）**：从"所有逻辑写在一个类里"改为三层架构，各层职责单一；层间通过接口通信 + 依赖注入（`@Autowired`），便于替换实现与测试。
2. **安全修复（v2.0）**：v1.0 使用字符串拼接 SQL，存在 **SQL 注入**风险（如姓名输入 `张三'); DROP TABLE student;--` 会删除整张表）；v2.0 全部改用 `?` 参数化占位符，从根本上消除该隐患。
3. **事务保障（v2.1）**：批量导入接口使用 `@Transactional`，任一学生插入失败则**整体回滚**，避免出现"导了一半"的脏数据。
4. **统一异常处理（v2.1）**：用 `@RestControllerAdvice` 集中处理业务异常，各接口只需抛出业务异常，响应格式与状态码由全局处理器统一决定；新增错误类型只需改一个类。
5. **准确反馈（v2.1）**：修改 / 删除接口根据 JdbcTemplate 返回的**影响行数**判断是否命中数据，未命中返回 404。
6. **登录鉴权（v2.2）**：登录成功后把用户标识写入 `HttpSession`；`LoginInterceptor` 在请求进入 Controller 前统一校验（`getSession(false)`，避免为匿名请求创建会话），未登录返回 401。登录状态存于服务端，客户端仅持有会话编号，因此客户端无法伪造身份。
7. **减少数据库往返（v2.2）**：成绩统计用**一条聚合 SQL**（`COUNT/AVG/MAX/MIN/SUM(CASE WHEN ...)`）一次取得全部统计值，避免多次查询造成的重复表扫描与连接占用。
8. **分页正确性（v2.2）**：分页查询使用 `ORDER BY id LIMIT offset, size`，**必须带确定性排序**，否则数据库不保证顺序，翻页可能出现重复或漏读。

## ❓ 常见问题

| 现象 | 原因 / 解决 |
|---|---|
| 接口返回 `401 {"code":401,...}` | 未登录或会话已过期：先调用 `/login`，并确保后续请求携带 Cookie / 使用同一个会话 |
| `Port 8080 already in use` | 8080 被其他程序占用，先停掉它 |
| 连接数据库失败 | 检查 MySQL 服务、账号密码、`score_db` 是否已创建 |
| 改了代码/页面不生效 | Spring Boot 不会热加载：用 IDEA 重启；若是 `java -jar` 运行的，需重新 `package` 打包 |
| 页面按钮"点了没反应" | 通常是接口报错但前端未处理，按 `F12` 看 Console 与 Network |
| PowerShell 里 curl 传 JSON 报错 | `curl.exe` 在 PowerShell 中会吞掉 JSON 的引号，改用 `Invoke-RestMethod` + 单引号包 JSON |
| `java -jar` 与 IDEA 运行结果不一致 | jar 是打包时的快照，源码改动需重新打包才会生效 |
