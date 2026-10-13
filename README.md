# 课程管理后台系统

基于 Java 21、Spring Boot 3.4.4、Spring Web 和 Spring Data JPA 的课程管理 REST API。项目按 controller、service、repository 分层；课程实体和统一响应体均使用手写构造函数、Getter、Setter，不依赖 Lombok，也未加入 Spring Security.

## 启动

1. 安装 JDK 21、Maven 3.9+ 和 MySQL 8。
2. 执行 database/init.sql 创建数据库。
3. 设置数据库环境变量后运行应用：

```powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "course_management"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "你的MySQL密码"
mvn spring-boot:run
```

默认连接地址为 localhost:3306/course_management。Hibernate 会在启动时按实体映射创建或更新 course 表。请通过环境变量提供数据库凭据，不要将真实密码提交到版本库。

## 测试

```powershell
mvn test
```

集成测试使用内存 H2 数据库，不需要运行 MySQL。

## 课程 API

所有响应都使用 Result<T>，格式为 { "code": 200, "message": "查询成功", "data": ... }。异常响应也使用相同结构。

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| GET | /api/courses?page=1&size=10 | 分页查询课程列表 |
| GET | /api/courses/{id} | 查询单门课程 |
| POST | /api/courses | 新建课程，成功返回 HTTP 201 |
| PUT | /api/courses/{id} | 更新课程 |
| DELETE | /api/courses/{id} | 删除课程 |

列表接口的 `page` 从 1 开始，默认值为 1；`size` 默认值为 10，范围为 1 到 100。分页响应的 `data` 包含 `records`（当前页课程）、`totalElements`（总条数）、`totalPages`（总页数）、`currentPage`（当前页）和 `pageSize`（每页条数）。例如：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [],
    "totalElements": 0,
    "totalPages": 0,
    "currentPage": 1,
    "pageSize": 10
  }
}
```

新建和更新请求体示例：

```json
{
  "courseName": "数据结构",
  "credit": 3.5,
  "maxEnrollment": 60
}
```

字段：
- id：数据库生成的课程 ID。
- courseName：课程名称，不能为空或纯空白，最长 100 个字符。
- credit：课程学分，范围为 1 到 5（含边界），最多两位小数。
- maxEnrollment：最大选课人数，正整数。

新建和更新接口都会校验请求字段。校验失败时返回 HTTP 400，响应仍为统一 `Result` 格式，`data` 中按字段名提供具体错误信息，例如：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "courseName": "课程名称不能为空",
    "credit": "课程学分必须在 1 到 5 之间"
  }
}
```

数据库连接可通过 DB_HOST、DB_PORT、DB_NAME、DB_USERNAME、DB_PASSWORD 配置；服务端口可通过 SERVER_PORT 配置。
