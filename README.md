# 📊 学生学业预警系统（Spring Boot 版）

面向高校教务场景的学生成绩管理与**学业预警**后端：在成绩增删改查与统计分析的基础上，按预警规则识别"不及格 / 成绩临界"的学生，帮助教师及时干预。

基于 **Spring Boot + MySQL**，采用 **Controller / Service / Dao 三层架构**，对外提供 RESTful 接口，并自带网页操作界面。

在此基础上继续集成了 **MyBatis**（动态 SQL）、**Redis 缓存**、**Spring AOP**、**大模型调用（DeepSeek）** 与 **JUnit + Mockito 单元测试**。

![界面截图](screenshot.png)

## ✨ 功能

- **登录鉴权**：登录后获得会话（Session），未登录访问业务接口返回 401
- **用户注册**：密码使用 **BCrypt** 加密存储（不存明文）
- 学生信息**增删改查**（新增 / 按学号查询 / 按姓名查询 / 修改成绩 / 删除）
- **分页查询**（`page` / `size` 参数，含边界校验）
- **成绩统计**（总人数 / 平均分 / 最高分 / 最低分 / 及格人数 / 及格率）
- **成绩排行榜**（前 N 名，支持并列名次）
- **成绩区间查询**
- **学业预警**：按阈值识别不及格 / 临界学生，**阈值可在配置文件调整**
- **批量导入**：一次提交多个学生，由事务保证**全部成功或全部回滚**
- **参数校验**：学号/姓名非空、成绩 0~100，校验失败统一返回 400
- **课程管理**：课程信息增删改查（独立模块，MyBatis Mapper 直接作为数据层）
- **AI 班级分析**：把全班成绩与预警名单拼成提示词交给大模型，返回班级整体分析与教学建议
- **AI 个性化建议**：`/students/ai-advice/{id}` 针对**单个学生**，结合其分数与预警等级生成学习建议
- **Redis 缓存**：统计与 AI 结果进缓存，写操作自动清缓存；AI 调用配置了连接/读取超时，失败统一返回 **503**
- 资源不存在返回标准 **404**，错误响应格式统一
- 自带网页界面，浏览器即可完成全部操作

## 🔌 接口一览

| 功能 | 接口 | 方法 | 是否需要登录 |
|---|---|---|---|
| 注册 | `/register` | POST（JSON） | 否 |
| 登录 | `/login` | POST（JSON） | 否 |
| 退出登录 | `/logout` | POST | 否 |
| 查询全部学生 | `/students` | GET | 是 |
| 按学号查询 | `/students/{id}` | GET | 是 |
| 按姓名查询 | `/students/search?name=xxx` | GET | 是 |
| 按成绩降序排列 | `/students/sort` | GET | 是 |
| 分页查询 | `/students/page?page=1&size=3` | GET | 是 |
| 成绩统计 | `/students/stats` | GET | 是 |
| 成绩排行榜 | `/students/top?n=3` | GET | 是 |
| 学业预警名单 | `/students/warnings` | GET | 是 |
| 成绩区间查询 | `/students/score-range?minScore=60&maxScore=90` | GET | 是 |
| 添加学生 | `/students` | POST（JSON body） | 是 |
| 批量导入学生 | `/students/batch` | POST（JSON 数组） | 是 |
| 修改成绩 | `/students/{id}/score?score=xx` | PUT | 是 |
| 删除学生 | `/students/{id}` | DELETE | 是 |
| AI 班级成绩分析 | `/students/ai-analysis` | GET | 是 |
| AI 单个学生建议 | `/students/ai-advice/{id}` | GET | 是 |
| 查询全部课程 | `/courses` | GET | 是 |
| 按课程号查询 | `/courses/{courseId}` | GET | 是 |
| 添加课程 | `/courses` | POST（JSON body） | 是 |
| 修改课程 | `/courses/{courseId}` | PUT（JSON body） | 是 |
| 删除课程 | `/courses/{courseId}` | DELETE | 是 |

**统一错误响应格式**（由 `@RestControllerAdvice` 全局异常处理器返回）：

```json
{"code": 404, "message": "学生YYY不存在"}
```

| 状态码 | 场景 |
|---|---|
| 400 | 参数校验失败（如成绩为负、姓名空、学分超范围）、区间参数非法（min > max）、主键/唯一键冲突 |
| 401 | 未登录或会话已过期 |
| 404 | 学生 / 课程不存在；修改或删除未命中数据 |
| 503 | 依赖的 AI 服务不可用（网络异常、超时、API 返回错误码） |

## 🧱 技术栈

| 分类 | 技术 |
|---|---|
| 语言 / 框架 | Java 25、Spring Boot 4.1.1、Spring MVC、Spring JDBC、Spring 事务管理 |
| 持久层 | JdbcTemplate（学生模块）+ **MyBatis**（课程模块、动态 SQL） |
| 缓存 | **Redis** + Spring Cache 注解（`@Cacheable` / `@CacheEvict`），Jedis 客户端 |
| AOP | Spring AOP（`@Aspect` + `@Around`，记录接口调用耗时） |
| 鉴权 | HttpSession + HandlerInterceptor（拦截器） |
| 安全 | BCrypt 密码哈希（spring-security-crypto）、SQL 参数化查询 |
| 校验 | Jakarta Bean Validation（`@Valid`） |
| 数据库 | MySQL 8（HikariCP 连接池 + 索引优化） |
| AI | `RestClient` 调用 DeepSeek Chat API（配置连接/读取超时） |
| 测试 | JUnit 5 + Mockito（Service 层单元测试） |
| 构建 / 工具 | Maven、Git |
| 前端 | 原生 HTML + Fetch API |

## 🚀 快速开始

### 1. 初始化数据库

```sql
CREATE DATABASE IF NOT EXISTS score_db
    DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE score_db;

-- 学生成绩表
CREATE TABLE IF NOT EXISTS student (
    id    VARCHAR(10) PRIMARY KEY,
    name  VARCHAR(20),
    score DOUBLE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统用户表（密码存 BCrypt 哈希）
CREATE TABLE IF NOT EXISTS sys_user (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 课程表（课程模块）
CREATE TABLE IF NOT EXISTS course (
    course_id   VARCHAR(10)  NOT NULL,
    course_name VARCHAR(20)  NOT NULL,
    credit      DECIMAL(3,1) NOT NULL,
    teacher     VARCHAR(20),
    PRIMARY KEY (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 索引（见下方"数据库设计与索引"说明）
ALTER TABLE student ADD INDEX idx_score (score);
ALTER TABLE student ADD INDEX idx_name  (name);
```

### 2. 配置数据库连接

修改 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/score_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=你的密码

# Redis（缓存；不启动 Redis 会导致带缓存注解的接口报错）
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=60s

# DeepSeek（key 从环境变量读，不要写死在文件里）
deepseek.api-key=${DEEPSEEK_API_KEY:}
deepseek.api-url=https://api.deepseek.com/chat/completions
deepseek.model=deepseek-chat
```

### 3. 启动

运行 `src/main/java/com/example/academicwarning/AcademicWarningApplication.java`，
控制台出现 `Tomcat started on port 8080` 即启动成功。

### 4. 使用

- **网页界面**：浏览器打开 <http://localhost:8080/index.html>
  - 首次使用：在页面上**先点"注册"**创建账号（密码会以 BCrypt 哈希存入 `sys_user`），再点"登录"
  - 登录后即可使用统计、预警、分页、排行榜、增删改、批量导入等全部功能
- **接口调用**（PowerShell 示例）：

```powershell
# 1. 注册（首次）
Invoke-RestMethod -Uri "http://localhost:8080/register" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '{"username":"zxy","password":"123456"}'

# 2. 登录：把会话保存在变量 $sess 中（浏览器里由 Cookie 自动完成）
$r = Invoke-WebRequest -Uri "http://localhost:8080/login" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '{"username":"zxy","password":"123456"}' -SessionVariable sess
$r.Content      # 登录成功zxy

# 3. 带会话调用业务接口
Invoke-RestMethod -Uri "http://localhost:8080/students" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/stats" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/warnings" -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/score-range?minScore=60&maxScore=70" -WebSession $sess

# 4. 添加 / 批量导入 / 修改 / 删除
Invoke-RestMethod -Uri "http://localhost:8080/students" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '{"id":"007","name":"小明","score":88.5}' -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/batch" -Method Post `
     -ContentType "application/json; charset=utf-8" `
     -Body '[{"id":"008","name":"小红","score":91},{"id":"009","name":"小刚","score":76}]' -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/007/score?score=90" -Method Put -WebSession $sess
Invoke-RestMethod -Uri "http://localhost:8080/students/007" -Method Delete -WebSession $sess
```

> 不带会话直接访问受保护接口（例如 `curl.exe http://localhost:8080/students`）会得到 `401 {"code":401,"message":"请先登录"}`。

## 📁 项目结构

```
src/main/java/com/example/academicwarning/
├── AcademicWarningApplication.java  启动类
├── Student.java                     学生实体（含校验注解）
├── SysUser.java                     用户实体
├── StudentController.java           学生接口：增删改查/分页/统计/排行/预警/区间查询
├── StudentService.java              业务层接口
├── StudentServiceImpl.java          业务层实现（分页边界校验、统计换算、预警判定、事务）
├── StudentDao.java                  数据层接口
├── StudentDaoImpl.java              数据层实现（JdbcTemplate + RowMapper）
├── LoginController.java             注册 / 登录 / 退出登录
├── UserService.java / UserServiceImpl.java    用户业务（查重、BCrypt 加密与校验）
├── UserDao.java / UserDaoImpl.java            用户数据访问
├── LoginInterceptor.java            登录拦截器（preHandle 中校验会话）
├── WebConfig.java                   注册拦截器与拦截路径
├── StudentNotFoundException.java    学生不存在异常（404）
├── CourseNotFoundException.java     课程不存在异常（404）
├── UnauthorizedException.java       自定义鉴权异常（401）
├── BadRequestException.java         自定义参数异常（400）
├── AiServiceException.java          AI 调用失败异常（503）
├── LogAspect.java                   AOP 切面：记录 Controller 调用耗时
│
├── Course.java                      课程实体（含校验注解）
├── CourseMapper.java                课程数据层：MyBatis @Mapper 接口（注解式 SQL）
├── CourseService.java               课程业务层接口
├── CourseServiceImpl.java           课程业务层实现（不存在则抛异常）
├── CourseController.java            课程接口：增删改查
│
├── AiService.java                   AI 业务层接口
├── AiServiceImpl.java               AI 实现：拼提示词 → RestClient 调 DeepSeek → 解析 JSON
└── GlobalExceptionHandler.java      全局异常处理器（@RestControllerAdvice）

src/test/java/com/example/academicwarning/
├── StudentServiceImplTest.java      Service 层单元测试（Mockito 模拟 Dao）
└── CourseServiceImplTest.java       课程模块 Service 层单元测试

src/main/resources/
├── application.properties           数据库连接、连接池、预警阈值、JSON 格式化
└── static/index.html                网页界面
```

**请求处理链**：

```
浏览器 / curl
     ↓
LoginInterceptor（受保护路径先校验会话，未登录 → 401）      ← 鉴权
     ↓
Controller（接收请求、参数绑定、组装响应）                   ← 协议
     ↓
Service（业务规则、边界校验、事务边界）                      ← 业务
     ↓
Dao（SQL 与数据映射）                                       ← 数据
     ↓
MySQL
     ↑
GlobalExceptionHandler（异常统一转成 {code, message}）       ← 横切
```

## 🗂️ 数据库设计与索引

### 表结构

| 表 | 字段 | 说明 |
|---|---|---|
| `student` | id (PK) / name / score | 学生成绩 |
| `course` | course_id (PK) / course_name / credit / teacher | 课程信息，`credit` 用 `DECIMAL(3,1)` 而非 `DOUBLE`（避免浮点误差） |
| `sys_user` | id (PK, 自增) / username (唯一) / password | 密码存 **BCrypt 哈希**，不存明文 |

### 索引（有意识设计，不是随手加的）

| 索引 | 列 | 服务的查询 | 为什么加 |
|---|---|---|---|
| PRIMARY | `student.id` | 按学号查、分页排序 | 主键自带 |
| `idx_score` | `student.score` | `WHERE score < ?`、`BETWEEN ? AND ?`、`ORDER BY score DESC` | 成绩范围查询与排序频繁 |
| `idx_name` | `student.name` | `WHERE name = ?` | 按姓名查询 |
| `uk_username`（唯一） | `sys_user.username` | 登录时按用户名查 | 唯一性约束 + 加速查询 |

> **索引不是越多越好**：它占额外空间，且每次写操作都要同步维护索引，会拖慢 INSERT/UPDATE/DELETE。
> 因此只给**查询频繁**且**区分度高**的列建索引。

### 用 EXPLAIN 验证

```sql
EXPLAIN SELECT * FROM student WHERE score BETWEEN 60 AND 70;
```

期望看到：`type = range`、`key = idx_score`、`rows` 明显小于总行数。

> 注意：**表数据很少时（几十行），优化器可能仍选择全表扫描（`type=ALL`）**，这是正常的——
> 数据量足够大时索引的效果才明显。可以用递归 CTE 造几万行测试数据来对比：
> ```sql
> SET SESSION cte_max_recursion_depth = 100000;
> INSERT INTO student_big (id, name, score)
> WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 50000)
> SELECT CONCAT('S', LPAD(n, 6, '0')), CONCAT('s', n), ROUND(RAND() * 100, 1) FROM seq;
> ```

## 🧪 单元测试

```powershell
mvn test        # 或在 IDEA 里点测试类旁的绿色三角
```

测试思路：**用 Mockito 把 Dao 换成假的**，隔离数据库，只验证 Service 的业务逻辑
（例如"页码小于 1 时被修正为第 1 页"、"每页条数超过上限时被截断为 100"、"最小值大于最大值时抛异常"）。

## 📈 版本演进

| 版本 | 形态 | 说明 |
|---|---|---|
| **v1.0** | Java + JDBC 控制台程序 | 命令行菜单实现增删改查，启动自动加载数据、增删改实时同步数据库 |
| **v2.0** | Spring Boot 三层架构 RESTful 后端 | 重构为 Controller/Service/Dao 分层 + RESTful 接口 + 网页界面，**功能零回归** |
| **v2.1** | 健壮性增强 | 全局异常处理、批量导入事务、按影响行数返回 404 |
| **v2.2** | 功能与安全完善 | 登录鉴权（Session + 拦截器）、分页、统计、排行榜；前端接入全部接口 |
| **v2.3** | 学业预警与质量保障 | 学业预警（阈值可配）、成绩区间查询、参数校验、BCrypt 密码加密、数据库索引、Service 层单元测试 |
| **v2.4** | 缓存与大模型接入 | 接入 Redis 缓存（Spring Cache 注解）、AI 班级成绩分析接口 |
| **v2.5** | AI 能力完善 + 课程模块 | 单学生 AI 建议接口 + 超时与异常处理（503）；独立完成课程模块（MyBatis + 参数校验 + 单元测试）；判空职责从 Controller 下沉到 Service |

**关键改进说明**：

1. **架构分层（v2.0）**：从"所有逻辑写在一个类里"改为三层架构，各层职责单一；层间通过接口通信 + 依赖注入（`@Autowired`）。
2. **安全修复（v2.0）**：v1.0 使用字符串拼接 SQL，存在 **SQL 注入**风险（如姓名输入 `张三'); DROP TABLE student;--` 会删除整张表）；v2.0 全部改用 `?` 参数化占位符。
3. **事务保障（v2.1）**：批量导入使用 `@Transactional`，任一插入失败则**整体回滚**，避免"导了一半"的脏数据。
4. **统一异常处理（v2.1）**：用 `@RestControllerAdvice` 集中处理业务异常，各接口只抛业务异常；新增错误类型只需改一个类。
5. **准确反馈（v2.1）**：修改 / 删除按 JdbcTemplate 返回的**影响行数**判断是否命中数据，未命中返回 404。
6. **登录鉴权（v2.2）**：登录成功后把用户标识写入 `HttpSession`；`LoginInterceptor` 在请求进入 Controller 前统一校验（用 `getSession(false)`，避免为匿名请求创建会话）。登录状态存于服务端，客户端只持有会话编号，无法伪造身份。
7. **减少数据库往返（v2.2）**：成绩统计用**一条聚合 SQL**（`COUNT/AVG/MAX/MIN/SUM(CASE WHEN ...)`）一次取全部统计值。
8. **分页正确性（v2.2）**：分页使用 `ORDER BY id LIMIT offset, size`，**必须带确定性排序**，否则翻页可能出现重复或漏读。
9. **密码安全（v2.3）**：密码用 **BCrypt** 哈希存储（含随机盐、计算强度 10），不存明文；校验用 `matches()` 而非 `equals()`。
10. **查询性能（v2.3）**：给高频查询列建索引，并用 `EXPLAIN` 验证 `type` 从 `ALL` 变为 `range`；同时认识到索引会拖慢写入，避免滥用。
11. **缓存（v2.4）**：统计与 AI 结果加 `@Cacheable`，写操作加 `@CacheEvict` 保证一致性。`@Cacheable` 必须打在**被外部调用的方法**上——同类内部自调用（`this.chat(...)`）不经过 Spring 代理，注解会**静默失效**。
12. **AI 调用的健壮性（v2.5）**：`RestClient` 默认是"无限等待"，依赖服务抽风时 Tomcat 线程会被逐个占满并引发级联故障；显式配置连接超时 5s / 读取超时 30s，并把 `RestClientException` 翻译成业务异常，由全局处理器返回 **503**（依赖不可用）而不是 500（自身 bug）。
13. **异常职责归属（v2.5）**：查询"不存在则报错"原先写在 Controller 里，导致每个入口都要重复判空、漏写一处就是 500。改为在 **Service 层**统一抛出 `NotFoundException`，所有调用方自动获得 404。
14. **MyBatis 使用要点（v2.5）**：`#{}` 里写的是 **Java 字段名**而不是数据库列名；单个对象参数**不要**加 `@Param`（加了会被包成 Map，反而取不到属性）；多个参数则**必须**用 `@Param` 命名。
15. **参数校验的时机（v2.5）**：`@Valid` 在**方法体执行之前**就完成校验，因此"在方法体里补上的字段"（例如用路径参数覆盖请求体里的 id）**不参与校验**，需要借助校验分组或 DTO 才能解决。

## ❓ 常见问题

| 现象 | 原因 / 解决 |
|---|---|
| 接口返回 `401 {"code":401,...}` | 未登录或会话已过期：先调用 `/login`，并确保后续请求携带 Cookie / 使用同一个会话 |
| `Port 8080 already in use` | 8080 被其他程序占用，先停掉它 |
| 连接数据库失败 | 检查 MySQL 服务、账号密码、`score_db` 是否已创建 |
| `Failed to obtain JDBC Connection` | 多为连接池连接失效（长时间运行、数据库重启、电脑休眠）。重启程序即可恢复；`application.properties` 已配置 `max-lifetime` / `keepalive-time` 减少复发 |
| 改了代码/页面不生效 | Spring Boot 不会热加载：用 IDEA 重启；若是 `java -jar` 运行的，需重新 `package` 打包 |
| 页面按钮"点了没反应" | 通常是接口报错但前端未处理，按 `F12` 看 Console 与 Network |
| PowerShell 里 curl 传 JSON 报错 | `curl.exe` 在 PowerShell 中会吞掉 JSON 的引号，改用 `Invoke-RestMethod` + 单引号包 JSON |
| `java -jar` 与 IDEA 运行结果不一致 | jar 是打包时的快照，源码改动需重新打包才会生效 |
| 索引加了但 `EXPLAIN` 仍显示 `ALL` | 表数据太少，优化器认为全表扫描更快；数据量大时才会走索引 |
| 带缓存注解的接口报 `RedisConnectionFailureException` | Redis 没启动。先启动 `redis-server.exe` 再重试 |
| AI 接口返回 `503 {"code":503,...}` | 依赖的 AI 服务不可用：检查 `DEEPSEEK_API_KEY` 是否配置、网络是否可达、是否触发超时 |
| 新增/修改接口返回 **415** | 请求没带 `Content-Type`。调用 POST/PUT 必须带 `application/json` |
| PowerShell 用 `-ContentType $变量` 结果全 415 | 变量为空时 PowerShell **不报错**，只是不发这个头。改用字面量字符串，或在脚本开头加 `Set-StrictMode -Version Latest` |
| 单元测试全绿却没真正测到东西 | 常见"假绿"：在打桩之前就调用了被测方法，断言的是 Mock 默认值。顺序必须是 **准备 → 打桩 → 执行 → 断言** |
