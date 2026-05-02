<<<<<<< HEAD
我构建了一个基于AI驱动的「户外旅游社交平台智能增强系统」，这是一个面向驴友群体的约伴、景点分享、旅行动态社交应用。
核心痛点有三个：一是用户发布约伴活动时，行程描述质量参差不齐，缺乏专业性和吸引力，导致招募转化率低；二是平台内容审核依赖人工，成本高且存在滞后性，违规内容风险难以管控；三是用户找约伴活动时匹配效率低，缺乏智能推荐能力，优质活动难以触达目标人群。
核心逻辑流采用「多Agent协作+长链推理」架构：
第一层是「智能行程生成Agent」。当用户在发布约伴页面填写目的地、天数、难度等基础信息后，系统触发长链推理流程：首先调用LLM分析目的地特征（地理位置、气候条件、景点分布），然后结合难度等级生成适配的每日行程安排，再推理交通方式、住宿建议、装备清单，最终输出结构化的专业行程描述。整个推理链路包含5-7个推理步骤，确保输出内容的专业性和可执行性。
第二层是「智能内容审核Agent」。采用双模型协作机制：轻量级模型进行实时快速预审（敏感词、图片合规性），深度模型进行异步精细审核（上下文语义分析、隐性违规识别）。两个Agent通过消息队列协作，实现毫秒级响应与深度审核的平衡。
第三层是「智能推荐匹配Agent」。基于用户历史行为数据（浏览、点赞、报名记录），构建用户画像向量，通过相似度计算实现约伴活动的个性化推荐。该Agent与行程生成Agent联动，当系统检测到某类目的地需求上升时，自动调整生成策略以匹配平台内容供给。
技术架构上采用SpringBoot+WebFlux异步响应式架构，AI服务层通过OpenAI兼容API设计，可无缝切换DeepSeek、MiMo、SiliconFlow等国内主流大模型。缓存层使用Redis加速热点数据访问，限流层采用Bucket4j实现AI接口的精细化流量管控，监控层集成Prometheus+Actuator实现全链路可观测。
项目目前后端API服务完整，管理后台基于Vue3+ElementPlus，小程序端基于uni-app跨平台开发。AI功能已深度集成到发布流程，日均预期Token消耗2000-5000万

=======
# 驴友巴巴 - 户外旅游社交平台

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.13-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue.js-3.x-4FC08D" alt="Vue.js">
  <img src="https://img.shields.io/badge/uni--app-2.x-007AFF" alt="uni-app">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

<p align="center">
  <b>基于 AI Agent 驱动的户外旅游约伴社交平台</b>
</p>

## 🎯 项目简介

驴友巴巴是一款面向户外爱好者的旅游社交平台，提供约伴组队、景点分享、旅行动态等功能。平台深度集成 AI 能力，通过智能行程生成、内容审核、个性化推荐等 Agent，提升用户体验和内容质量。

## ✨ 核心功能

### 基础功能
- 🔍 **景点发现** - 浏览热门景点，查看详细介绍、门票、开放时间
- 🤝 **约伴组队** - 发布/加入户外约伴活动，找到志同道合的旅伴
- 📸 **旅行动态** - 分享旅行故事，图文记录精彩瞬间
- 💬 **互动社交** - 点赞、评论、关注，构建户外社交圈

### AI 增强功能

| 功能 | 说明 | 状态 |
|------|------|------|
| 🤖 智能行程生成 | 基于目的地、天数、难度自动生成专业行程 | ✅ 已实现 |
| 🏷️ 智能标签提取 | 自动从动态内容提取关键词标签 | ✅ 已实现 |
| 🛡️ 内容安全审核 | AI 实时检测违规内容 | ✅ 已实现 |
| 📊 智能推荐匹配 | 基于用户画像的个性化推荐 | 🚧 开发中 |

## 🏗️ 技术架构

### 后端 (apiManage)
```
Spring Boot 3.x
├── WebFlux - 异步响应式编程
├── MyBatis Plus - ORM 框架
├── Redis - 缓存与会话
├── MinIO - 对象存储
├── JWT - 身份认证
├── Bucket4j - 限流防护
└── Prometheus + Actuator - 监控告警
```

### 管理后台 (adminManage)
```
Vue 3 + TypeScript
├── Element Plus - UI 组件库
├── Pinia - 状态管理
├── Vue Router - 路由管理
└── Axios - HTTP 客户端
```

### 小程序 (smallProgram)
```
uni-app
├── Vue 3 语法
├── 微信小程序平台
└── 条件编译多平台支持
```

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 18+

### 1. 克隆项目
```bash
git clone https://github.com/jasonshie89/lvyoubaba-platform.git
cd lvyoubaba-platform
```

### 2. 数据库初始化
```bash
mysql -u root -p < apiManage/src/main/resources/sql/database_schema.sql
```

### 3. 配置环境变量
创建 `.env` 文件或在启动时设置：

```bash
# 数据库
export DATABASE_URL=jdbc:mysql://localhost:3306/lvyoubaba
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password

# AI API (支持 OpenAI/DeepSeek/MiMo 等)
export AI_API_KEY=your-api-key
export AI_API_BASE_URL=https://api.deepseek.com/v1
export AI_API_MODEL=deepseek-chat

# MinIO
export MINIO_ENDPOINT=http://localhost:9000
export MINIO_ACCESS_KEY=minioadmin
export MINIO_SECRET_KEY=minioadmin

# JWT
export JWT_SECRET=your-256-bit-secret-key
```

### 4. 启动后端
```bash
cd apiManage
mvn clean install
mvn spring-boot:run
```

后端服务启动在 http://localhost:8080

### 5. 启动管理后台
```bash
cd adminManage
npm install
npm run dev
```

管理后台运行在 http://localhost:5173

### 6. 导入小程序
使用 HBuilderX 打开 `smallProgram` 目录，配置小程序 AppID，点击运行到微信小程序模拟器。

## 📡 API 接口

### AI 服务接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/generate-itinerary` | POST | 智能生成行程安排 |
| `/api/ai/extract-tags` | POST | 提取内容标签 |
| `/api/ai/check-content` | POST | 内容安全审核 |

### 核心业务接口

| 接口 | 说明 |
|------|------|
| `/api/scenic-spot/**` | 景点管理 |
| `/api/team/**` | 约伴活动 |
| `/api/activity/**` | 旅行动态 |
| `/api/file/**` | 文件上传 |

### 监控端点

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/metrics` | 应用指标 |
| `/actuator/prometheus` | Prometheus 指标 |

## 🔒 安全特性

- ✅ JWT 身份认证
- ✅ 接口限流防护（Bucket4j）
- ✅ SQL 注入防护（MyBatis Plus）
- ✅ XSS 过滤
- ✅ 操作日志审计（AOP）
- ✅ 敏感配置环境变量化

## 📊 监控与运维

### 健康检查
```bash
curl http://localhost:8080/actuator/health
```

### 查看指标
```bash
curl http://localhost:8080/actuator/metrics
```

### Prometheus 接入
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'lvyoubaba-api'
    static_configs:
      - targets: ['localhost:8080']
```

## 🧪 测试

```bash
cd apiManage
mvn test
```

## 📁 项目结构

```
lvyoubaba-platform/
├── apiManage/               # 后端 API 服务
│   ├── src/main/java/
│   │   └── org/example/apimanage/
│   │       ├── controller/  # 控制器
│   │       ├── service/     # 业务逻辑
│   │       ├── mapper/      # 数据访问
│   │       ├── entity/      # 实体类
│   │       ├── config/      # 配置类
│   │       ├── aspect/      # AOP 切面
│   │       └── utils/       # 工具类
│   └── src/main/resources/
│       └── application.properties
│
├── adminManage/             # 管理后台
│   ├── src/
│   │   ├── api/            # API 请求
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   └── utils/          # 工具函数
│   └── package.json
│
└── smallProgram/           # 微信小程序
    ├── pages/              # 页面
    ├── api/                # API 请求
    └── static/             # 静态资源
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

## 📄 许可证

本项目基于 [MIT](LICENSE) 许可证开源。

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [uni-app](https://uniapp.dcloud.net.cn/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)

---

<p align="center">
  Made with ❤️ for outdoor enthusiasts
</p>
>>>>>>> 0773c8e (docs: add comprehensive README)
