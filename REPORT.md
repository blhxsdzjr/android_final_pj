# Android 应用开发实验报告 - 校园助手 (Campus Assistant)

**项目名称**：校园助手 (Campus Assistant)  
**开发环境**：Android Studio, Java, Room Database  
**日期**：2025年12月25日

---

## 第一章 项目概述
本项目旨在开发一款服务于大学生的校园助手应用，集成了课程表管理、待办事项提醒、以及校园集市交流功能。通过该应用，学生可以方便地查看每日课表、管理个人任务，并在校园集市中发布动态或交易信息，实现校内信息的互通与共享。

## 第二章 系统需求分析
### 2.1 系统功能模块总述
本系统设计为一款综合性的校园生活辅助工具，包含以下六大核心功能模块：

1.  **用户权限与安全模块**：
    *   **身份验证**：提供本地注册与登录功能，基于 SQLite 存储加密凭证。
    *   **权限控制**：实施严格的游客拦截机制，未登录用户无法访问核心功能区。
    *   **数据隔离**：确保用户数据的私密性，不同用户的配置互不干扰。

2.  **智能首页仪表盘 (Dashboard)**：
    *   **今日概览**：根据系统日期自动计算并展示“当日课程表”，解决传统课表查看繁琐的问题。
    *   **待办摘要**：聚合展示未完成的待办事项，按截止时间排序，提供高效的时间管理入口。

3.  **课程与日程管理模块**：
    *   **课程录入**：支持手动添加课程信息，包含课程名、教室、教师、周次范围及节次信息。
    *   **任务管理**：支持创建待办事项，内置日期与时间选择器（DatePicker/TimePicker）。
    *   **智能提醒**：集成 Android `AlarmManager`，为待办事项提供精确的系统级闹钟提醒服务。

4.  **校园集市社区 (Community)**：
    *   **信息流**：以时间倒序展示全校同学发布的动态（失物招领、二手交易等）。
    *   **互动体系**：支持点赞、评论及**多级嵌套回复**（楼中楼），构建活跃的社区氛围。
    *   **内容发布**：支持用户快捷发布包含标题与正文的集市动态。

5.  **AI 校园助手 (Discovery)**：
    *   **智能对话**：内置基于 LLM（大语言模型）的对话界面，支持自然语言交互。
    *   **自定义配置**：支持用户在设置中自定义 API Key、Endpoint URL 和模型名称，兼容 OpenAI 格式接口。

6.  **校园地图服务 (Map)**：
    *   **高德地图集成**：嵌入高德地图 SDK，提供流畅的地图浏览体验。
    *   **POI 检索**：内置校园关键地点（如图书馆、食堂、教学楼）的标记（Marker）。
    *   **地理编码搜索**：支持通过文本搜索具体的校园地址或周边位置，并自动定位。

### 2.2 数据需求
*   **用户数据**：用户名、密码。
*   **课程数据**：课程名、教室、教师、周几、节次信息。
*   **待办数据**：内容、截止时间、完成状态。
*   **集市数据**：
    *   帖子：标题、内容、作者、发布时间、点赞数。
    *   评论：所属帖子ID、父评论ID（用于嵌套）、内容、作者、时间。

---

## 第三章 系统设计

### 3.1 系统总体架构
本系统采用分层架构设计，遵循“高内聚、低耦合”的原则，确保系统的可维护性与扩展性。整体架构主要分为三层：

1.  **表现层 (Presentation Layer)**：
    *   采用 **Single Activity + Multi-Fragment** 模式。`MainActivity` 作为全局容器，通过 Android Jetpack 的 **Navigation Component** 管理 `HomeFragment`, `DiscoveryFragment`, `CourseTableFragment`, `ProfileFragment` 等核心功能页面的路由跳转。
    *   UI 组件遵循 Material Design 规范，大量使用 `CoordinatorLayout`, `NestedScrollView`, `CardView` 等现代化控件。

2.  **业务逻辑层 (Business Logic Layer)**：
    *   负责处理核心业务规则。例如 `HomeFragment` 中的课程筛选逻辑（根据日期计算周次）、`DiscoveryFragment` 中的 AI 对话上下文管理、以及 `AlarmReceiver` 中的定时提醒分发逻辑。
    *   网络请求模块封装了 `OkHttp` 客户端，负责与 AI 大模型接口进行异步通信及错误处理。

3.  **数据持久层 (Data Persistence Layer)**：
    *   **SQLite (Room)**：作为核心数据库，存储用户、课程、待办及集市数据。通过 DAO (Data Access Object) 接口隔离 SQL 细节。
    *   **SharedPreferences**：用于存储轻量级配置信息，如用户登录状态、AI API Key 配置等。

### 3.2 数据库详细设计
系统基于 Android Jetpack Room 框架构建了 `version 6` 的关系型数据库。包含以下核心实体及关系：

#### 3.2.1 实体定义
1.  **User (用户表)**：
    *   `id`: 主键，自增。
    *   `username`: 用户名（唯一标识）。
    *   `password`: 加密存储的用户凭证。

2.  **Course (课程表)**：
    *   核心字段：`name` (课程名), `room` (教室), `teacher` (教师), `dayOfWeek` (周几), `startPeriod` (开始节次), `endPeriod` (结束节次)。
    *   支持复杂的教学周期描述（起始周/结束周）。

3.  **Todo (待办事项表)**：
    *   字段：`content` (内容), `deadlineTime` (截止时间字符串), `isDone` (完成状态), `createdTime` (创建时间)。
    *   该表数据变化会触发系统 `AlarmManager` 的增删改操作。

4.  **MarketPost (集市帖子表)**：
    *   字段：`title` (标题), `content` (正文), `username` (发布者), `postTime` (发布时间), `likeCount` (点赞数)。

5.  **MarketComment (集市评论表)**：
    *   `id`: 主键。
    *   `postId`: **外键**，关联 `MarketPost` 表的 `id`。
    *   `parentId`: **自引用外键**，关联本表的 `id`，用于实现**多级嵌套回复（楼中楼）**结构。若为 0 或 null 则表示根评论。

#### 3.2.2 关键约束
*   **级联删除 (OnDelete = CASCADE)**：配置在 `MarketComment` 表的 `postId` 外键上。当某个帖子被删除时，数据库引擎会自动清理该帖子下的所有评论数据，防止孤儿数据产生，维护数据完整性。

### 3.3 关键技术选型
系统集成多种成熟的开源库与 SDK 以实现复杂功能：

1.  **Android Jetpack 组件**：
    *   **Room Database**: 提供类型安全的 SQLite 抽象层，支持编译时 SQL 检查。
    *   **Navigation**: 简化 Fragment 间的导航与参数传递。
    *   **Lifecycle**: 确保后台任务（如定位、网络请求）能够感知组件生命周期，避免内存泄漏。

2.  **第三方 SDK 与库**：
    *   **高德地图 SDK (AMap)**：提供核心的地图显示、定位服务以及 POI (Point of Interest) 检索与逆地理编码功能。
    *   **OkHttp 3**: 高效的 HTTP 客户端，用于构建 AI 助手的网络通信模块，支持连接池与超时重连。
    *   **Gson**: Google 提供的 JSON 序列化/反序列化库，用于解析 AI 接口返回的复杂 JSON 数据结构。

### 3.4 模块交互流程

#### 3.4.1 用户鉴权流程
系统启动时，`SplashActivity` 首先读取 `SharedPreferences` 中的登录标记：
*   若标记存在，直接跳转至 `MainActivity`。
*   若不存在，跳转至 `LoginActivity`。
*   这种设计在应用入口处即完成了流量分发，保障了系统的安全性。

#### 3.4.2 校园集市发布流程
用户点击发布 -> `AddPostActivity` 校验输入 -> 调用后台线程执行 `marketDao().insert()` -> 插入成功后 `runOnUiThread` 回调 -> 关闭页面并刷新 `MarketActivity` 列表。

#### 3.4.3 AI 智能问答流程
1.  **输入**：用户在 `DiscoveryFragment` 输入问题并点击发送。
2.  **封装**：系统从配置中读取 API Key，将历史上下文封装为 JSON 格式。
3.  **请求**：通过 `OkHttp` 开启子线程发起 POST 请求。
4.  **解析**：收到响应后使用 `Gson` 解析 `choices[0].message.content` 字段。
5.  **展示**：通过 `Handler` 或 `runOnUiThread` 将 AI 回复追加到 `ChatAdapter` 并滚动到底部。

---

## 第四章 项目亮点与创新点

### 4.1 可配置化的 AI 校园助手
本项目不仅仅是简单接入了一个聊天机器人，而是构建了一个**通用的 LLM（大语言模型）客户端框架**。
*   **灵活配置**：在“个人中心”模块，用户可以自定义 API Key、Endpoint URL 和模型名称。这意味着系统不仅支持 OpenAI，还完美兼容 DeepSeek、Moonshot (Kimi) 等所有支持 OpenAI 接口规范的国产大模型。
*   **上下文感知**：通过在请求中封装历史对话列表 (`messages` 数组)，实现了具备记忆能力的连续对话体验，能够更好地辅助学生解决复杂的学业问题。

### 4.2 桌面课程小组件 (App Widget)
为了让用户“零步操作”获取核心信息，系统开发了 Android 原生 **App Widget**。
*   **即时直达**：用户无需打开 App，即可在手机桌面上查看当天的课程列表。
*   **实时同步**：当用户在 App 内添加或修改课程后，系统会发送广播 (`Broadcast`) 触发 `RemoteViewsService` 更新，确保桌面显示的数据永远是最新的。这一功能极大地提升了应用的用户粘性和便捷性。

### 4.3 系统级精准提醒机制
在待办事项模块中，系统并没有止步于简单的列表记录，而是深度集成了 Android **AlarmManager** 服务。
*   **强提醒**：通过设置 `RTC_WAKEUP` 类型的精确闹钟，即使应用处于后台或手机处于休眠状态，系统也能准时唤醒设备并发送通知。
*   **任务闭环**：从创建任务 -> 设置时间 -> 系统提醒 -> 标记完成，构建了完整的任务管理闭环。

### 4.4 深度集成的校园地图服务
基于高德地图 SDK，系统实现了定制化的校园LBS（基于位置的服务）功能。
*   **POI 精准检索**：利用 `GeocodeSearch` 实现了从“地点名称”到“经纬度坐标”的精准解析，帮助新生快速找到教学楼、食堂等关键设施。
*   **逆地理编码**：支持点击地图任意点解析出详细的结构化地址。
*   **一键回校**：内置“学术交流中心”等核心地标的坐标，提供一键复位功能，优化了地图操作体验。

### 4.5 支持无限层级的社区评论系统
这是本项目在数据结构与算法层面的最大亮点。针对校园集市的互动需求，系统突破了传统扁平化评论的限制。
*   **树形结构存储**：数据库层面采用自引用设计存储评论树。
*   **递归渲染算法**：在前端实现了一套高效的递归展平算法（Flattening Algorithm），将复杂的树状数据转换为线性列表，并通过计算 `depth` 属性动态调整 UI 缩进。这使得用户可以进行类似 Reddit 或 贴吧 的“楼中楼”式深度探讨。

### 4.6 智能首页聚合算法
系统摒弃了死板的菜单式导航，采用了“Dashboard（仪表盘）”的设计理念。
*   **动态计算**：首页加载时，会自动获取系统当前的 `Calendar` 日期，换算为教学周次和星期几，并据此构建 SQL 查询条件。
*   **并发加载**：利用线程池并行查询课程表和待办表，最后统一刷新 UI。这种“即开即用、所见即所得”的设计极大地减少了用户获取关键信息的时间成本。


---

## 第五章 系统重难点与解决方案

### 5.1 算法难点：树形评论数据的扁平化处理
**问题描述**：
校园集市的评论区支持“楼中楼”回复，逻辑上构成了多棵多叉树（Forest of Trees）。然而，Android 的 `RecyclerView` 仅能渲染线性列表。如何高效地将树形结构转换为线性列表，并在视觉上保留层级关系，是本项目的核心算法难点。

**解决方案**：
采用了 **DFS（深度优先搜索）递归展平算法**。
1.  **预处理**：首先将所有评论数据按 `parentId` 分组，构建邻接表 `Map<Integer, List<Comment>>`。
2.  **递归构建**：从所有根节点（`parentId=0`）出发，执行 DFS 遍历。在遍历过程中，维护一个 `depth` 指针。
3.  **封装节点**：将遍历到的每个评论封装为 `CommentNode` 对象，显式记录其当前深度。
4.  **UI 映射**：在 `Adapter` 中，根据节点的 `depth` 属性动态计算 `paddingStart`（例如：`depth * 48dp`），从而在扁平的列表中完美复现了树状缩进效果。

### 5.2 架构难点：Fragment 生命周期与异步回调安全
**问题描述**：
项目大量使用 `Executors` 线程池进行数据库查询和网络请求。在异步任务结束准备回调主线程更新 UI 时，如果此时用户已经退出了该界面（Fragment 已销毁），直接调用 `tvResult.setText()` 会导致 `NullPointerException` 或 `IllegalStateException`，造成应用崩溃。

**解决方案**：
建立**生命周期感知 (Lifecycle-Aware)** 的回调机制。
*   在 `runOnUiThread` 执行前，严格检查 Fragment 的状态：`if (isAdded() && getContext() != null)`。
*   使用 `WeakReference`（虽然本项目主要采用状态检查）或 Jetpack 的 `LiveData` 来确保数据推送的安全性。这一机制保证了在弱网环境或慢速 IO 操作下，应用的稳定性不受影响。

### 5.3 UI 交互难点：多层嵌套滚动的冲突处理
**问题描述**：
首页 (`HomeFragment`) 需要在一个整体可滚动的页面中，垂直排列“今日课程”和“待办事项”两个列表。若直接将两个 `RecyclerView` 放入 `ScrollView`，会导致滑动冲突：内部列表抢占滑动事件，导致外部无法滚动，或者内部列表高度计算错误只显示一行。

**解决方案**：
*   **容器选择**：最外层使用 `NestedScrollView`，它实现了 Android 的嵌套滑动接口 (`NestedScrollingParent`)。
*   **事件让渡**：对内部的 `RecyclerView` 调用 `setNestedScrollingEnabled(false)`。这指示内部列表放弃处理自身的滑动事件，而是将自身完整内容的高度（Wrap Content）暴露给父容器，由 `NestedScrollView` 统一管理整个页面的惯性滑动。

### 5.4 组件通信难点：App Widget 的跨进程数据同步
**问题描述**：
桌面小组件 (`AppWidget`) 运行在 Launcher 进程中，而主应用运行在独立的 App 进程中。当用户在 App 内添加了新课程后，桌面组件并不会自动感知数据变化，导致显示滞后。

**解决方案**：
利用 **Android 广播机制 (Broadcast)** 实现跨进程通信 (IPC)。
*   在 `AddCourseActivity` 保存数据成功后，发送一条带有特定 Action (`ACTION_APPWIDGET_UPDATE`) 的广播。
*   `CourseWidgetProvider` 接收到广播后，主动触发 `AppWidgetManager.notifyAppWidgetViewDataChanged()`。
*   这强制 `RemoteViewsFactory` 重新查询数据库（Room 支持多进程读取），从而实现桌面数据的毫秒级实时刷新。

### 5.5 网络难点：通用 LLM 接口的动态配置与容错
**问题描述**：
为了支持用户自定义 AI 模型（如切换 OpenAI、DeepSeek、Kimi），系统不能硬编码 API 域名。这要求网络层能够处理动态变化的 URL 和不可预知的 API 响应格式。

**解决方案**：
*   **动态构建 Client**：不使用单例模式写死 BaseUrl，而是每次请求时根据 `SharedPreferences` 中的用户配置动态构建 `Request` 对象。
*   **鲁棒的错误解析**：针对不同厂商 API 返回的 Error JSON 格式差异（有的在 `error.message`，有的在 `msg`），编写了兼容性解析逻辑。无论用户配置出错还是 Key 过期，系统都能从原始 JSON 中提取出可读的错误提示展示给用户，而非直接崩溃。

---

## 第六章 版本适配与兼容性测试

随着 Android 系统的不断迭代，API 差异和权限变更对应用开发提出了挑战。本项目在开发过程中，针对主流的高版本 Android 系统进行了针对性的适配工作，以确保应用在不同设备上的稳定性。

### 6.1 Android 13 (API 33) 通知权限适配
**问题**：从 Android 13 开始，发送通知不再是默认权限，需要动态申请 `POST_NOTIFICATIONS`。
**适配方案**：
在 `MainActivity` 的 `onCreate` 生命周期中增加了版本判断逻辑：
```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
    }
}
```
此逻辑确保了应用在 Android 13+ 设备上能够正常推送待办事项提醒。

### 6.2 Android 12 (API 31) 精确闹钟适配
**问题**：为了节省电量，Android 12 限制了应用默认使用 `AlarmManager` 设置精确闹钟的能力。
**适配方案**：
在 `AddTodoActivity` 中设置提醒前，增加了 `canScheduleExactAlarms()` 的检查逻辑。
*   对于 Android 12 及以上版本，先检查权限。
*   若无权限，则回退到非精确闹钟 (`set()`) 或引导用户去设置页面开启权限（本项目采用自动降级策略，使用 `set()` 保证程序不崩溃）。
*   同时，在 `PendingIntent` 的构建中显式指定了 `FLAG_IMMUTABLE`，以符合 Android 12+ 的安全规范。

### 6.3 屏幕适配策略
*   **单位规范**：全项目布局严格使用 `dp` 定义控件尺寸，使用 `sp` 定义文字大小，确保在不同分辨率（ldpi 到 xxxhdpi）设备上显示一致。
*   **弹性布局**：大量使用 `LinearLayout` 的 `layout_weight` 属性（如帖子详情页底部的输入框）和 `ConstraintLayout`，确保界面能够自适应全面屏、刘海屏等各种异形屏幕。

---

## 第七章 课程总结与展望

### 7.1 技术收获与成长
通过本次《移动应用开发》课程的大作业实践，我从零开始构建了一个功能完备的 Android 应用，实现了从理论知识到工程实践的跨越：
1.  **架构掌控力**：深入理解了 Android 四大组件的协同工作机制，熟练运用 **Single Activity + Navigation** 的现代化架构，告别了混乱的 Activity 跳转。
2.  **数据驾驭力**：掌握了 **Jetpack Room** 数据库的高级用法，包括多表关联、外键约束、数据迁移以及复杂的异步查询策略。
3.  **UI 构建力**：能够利用 `RecyclerView` 配合多种 `Adapter` 模式实现复杂列表（如树形评论），并解决了 `NestedScrollView` 滑动冲突等“疑难杂症”。
4.  **全栈视野**：通过集成 AI 大模型接口和高德地图 SDK，通过 HTTP 协议与第三方服务交互，拓展了单纯客户端开发的视野。

### 7.2 工程化思维的建立
除了代码编写，本次实验更让我体会到了**软件工程**的重要性：
*   **版本控制**：习惯了使用 Git 进行代码管理，每一次功能提交都有清晰的 Commit Message。
*   **模块化设计**：将 UI、数据、逻辑分离，提高了代码的可读性和复用性。
*   **文档能力**：学会了撰写详尽的 Technical Report，能够清晰地阐述系统设计思路和难点解决方案。

### 7.3 未来展望
虽然目前的应用已经具备了基础的实用性，但仍有优化空间：
1.  **云端同步**：引入后端服务（如 Spring Boot + MySQL），利用 Retrofit 实现数据的云端备份与多端同步。
2.  **RAG 增强 (AI 2.0)**：结合 RAG (检索增强生成) 技术，让 AI 助手能够读取本地的课程表和待办数据，实现“明天我有几节课？”的个性化智能回答。
3.  **即时通讯**：将校园集市升级为实时社区，引入 WebSocket 实现私信功能。

**结语**：本次实验不仅是一次代码的堆砌，更是一次对移动互联网产品开发全流程的深度体验。我将以此为起点，继续探索 Android 开发的广阔天地。
