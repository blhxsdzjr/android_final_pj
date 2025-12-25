# Campus Assistant Project - To-Do List
# 校园助手项目 - 待办事项列表

This document tracks the development progress of the "Campus Assistant" Android project.
本文档跟踪“校园助手”Android 项目的开发进度。

## Phase 1: Project Setup & Basic UI Architecture (Week 1)
## 第一阶段：项目设置与基础 UI 架构（第一周）
- [ ] **Project Initialization**
    - [ ] **项目初始化**
    - [ ] Create new Android project (Min SDK 24).
        - [ ] 创建新的 Android 项目（最低 SDK 24）。
    - [ ] Configure `build.gradle` (dependencies for navigation, networking, database, etc.).
        - [ ] 配置 `build.gradle`（导航、网络、数据库等依赖项）。
    - [ ] Set up Git repository and initial commit.
        - [ ] 设置 Git 仓库并进行初次提交。
- [ ] **Navigation Framework**
    - [ ] **导航框架**
    - [ ] Implement `MainActivity` with `BottomNavigationView` or `TabLayout`.
        - [ ] 使用 `BottomNavigationView` 或 `TabLayout` 实现 `MainActivity`。
    - [ ] Create placeholder Fragments for main modules:
        - [ ] 为主要模块创建占位 Fragment：
        - [x] `HomeFragment` (Dashboard)
            - [x] `HomeFragment`（仪表盘）
            - [x] **Requirement**: Display today's course schedule and non-completed to-do list.
                - [x] **需求**：显示当天的课表和未完成的待办事项。
        - [ ] `MapFragment` (Campus Map)
            - [ ] `MapFragment`（校园地图）
        - [ ] `ScheduleFragment` (Calendar/Todo)
            - [ ] `ScheduleFragment`（日历/待办）
        - [ ] `DiscoveryFragment` (News/AI)
            - [ ] `DiscoveryFragment`（发现/AI）
        - [ ] `ProfileFragment` (User Settings)
            - [ ] `ProfileFragment`（用户设置）
    - [ ] Implement navigation logic (switching between fragments).
        - [ ] 实现导航逻辑（在 Fragment 之间切换）。
- [x] **User Login Module**
    - [x] **用户登录模块**
    - [x] **Requirement**: Guests cannot access; logged-in users can access; no admin role needed.
        - [x] **需求**：游客不可访问；登录用户可访问；无需管理员角色。
    - [x] Create `User` Entity and `UserDao`.
        - [x] 创建 `User` 实体和 `UserDao`。
    - [x] Implement `LoginActivity` and `RegisterActivity`.
        - [x] 实现 `LoginActivity` 和 `RegisterActivity`。
    - [x] Update `SplashActivity` to redirect to Login if not authenticated.
        - [x] 更新 `SplashActivity`，如果未验证则重定向到登录页面。
- [ ] **Login/Startup (Optional/Basic)**
    - [ ] **登录/启动（可选/基础）**
    - [ ] Design a simple Splash Screen.
        - [ ] 设计简单的启动画面。
    - [ ] Create a Login Activity (store "isLoggedIn" in SharedPreferences).
        - [ ] 创建登录 Activity（在 SharedPreferences 中存储“已登录”状态）。

## Phase 2: Course Schedule Module (Week 2)
## 第二阶段：课程表模块（第二周）
- [ ] **Database Setup**
    - [ ] **数据库设置**
    - [ ] Define `Course` entity (id, name, room, teacher, dayOfWeek, startPeriod, endPeriod).
        - [ ] 定义 `Course` 实体（ID、名称、教室、教师、周几、开始节次、结束节次）。
    - [ ] Implement Database Helper (SQLiteOpenHelper or Room).
        - [ ] 实现数据库助手（SQLiteOpenHelper 或 Room）。
- [ ] **Course Management UI**
    - [ ] **课程管理 UI**
    - [ ] Design `AddCourseActivity` or Dialog (inputs for name, room, time, etc.).
        - [ ] 设计 `AddCourseActivity` 或对话框（输入名称、教室、时间等）。
    - [ ] Implement "Save" logic to insert data into DB.
        - [ ] 实现“保存”逻辑以将数据插入数据库。
- [ ] **Course Display UI**
    - [ ] **课程显示 UI**
    - [ ] Implement Grid-based or List-based view for the weekly schedule.
        - [ ] 实现周课表的网格或列表视图。
    - [ ] Fetch and display courses from DB.
        - [ ] 从数据库获取并显示课程。
    - [ ] Add "Edit/Delete" functionality (long press or click detail).
        - [ ] 添加“编辑/删除”功能（长按或点击详情）。

## Phase 3: Schedule & Reminder Module (Week 3)
## 第三阶段：日程与提醒模块（第三周）
- [ ] **Schedule Database**
    - [ ] **日程数据库**
    - [ ] Define `ScheduleItem` entity (title, time, location, description).
        - [ ] 定义 `ScheduleItem` 实体（标题、时间、地点、描述）。
    - [ ] Update Database Helper to include Schedule table.
        - [ ] 更新数据库助手以包含日程表。
- [ ] **Schedule List UI**
    - [ ] **日程列表 UI**
    - [ ] Use `RecyclerView` to list daily/upcoming tasks.
        - [ ] 使用 `RecyclerView` 列出每日/即将到来的任务。
    - [ ] Implement `AddScheduleActivity`.
        - [ ] 实现 `AddScheduleActivity`。
- [ ] **Notification System**
    - [ ] **通知系统**
    - [ ] Implement `AlarmManager` logic for timed alerts.
        - [ ] 实现用于定时提醒的 `AlarmManager` 逻辑。
    - [ ] Create `BroadcastReceiver` to handle alarm triggers.
        - [ ] 创建 `BroadcastReceiver` 以处理闹钟触发。
    - [ ] Show System Notification (`NotificationCompat`) when alarm goes off.
        - [ ] 当闹钟响起时显示系统通知 (`NotificationCompat`)。

## Phase 4: Campus Map & Navigation (Week 4)
## 第四阶段：校园地图与导航（第四周）
- [ ] **Map SDK Integration**
    - [ ] **地图 SDK 集成**
    - [ ] Apply for Map API Key (Amap/Baidu/Tencent).
        - [ ] 申请地图 API 密钥（高德/百度/腾讯）。
    - [ ] Configure Manifest permissions (Location, Internet).
        - [ ] 配置 Manifest 权限（定位、网络）。
    - [ ] Embed `MapView` in `MapFragment`.
        - [ ] 在 `MapFragment` 中嵌入 `MapView`。
- [ ] **Location & Markers**
    - [ ] **定位与标记**
    - [ ] Implement "Locate Me" button (show current user location).
        - [ ] 实现“定位我”按钮（显示当前用户位置）。
    - [ ] Add Markers for key campus locations (Canteen, Library, etc.).
        - [ ] 为关键校园地点（食堂、图书馆等）添加标记。
    - [ ] Show `InfoWindow` (details) on marker click.
        - [ ] 点击标记时显示 `InfoWindow`（详情）。
- [ ] **Path Finding (Optional/Advanced)**
    - [ ] **路径规划（可选/高级）**
    - [ ] Implement simple path planning (draw line between user and selected destination).
        - [ ] 实现简单的路径规划（在用户和选定目的地之间画线）。

## Phase 5: Campus Market, Network Modules & User Center (Week 5)
## 第五阶段：校园集市、网络模块及用户中心（第五周）
- [x] **Campus Market Module (New)**
    - [x] **校园集市模块（新增）**
    - [x] **Database**: Create `MarketPost` and `MarketComment` entities.
        - [x] **数据库**：创建 `MarketPost` 和 `MarketComment` 实体。
    - [x] **Logic**: Support posting, commenting, and nested replies (replying to comments).
        - [x] **逻辑**：支持发帖、评论和嵌套回复（回复评论）。
    - [x] **UI**:
        - [x] Market Feed (List of posts).
            - [x] 集市信息流（帖子列表）。
        - [x] Post Detail Activity (Show post content + recursive comment list).
            - [x] 帖子详情 Activity（显示帖子内容 + 递归评论列表）。
        - [x] Create Post/Comment Interface.
            - [x] 发帖/评论界面。
- [ ] **AI Assistant (LLM Integration)**
    - [ ] **AI 助手（LLM 集成）**
    - [ ] Register for an LLM API (e.g., OpenAI/Aliyun).
        - [ ] 注册 LLM API（例如：OpenAI/阿里云）。
    - [ ] Create Chat UI (RecyclerView with multiple view types for User/AI).
        - [ ] 创建聊天 UI（使用具有多种视图类型的 RecyclerView 用于用户/AI）。
    - [ ] Implement network request (OkHttp/Retrofit) to send prompt and receive answer.
        - [ ] 实现网络请求（OkHttp/Retrofit）以发送提示并接收回答。
    - [ ] Handle async response and update UI.
        - [ ] 处理异步响应并更新 UI。
- [ ] **Campus News/Discovery**
    - [ ] **校园新闻/发现**
    - [ ] Create a simple JSON source (hosted on Gitee/GitHub or mock local JSON).
        - [ ] 创建一个简单的 JSON 源（托管在 Gitee/GitHub 上或模拟本地 JSON）。
    - [ ] Parse JSON and display news list in `DiscoveryFragment`.
        - [ ] 解析 JSON 并在 `DiscoveryFragment` 中显示新闻列表。
    - [ ] (Optional) Click to view details.
        - [ ] （可选）点击查看详情。
- [x] **Personal Center**
    - [x] **个人中心**
    - [x] Profile editing (Avatar, Nickname).
        - [x] 个人资料编辑（头像、昵称）。
    - [x] Implement logout functionality.
        - [x] 实现退出登录功能。
    - [ ] "About" page.
        - [ ] “关于”页面。

## Phase 6: Desktop Widget & Polish (Week 6)
## 第六阶段：桌面小组件与打磨（第六周）
- [ ] **App Widget**
    - [ ] **App 小组件**
    - [ ] Create Widget layout (list view for today's courses/tasks).
        - [ ] 创建 Widget 布局（今日课程/任务的列表视图）。
    - [ ] Implement `AppWidgetProvider`.
        - [ ] 实现 `AppWidgetProvider`。
    - [ ] Implement `RemoteViewsService` to load data from DB to Widget.
        - [ ] 实现 `RemoteViewsService` 以将数据从数据库加载到 Widget。
    - [ ] Add broadcast logic to update Widget when data changes in App.
        - [ ] 添加广播逻辑，以便在 App 中数据更改时更新 Widget。
- [ ] **System Testing & Optimization**
    - [ ] **系统测试与优化**
    - [ ] Verify all permissions handling.
        - [ ] 验证所有权限处理。
    - [ ] Test on different screen sizes (if possible).
        - [ ] 在不同屏幕尺寸上进行测试（如果可能）。
    - [ ] Code cleanup and commenting.
        - [ ] 代码清理和注释。
    - [ ] **Documentation**
    - [ ] **文档**
    - [x] Write Project Report / Thesis (as per `todo-ver0.md` structure).
        - [x] 撰写项目报告/论文（按照 `todo-ver0.md` 的结构）。    - [ ] Prepare presentation/demo.
        - [ ] 准备演示文稿/演示。

## Phase 7: Data Import & Export (Optional Extensions)
## 第七阶段：数据导入与导出（可选加分项）
- [ ] **JSON/CSV Export**
    - [ ] **JSON/CSV 导出**
    - [ ] Implement logic to convert Database tables (Courses/Schedules) to JSON or CSV format.
        - [ ] 实现将数据库表（课程/日程）转换为 JSON 或 CSV 格式的逻辑。
    - [ ] Use `FileProvider` or Scoped Storage to save files to the device's external storage.
        - [ ] 使用 `FileProvider` 或分区存储将文件保存到设备的外部存储。
- [ ] **Data Sharing**
    - [ ] **数据分享**
    - [ ] Implement `Intent.ACTION_SEND` to share the exported file via other apps (e.g., QQ, WeChat).
        - [ ] 实现 `Intent.ACTION_SEND`，通过其他应用（如 QQ、微信）分享导出的文件。
- [ ] **JSON Import**
    - [ ] **JSON 导入**
    - [ ] Implement a file picker to select a JSON file.
        - [ ] 实现文件选择器以选择 JSON 文件。
    - [ ] Parse the JSON file and bulk insert data into the Database.
        - [ ] 解析 JSON 文件并将数据批量插入数据库。

---
**Status Legend:**
**状态说明：**
- [ ] Not Started
    - [ ] 未开始
- [x] Completed
    - [x] 已完成
- [~] In Progress
    - [~] 进行中
