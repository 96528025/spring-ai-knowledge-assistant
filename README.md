# Spring AI Knowledge Assistant

A Spring Boot GenAI service for document-grounded Q&A: upload documents, then ask
natural-language questions answered from their content — with a pluggable AI layer
that swaps between a mock client (for hermetic tests) and a real OpenAI-compatible
provider.

**✅ 5/5 automated controller tests passing** (`./mvnw test`, no API key required).

```bash
# Ask a question against uploaded documents (mock mode, no key needed)
curl -X POST localhost:8080/api/questions/ask \
  -H 'Content-Type: application/json' \
  -d '{"question": "What is the refund policy?"}'
```

This project demonstrates how to:

- upload knowledge documents to a backend service
- store document content in memory
- ask natural-language questions about uploaded content
- switch between a mock AI client and a real OpenAI-compatible provider
- verify core behavior with automated tests

## Why This Project Exists

This project is designed to match the kind of backend and AI integration work described in roles like the Five9 AI internship:

- Java backend development with Spring Boot
- API integration with AI vendors
- document-driven question answering workflows
- automated testing
- clear system design and documentation

The goal is not to build a production-ready RAG platform in one step. The goal is to build a clean, explainable MVP that can be extended over time.

## Current Features

- `GET /api/health` health-check endpoint
- `POST /api/documents/upload` upload a text-based knowledge document
- `GET /api/documents` inspect uploaded documents currently stored in memory
- `POST /api/questions/ask` ask a question about uploaded content
- pluggable AI layer through the `AiClient` interface
- default mock AI mode for local development and stable tests
- real OpenAI-compatible mode for live provider calls
- automated controller tests with Spring Boot and MockMvc

## Tech Stack

- Java 21 target
- Spring Boot 3.4.5
- Maven Wrapper
- JUnit 5
- Spring MockMvc
- OpenAI-compatible Chat Completions API

## Project Structure

```text
spring-ai-demo/
├── .mvn/
├── mvnw
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java/com/angelren/springaidemo
    │   │   ├── SpringAiDemoApplication.java
    │   │   ├── ai
    │   │   │   ├── AiClient.java
    │   │   │   ├── MockAiClient.java
    │   │   │   └── OpenAiCompatibleClient.java
    │   │   ├── config
    │   │   │   ├── AiClientConfig.java
    │   │   │   └── AiProperties.java
    │   │   ├── controller
    │   │   │   ├── DocumentController.java
    │   │   │   ├── HealthController.java
    │   │   │   └── QuestionController.java
    │   │   ├── dto
    │   │   │   └── AskQuestionRequest.java
    │   │   ├── model
    │   │   │   └── DocumentRecord.java
    │   │   └── service
    │   │       ├── DocumentService.java
    │   │       └── QuestionAnswerService.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java/com/angelren/springaidemo/controller
            ├── DocumentControllerTest.java
            ├── HealthControllerTest.java
            └── QuestionControllerTest.java
```

## Architecture Overview

### 1. Document ingestion

The document upload endpoint accepts a file and passes it to `DocumentService`.

`DocumentService`:

- reads the file as UTF-8 text
- creates a `DocumentRecord`
- stores the document in an in-memory list

This is intentionally simple for MVP speed. It makes the system easy to explain and easy to test.

### 2. Question answering flow

When a user submits a question:

1. `QuestionController` receives the HTTP request.
2. `QuestionAnswerService` loads the uploaded documents.
3. The service combines document content into a single context string.
4. The service calls the `AiClient` abstraction.
5. The selected AI client returns an answer.
6. The controller returns the answer as JSON.

### 3. AI provider abstraction

The `AiClient` interface decouples business logic from a specific vendor.

Current implementations:

- `MockAiClient`
  Used by default for local development and stable automated tests.

- `OpenAiCompatibleClient`
  Sends requests to an OpenAI-compatible `/chat/completions` endpoint.

`AiClientConfig` decides which implementation to use based on configuration.

This design makes it easier to:

- test the system without external dependencies
- switch providers later
- support multiple vendors in future iterations

## API Endpoints

### Health check

`GET /api/health`

Example response:

```json
{
  "status": "ok",
  "service": "spring-ai-demo"
}
```

### Upload a document

`POST /api/documents/upload`

Form field:

- `file`

Example using `curl`:

```bash
curl -X POST http://localhost:8080/api/documents/upload \
  -F "file=@/Users/angelren/Documents/projects/spring-ai-demo/README.md"
```

Example response:

```json
{
  "fileName": "README.md",
  "uploadedAt": "2026-05-02T20:02:24.899256Z",
  "id": "975b38b6-8645-4a77-bed8-6d48d2aef3b2"
}
```

### List uploaded documents

`GET /api/documents`

Example using `curl`:

```bash
curl http://localhost:8080/api/documents
```

### Ask a question

`POST /api/questions/ask`

Example request:

```bash
curl -X POST http://localhost:8080/api/questions/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"What is this project about?"}'
```

Example response:

```json
{
  "answer": "This project is a minimal Spring Boot starter for a GenAI knowledge assistant...",
  "question": "What is this project about?"
}
```

## Running The Project

### 1. Go to the project directory

```bash
cd /Users/angelren/Documents/projects/spring-ai-demo
```

### 2. Run tests

```bash
./mvnw test
```

### 3. Start the application

```bash
./mvnw spring-boot:run
```

By default, the app runs on:

- `http://localhost:8080`

## AI Configuration

The application supports two modes:

### Mock mode

Use this when you want:

- local development without API cost
- deterministic behavior
- stable automated tests

Relevant setting:

```properties
app.ai.enabled=false
```

### Real OpenAI-compatible mode

Use this when you want:

- live model responses
- end-to-end provider integration
- a stronger demo for project presentation

Relevant settings in [`application.properties`](/Users/angelren/Documents/projects/spring-ai-demo/src/main/resources/application.properties):

```properties
app.ai.enabled=true
app.ai.base-url=https://api.openai.com/v1
app.ai.api-key=${OPENAI_API_KEY:}
app.ai.model=gpt-4o-mini
```

Before starting the app, set your API key in the terminal:

```bash
export OPENAI_API_KEY="your-real-api-key"
```

Then run:

```bash
./mvnw spring-boot:run
```

For Azure OpenAI or another compatible provider, update:

- `app.ai.base-url`
- `app.ai.model`

to match that provider's OpenAI-compatible endpoint and deployed model name.

## Testing

Current automated tests cover:

- application health endpoint
- document upload behavior
- document listing behavior
- question endpoint validation
- question-answer flow through the mock AI client

Run all tests with:

```bash
./mvnw test
```

Important testing note:

- tests use the mock AI path instead of live provider calls
- this keeps tests fast, deterministic, and free of external API cost
- in-memory document state is cleared before each relevant test to avoid test interference

## What This Project Demonstrates

From an interview perspective, this project shows:

- Spring Boot backend fundamentals
- layered backend design using controller/service/config separation
- file upload handling
- API integration with an external AI provider
- use of interfaces to decouple business logic from vendors
- environment-based configuration
- automated testing and test isolation
- ability to document engineering tradeoffs clearly

## Current Limitations

This is an MVP, so several simplifications are intentional:

- uploaded documents are stored only in memory
- data is lost when the app restarts
- all documents are concatenated into one context string
- there is no chunking, ranking, or vector retrieval yet
- there is no PDF parsing yet
- there is no frontend UI for this demo yet
- there is no persistence layer such as MySQL or PostgreSQL

These are acceptable tradeoffs for an internship-ready demo because they keep the core architecture easy to understand and extend.

## Suggested Next Steps

If you continue improving the project, strong next steps would be:

- support PDF upload and extraction
- add DTOs for all API responses
- improve prompt construction and provider error handling
- add document chunking before question answering
- introduce a persistent database
- add keyword retrieval or vector search
- add a simple React frontend
- add tests for the real AI client with mocked HTTP responses

## Resume-Friendly Project Summary

If you want to describe this project on a resume or in an interview, a concise version could be:

Built a Spring Boot GenAI knowledge assistant that supports document upload, question answering, and pluggable AI provider integration through a mock and OpenAI-compatible client architecture. Added automated endpoint tests with MockMvc and used environment-based configuration for secure API key handling.

---

## 中文说明

这个项目是一个用于实习准备的最小可用 GenAI 后端 demo，核心目标是用 Spring Boot 做出一条清晰、可解释、可测试的 AI 问答链路。

它目前已经实现了这些能力：

- 上传知识文档到后端
- 把文档内容暂时存放在内存中
- 针对已上传文档发起自然语言提问
- 支持在 mock AI 和真实 OpenAI-compatible provider 之间切换
- 通过自动化测试验证核心接口行为

### 为什么做这个项目

这个项目是为了贴近 AI / 后端实习岗位常见要求而设计的，尤其适合用来展示下面这些能力：

- Java 和 Spring Boot 后端开发
- 第三方 AI vendor API 集成
- 文档驱动的问答流程
- 自动化测试和测试隔离
- 工程化的分层设计与文档能力

这个项目的目标不是一步到位做成完整生产级 RAG 平台，而是先做一个结构清晰、容易解释、可以逐步扩展的 MVP。

### 当前项目结构怎么理解

你可以把它理解成 4 层：

- `controller`
  负责接收 HTTP 请求，返回 JSON 响应

- `service`
  负责业务逻辑，比如保存文档、组织上下文、调用 AI 客户端

- `ai`
  负责抽象 AI 调用能力

- `config`
  负责根据配置决定当前使用 mock AI 还是真实 AI provider

### 当前问答流程

问答请求的执行过程是这样的：

1. 用户调用 `POST /api/questions/ask`
2. `QuestionController` 接收问题
3. `QuestionAnswerService` 读取当前已经上传的文档
4. 服务层把所有文档内容拼成一个 context
5. 服务层调用 `AiClient`
6. 当前启用的 AI 实现返回答案
7. Controller 把答案包装成 JSON 返回给用户

### AI 抽象层为什么重要

项目里最关键的设计之一是 `AiClient` 接口。

它的意义是：

- 业务逻辑不需要依赖某一个固定厂商
- 本地开发时可以用 `MockAiClient`
- 演示真实能力时可以切到 `OpenAiCompatibleClient`
- 以后如果要接 Azure OpenAI 或其他兼容 OpenAI 接口的服务，改动会更小

这是一种很典型、也很适合面试讲解的可替换架构设计。

### 当前接口

- `GET /api/health`
  健康检查接口

- `POST /api/documents/upload`
  上传文档接口

- `GET /api/documents`
  查看当前内存中的已上传文档

- `POST /api/questions/ask`
  提问接口

### 如何运行项目

先进入项目目录：

```bash
cd /Users/angelren/Documents/projects/spring-ai-demo
```

运行测试：

```bash
./mvnw test
```

启动应用：

```bash
./mvnw spring-boot:run
```

默认服务地址：

- `http://localhost:8080`

### 如何切换到真实 OpenAI 模式

在 `application.properties` 中，当前真实 AI 开关由下面这个配置控制：

```properties
app.ai.enabled=true
```

在启动应用之前，需要先在 terminal 中设置环境变量：

```bash
export OPENAI_API_KEY="你的真实 API key"
```

然后再运行：

```bash
./mvnw spring-boot:run
```

如果你想改成其他 OpenAI-compatible provider，也可以调整：

- `app.ai.base-url`
- `app.ai.model`

### 当前测试覆盖了什么

项目现在的自动化测试主要覆盖：

- 健康检查接口
- 文档上传接口
- 文档列表接口
- 提问接口的基本校验
- 通过 mock AI 路径的问答流程

测试默认不依赖真实 OpenAI 调用，这样做有几个好处：

- 更稳定
- 更快
- 没有外部 API 成本
- 更适合持续开发

### 当前项目的限制

这个版本是 MVP，所以目前有一些有意识保留的简化：

- 文档只存在内存中，应用重启后会丢失
- 所有文档会被直接拼接成一个大 context
- 还没有做 chunking
- 还没有做向量检索
- 还没有支持 PDF 解析
- 还没有数据库持久化
- 还没有前端页面

这些限制并不代表项目不好，反而说明这是一个阶段性设计：先把主流程做通，再逐步增强能力。

### 这个项目适合怎么讲给面试官

你可以把它描述成：

- 我做了一个基于 Spring Boot 的 GenAI knowledge assistant demo
- 它支持文档上传、问答接口和 OpenAI-compatible provider integration
- 我专门做了 AI 抽象层，让 mock 和真实 provider 可以切换
- 我写了自动化测试来保证接口行为和重构稳定性
- 当前版本是 MVP，后续可以扩展 PDF、数据库和检索能力

### 下一步适合继续做什么

如果你想继续增强这个项目，比较有价值的方向有：

- 支持 PDF 上传和文本提取
- 为 API 响应增加更正式的 DTO
- 优化 prompt 构造和错误处理
- 为文档做 chunking
- 接入数据库做持久化
- 增加关键词检索或向量检索
- 加一个简单 React 前端
- 给真实 AI client 增加 mock HTTP 测试
