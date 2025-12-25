# Android 应用开发实验报告 - 校园助手 (Campus Assistant)

**项目名称**：校园助手 (Campus Assistant)  
**开发环境**：Android Studio, Java, Room Database  
**日期**：2025年12月25日

---

## 第一章 项目概述
本项目旨在开发一款服务于大学生的校园助手应用，集成了课程表管理、待办事项提醒、以及校园集市交流功能。通过该应用，学生可以方便地查看每日课表、管理个人任务，并在校园集市中发布动态或交易信息，实现校内信息的互通与共享。

## 第二章 系统需求分析
### 2.1 功能需求
1.  **用户模块**：
    *   支持用户注册与登录。
    *   游客无法进入主功能区，需强制登录。
    *   支持退出登录。
2.  **首页模块 (Home)**：
    *   自动显示当天的课程安排（根据当前星期几动态筛选）。
    *   展示未完成的待办事项（To-Do List）。
    *   提供校园集市的快速入口。
3.  **校园集市模块 (Market)**：
    *   **浏览帖子**：以列表形式展示所有用户发布的帖子，包含标题、内容摘要、作者及时间。
    *   **发布帖子**：支持用户发布新的集市动态。
    *   **帖子详情**：查看帖子完整内容及评论区。
    *   **评论与回复**：支持对帖子进行评论，并支持对评论进行回复（支持多级嵌套回复），界面上通过缩进展示层级关系。
4.  **个人中心模块**：
    *   展示当前登录用户信息。
    *   提供退出登录功能。

### 2.2 数据需求
*   **用户数据**：用户名、密码。
*   **课程数据**：课程名、教室、教师、周几、节次信息。
*   **待办数据**：内容、截止时间、完成状态。
*   **集市数据**：
    *   帖子：标题、内容、作者、发布时间、点赞数。
    *   评论：所属帖子ID、父评论ID（用于嵌套）、内容、作者、时间。

---

## 第三章 系统设计
### 3.1 数据库设计 (ER Design)
本项目使用 Android Jetpack Room 作为持久层框架。

1.  **User (users)**
    *   `id` (PK), `username`, `password`
2.  **Course (courses)**
    *   `id` (PK), `name`, `room`, `teacher`, `dayOfWeek`, `startPeriod`, `endPeriod`
3.  **Todo (todos)**
    *   `id` (PK), `content`, `deadlineTime`, `isDone`
4.  **MarketPost (market_posts)**
    *   `id` (PK), `title`, `content`, `username`, `postTime`, `likeCount`
5.  **MarketComment (market_comments)**
    *   `id` (PK), `postId` (FK -> MarketPost), `parentId` (FK -> MarketComment, nullable), `username`, `content`, `commentTime`
    *   *注：设置了级联删除 (CASCADE)，删除帖子会自动删除其下所有评论。*

### 3.2 界面交互设计
*   **MainActivity**：承载 BottomNavigationView，负责模块间的顶级导航。
*   **HomeFragment**：使用 `NestedScrollView` 嵌套两个 `RecyclerView`，分别展示课程和待办，头部显示欢迎语和集市入口。
*   **MarketActivity**：使用 `CoordinatorLayout` + `RecyclerView` 展示信息流，右下角悬浮 FAB 按钮用于发布。
*   **PostDetailActivity**：详情页包含帖子正文和评论列表，支持点击评论进行回复，底部固定输入框。

---

## 第四章 系统实现关键代码
### 4.1 首页数据聚合
在 `HomeFragment` 中，利用 `Executors` 在后台线程同时获取当日课程和未完成待办，通过 `runOnUiThread` 更新 UI。

```java
// 计算今天是周几 (转换 Calendar.SUNDAY 为 7)
Calendar calendar = Calendar.getInstance();
int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
int appDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;

// 查询数据库
List<Course> courses = db.courseDao().getCoursesByDay(appDay);
List<Todo> todos = db.todoDao().getUnfinishedTodos();
```

### 4.2 评论区的嵌套显示
为了在 `RecyclerView` 中展示树状结构的评论（评论回复评论），采用了**先处理后显示**的策略。

*   定义 `CommentNode` 包装类，包含 `MarketComment` 对象和 `depth`（深度）。
*   将扁平的数据库列表转换为树形结构，再深度优先遍历（DFS）展平为带缩进信息的列表。

---

## 第五章 系统重难点 (System Difficulties & Solutions)

### 5.1 难点一：无限层级嵌套评论的 UI 展示
**问题描述**：
校园集市的评论区需要支持回复功能。如果用户A评论了帖子，用户B回复了用户A，用户C又回复了用户B，这形成了一个树状结构。Android 的 `RecyclerView` 本质上是一个扁平的列表容器，不直接支持树形组件。如何在一个列表中直观地展示这种层级关系是一个难点。

**解决方案**：
1.  **数据结构设计**：在 `MarketComment` 实体中增加 `parentId` 字段。如果 `parentId` 为 0，表示是根评论；否则表示是子回复。
2.  **算法处理**：在 `PostDetailActivity` 中编写了 `processAndDisplayComments` 方法。
    *   **分组**：首先将所有评论按 `parentId` 分组，存入 `Map<Integer, List<MarketComment>> childrenMap`。
    *   **递归展平**：找到所有根评论，递归查找其子评论，将树形结构“拍扁”成线性列表。
    *   **深度标记**：在递归过程中，维护 `depth` 变量，封装到 `CommentNode` 中。
3.  **适配器实现**：创建 `CommentNode` 类封装评论数据和深度。在 `CommentAdapter` 的 `onBindViewHolder` 中，根据 `depth` 动态设置 `paddingStart` (例如 `depth * 48dp`)，从而在视觉上实现缩进效果。

```java
// 核心递归逻辑伪代码
void addChildrenRecursive(parentId, childrenMap, resultList, depth) {
    if (childrenMap.containsKey(parentId)) {
        for (child : childrenMap.get(parentId)) {
            resultList.add(new CommentNode(child, depth)); // 加入列表并记录深度
            addChildrenRecursive(child.getId(), childrenMap, resultList, depth + 1); // 递归查找下一级
        }
    }
}
```

### 5.2 难点二：异步数据库操作与 UI 更新
**问题描述**：
Android 禁止在主线程（UI线程）进行耗时的数据库 IO 操作，否则会抛出异常或导致应用无响应（ANR）。然而，Room 查询结果需要显示在 UI 上，这就涉及到多线程并发与线程通信的问题。

**解决方案**：
*   **线程池**：使用 `Executors.newSingleThreadExecutor()` 创建后台线程池来执行 `insert`, `query`, `update` 等操作，避免阻塞主线程。
*   **UI 切换**：在获取到数据后，通过 `Activity.runOnUiThread(() -> { ... })` 切换回主线程更新 `RecyclerView` 的适配器数据。
*   **上下文处理**：在 Fragment 中操作时，注意生命周期，使用 `requireContext()` 或 `getActivity()` 并做好判空处理，防止 Fragment 销毁后回调导致的崩溃。

### 5.3 难点三：复杂布局的滑动冲突
**问题描述**：
在 `HomeFragment` 和 `PostDetailActivity` 中，页面主体内容可能超过一屏需要滚动，但内部又包含了 `RecyclerView`（本身也具有滚动能力）。如果直接嵌套，会导致 `RecyclerView` 显示不全（只显示一行）或滑动卡顿（外层和内层抢夺滑动事件）。

**解决方案**：
*   **NestedScrollView**：使用 `NestedScrollView` 作为最外层容器，它支持嵌套滚动机制。
*   **禁用内部滚动**：对内部的 `RecyclerView` 设置 `android:nestedScrollingEnabled="false"`。这样，`RecyclerView` 会一次性测量出完整高度并展示所有 Item，不再处理滚动事件，所有滚动力度完全由外层的 `NestedScrollView` 接管。这样既保证了内容完整显示，又拥有流畅的整体滑动体验。

### 5.4 难点四：数据一致性与级联删除
**问题描述**：
当一个帖子被删除时，如果其关联的评论没有被删除，就会产生“脏数据”，不仅占用空间，还可能导致查询逻辑出错。

**解决方案**：
在 Room 的 `@Entity` 注解中使用 `foreignKeys` 属性配置外键约束，并设置 `onDelete = ForeignKey.CASCADE`。
```java
@Entity(foreignKeys = @ForeignKey(
    entity = MarketPost.class,
    parentColumns = "id",
    childColumns = "postId",
    onDelete = CASCADE))
```
这样，当底层数据库检测到 Post 被删除时，SQLite 引擎会自动级联删除所有 `postId` 匹配的 Comment，保证了数据的完整性和一致性。

---

## 第六章 实验总结
通过本次实验，我们成功构建了一个功能相对完善的 Android 校园助手应用。
1.  **架构层面**：掌握了 Android 四大组件的协同工作，以及 Fragment 的生命周期管理。
2.  **数据层面**：深入理解了 Jetpack Room 数据库的使用，包括实体定义、DAO 编写、版本迁移以及复杂的一对多（Post-Comment）关系处理。
3.  **UI 层面**：熟练运用 `RecyclerView` 实现多种列表布局，特别是通过自定义适配器逻辑实现了复杂的嵌套评论展示；解决了 `NestedScrollView` 与列表控件的滑动冲突问题。
4.  **交互层面**：实现了完整的用户登录/退出流程和基于 Intent 的 Activity 跳转与参数传递。

未来可以进一步引入网络请求框架（Retrofit）对接真实服务器，并集成 AI 模型 API 完善“发现”模块。
