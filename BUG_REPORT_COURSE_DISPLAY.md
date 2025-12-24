# 校园助手项目：课程显示异常 Bug 排查与修复报告

## 1. 问题背景 (Problem Description)
在进行课程管理功能测试时，发现用户在“添加课程”页面输入信息并点击保存后，虽然系统提示“保存成功”，但在“课程表”主界面网格中无法看到新增的课程块。

## 2. 故障现象 (Phenomenon)
- 数据库层：通过调试发现数据已成功插入 `courses` 表。
- UI 层：课程表网格保持为空白，没有任何渲染出的 `CardView`。
- 日志层：没有任何崩溃（Crash）记录，属于逻辑错误（Logic Bug）。

## 3. 技术根源剖析 (Root Cause Analysis)

### 3.1 UI 层级：控件数据源缺失
- **文件位置**：`app/src/main/res/layout/activity_add_course.xml`
- **错误细节**：`Spinner`（下拉列表）控件未定义 `android:entries` 属性。
- **后果**：由于没有绑定星期数组（周一至周日），该下拉框在运行时没有任何可选项，用户无法进行有效的日期选择。

### 3.2 代码层级：无效索引偏移
- **文件位置**：`app/src/main/java/com/example/final_pj/AddCourseActivity.java`
- **逻辑缺陷**：
  ```java
  int dayOfWeek = spinnerDay.getSelectedItemPosition() + 1;
  ```
- **后果**：由于 `Spinner` 为空，`getSelectedItemPosition()` 固定返回 `-1`。导致最终保存到数据库的 `dayOfWeek` 字段值始终为 `0`（即 -1 + 1）。

### 3.3 渲染层级：非法坐标拦截
- **文件位置**：`app/src/main/java/com/example/final_pj/ui/CourseTableFragment.java`
- **过滤逻辑**：
  ```java
  int day = course.getDayOfWeek() - 1; // 获取数据库中的 0
  if (day < 0 || day > 6) return; // 逻辑判断：0-1 = -1，触发 return
  ```
- **后果**：渲染引擎在绘制时，判定该课程的横坐标（星期）不在合法范围（0-6）内，为了防止 UI 错位而执行了安全拦截。最终导致课程数据在前端“隐身”。

## 4. 修复方案 (Resolution)

### 4.1 修复 XML 数据绑定
在 `activity_add_course.xml` 中，为 `Spinner` 显式绑定星期数据源：
```xml
<Spinner
    android:id="@+id/spinner_day"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:entries="@array/days_of_week" /> <!-- 修复点：绑定数据源 -->
```

### 4.2 验证逻辑闭环
- 绑定后，用户可选择“周一”至“周日”，对应索引 `0` 至 `6`。
- `dayOfWeek` 保存值变为 `1` 至 `7`。
- `CourseTableFragment` 接收到值后，计算出 `day` 下标为 `0` 至 `6`，通过安全检查，课程块得以在正确的位置绘制。

## 5. 经验总结 (Lessons Learned)
1. **数据源完整性**：在 Android 开发中，所有具备选择性质的控件（Spinner, ListView等）必须确保其 Adapter 或 Entries 属性在初始化时是完整的。
2. **防御性编程**：在 Activity 保存数据前，应增加逻辑判断（如 `if (dayOfWeek <= 0) { ... }`），并在 UI 上给出相应提示，而非任由非法数据流入数据库。
3. **闭环思维**：解决显示问题不应只看渲染代码，必须追踪数据的“全生命周期”，从 UI 输入、数据存储到前端解析逐一排查。


1. 故障现象（Phenomenon）
   用户在“添加课程”页面输入了完整的课程名称、教室和节次信息，并点击“保存”提示成
   功。但在跳转回“课程表”主界面时，该课程并未出现在网格中，界面依然显示为空白（
   或仅显示旧数据）。

2. 技术根源剖析（Root Cause Analysis）
   该 Bug 是由 UI 配置缺失 与 逻辑计算偏移 共同导致的连锁反应：

* UI 层的属性缺失（XML）：
  在 activity_add_course.xml 中，定义 Spinner（下拉列表）控件时漏掉了
  android:entries 属性。在 Android 中，Spinner
  需要一个数据适配器（Adapter）来填充内容。由于没有绑定数据源（星期数组），该
  控件在运行时没有任何选项。
* 代码层的逻辑断裂（Java）：
  在 AddCourseActivity.java 的 saveCourse 方法中，获取星期数据的逻辑如下：
  1     int dayOfWeek = spinnerDay.getSelectedItemPosition() + 1;
  由于 Spinner
  内部没有任何数据，用户无法进行选择，getSelectedItemPosition() 会固定返回
  -1。此时，计算出的 dayOfWeek 始终等于 0。
* 渲染层的范围过滤（Logic Filter）：
  在主界面的绘制逻辑 CourseTableFragment.java
  中，渲染循环是基于标准的“周一至周日”（即 1 到 7）进行的。
  1     int day = course.getDayOfWeek() - 1; // 期望值 1-7，转换为下标 0-6
  2     if (day < 0 || day > 6) return; // 异常值直接拦截
  由于数据库中存储的 dayOfWeek 是 0，计算得出的 day 为
  -1，触发了安全拦截机制。这导致该课程数据虽然存在于数据库中，但在渲染时被视为
  “非法坐标”而无法绘制，从而在课表上彻底“隐身”。
3. 修复方案（Resolution）
   修复方案分为两个维度，确保系统的健壮性：

* 前端修复（直击要害）：
  在 activity_add_course.xml 的 Spinner 标签中显式绑定资源文件 strings.xml
  中预定义的星期字符串数组：
  1     android:entries="@array/days_of_week"
  通过此操作，Spinner 会加载“周一”到“周日”共 7 个选项，用户点击后
  getSelectedItemPosition() 会返回正确的 0-6 索引。
* 逻辑闭环：
  保存后的数据将落在 1-7 的合法区间内，完美契合 CourseTableFragment
  的渲染逻辑，课程块得以在正确的列坐标处显示。

4. 总结与反思（Lesson Learned）
* UI与逻辑的耦合性：在 Android
  开发中，控件的状态直接影响后端的数据质量。对于 Spinner、RadioGroup
  等选择类控件，必须确保初始数据源的完整性。
* 边界值检查的重要性：在保存数据前，应增加对 dayOfWeek
  的校验（例如判断是否大于
  0），这种“防御性编程”意识能有效避免下游模块出现难以排查的显示异常。

