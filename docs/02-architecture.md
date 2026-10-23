# 02 · 工程架构

本文说明工程如何组织、Token 如何落到代码、API 遵循什么原则。视觉数值不在此重复，见 [00-design-language.md](00-design-language.md)。

---

## 一、总体形态

- **多模块 Gradle 工程**，3 个可发布 library + 1 个 demo app。
- **纯 Jetpack Compose**，仅依赖 `androidx.compose.*`（runtime / ui / foundation / animation），**不依赖 Material3**。
- 单 Activity 架构，demo app 不引入 Navigation Compose（页面用标签枚举 + 返回栈管理）。
- 不使用注解处理器（无 KSP / KAPT / Hilt），依赖手工注入，保持构建快、版本配对简单。

```text
OpenMetroUI/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties              # 集中版本号 OM_VERSION
├── gradle/libs.versions.toml      # 版本目录（含依赖天花板注释）
├── omui-core/                     # Token / 主题 / 交互原语 / 栅格布局（无组件）
├── omui-components/               # 组件（依赖 core）
├── omui-icons/                    # 图标集（独立，宿主可选）
├── sample/                        # demo app（不发布）
└── docs/
```

### （一）模块职责

| 模块 | 职责 | 依赖 | 发布坐标 |
|---|---|---|---|
| `omui-core` | 色彩/排版/间距/栅格/形状/动效 token、`OmTheme`、自定义 `Indication`、焦点环、栅格容器、字号适配工具 | compose runtime / ui / foundation / animation | `io.openmetro:omui-core` |
| `omui-components` | 全部组件 | `omui-core` + `omui-icons`（页头默认返回箭头） | `io.openmetro:omui-components` |
| `omui-icons` | 图标集（`ImageVector` 构建） | `omui-core`（仅取描边宽度） | `io.openmetro:omui-icons` |
| `omui-test` | 设计约束的自动校验（对比度 / 4dp 网格 / 触摸目标 / 排版 / 间距） | `omui-core` + JUnit | `io.openmetro:omui-test` |
| `sample` | 活规范 demo | 以上全部 | 不发布 |

**为什么把交互原语放在 core 而不是 components**：按压反馈、焦点环、reveal 是「视觉语言的一部分」而非某个组件的实现细节；宿主自建组件时也需要它们，且它们要被单独测试。

**为什么图标独立**：图标集增长快、体积占比大，独立后宿主可按需依赖，也避免主包频繁因新增图标而升版本。

---

## 二、命名约定

| 对象 | 约定 | 示例 |
|---|---|---|
| 组件 | `Om` 前缀 + PascalCase | `OmButton`、`OmTile` |
| Token 数据类 | `Om` + 域 | `OmColorScheme`、`OmTypography` |
| CompositionLocal | `LocalOm` + 域 | `LocalOmColors` |
| Modifier 扩展 | `Modifier.om…` | `Modifier.omGridSpan(2)` |
| 枚举 / 变体 | 语义命名，不用外观命名 | `OmButtonVariant.Filled` 而非 `Primary` |
| 资源前缀 | `om_` | `om_ic_arrow_start` |

`Om` 前缀刻意保留，使得与 Material 组件混用时不会歧义（`Button` vs `OmButton`）。

---

## 三、Token 层设计

### （一）职责边界

Token 层只做三件事：**定义数值、按主题组合、通过 CompositionLocal 下发**。它不含任何布局与绘制逻辑。

### （二）六个域

| 域 | 数据类 | 内容 |
|---|---|---|
| 颜色 | `OmColorScheme` | 语义别名（`textPrimary`、`accent`、`line`…），不暴露 `neutral.xxx` |
| 排版 | `OmTypography` | 10 个字阶的 `TextStyle`，含行高与字距 |
| 间距 | `OmSpacing` | `space1…space24` 的 `Dp` 值 |
| 栅格 | `OmGrid` | 栏数、页边距、栏间距、内容最大宽度、当前宽度类别 |
| 形状 | `OmShapes` | `none` / `full`、三种描边宽度 |
| 动效 | `OmMotion` | 5 个时长（`Duration`）、3 条缓动（`Easing`） |

### （三）下发方式

- 每个域一个 `CompositionLocal`，默认值指向 Paper 主题；**不提供无主题的隐式可用值**——脱离 `OmTheme` 使用组件即为 API 误用。
- `OmTheme` 同时提供聚合访问入口（`OmTheme.colors` 等），避免调用点满屏 CompositionLocal。
- 细粒度 CompositionLocal 的意义：只改颜色时不会让排版读取者重组。

### （四）栅格随窗口变化

`OmGrid` 由主题在组合期根据当前窗口宽度计算并下发（Compact / Medium / Expanded / Large），组件读取 `LocalOmGrid` 即可，**不需要自己写断点判断**。

### （五）主题参数

`OmTheme` 的入参（设计意图，非最终签名）：

| 参数 | 默认 | 说明 |
|---|---|---|
| `colorScheme` | Paper | 可直接传完整配色表 |
| `accent` | `#DA291C` | 覆盖强调色，自动推导 `onAccent` 并校验对比度 |
| `darkTheme` | 跟随系统 | 切换 Paper / Ink |
| `typography` | 系统字体 | 可传 Inter 版或完全自定义 |
| `tileMode` | Monochrome | `Spectrum` 为 opt-in |
| `content` | — | 主题作用域内容 |

不允许通过主题参数修改圆角、阴影、间距——这些是硬约束，不开放。

---

## 四、API 设计原则

1. **无状态优先**：所有组件默认受控（`value` + `onValueChange`），内部只保留纯交互反馈状态（按压、焦点、悬停）。唯一例外是需要测量与动画的内部状态。
2. **Slot 优先**：用 `content: @Composable () -> Unit` 而非穷举参数。`OmCard` 不提供 `title/subtitle/icon` 三件套，而提供 `content` 槽位。
3. **`modifier` 永远是第一个可选参数**，且必须向外传递到最后一次布局。
4. **不写死高度**：任何承载文字的组件，高度由内容决定；最小高度用 `heightIn(min = …)`。
5. **只暴露语义，不暴露原始 token**：组件参数用 `OmButtonVariant`，不接受 `Color`；确需定制时走 `OmTheme` 覆盖。
6. **状态语义内建**：`Role`、`contentDescription`、`stateDescription`、`selected` 由组件自己注入，不要求宿主补。
7. **不提供全局可变单例**：无 `object` 级的可变主题状态，一切走 CompositionLocal。
8. **不隐藏副作用**：需要 `LaunchedEffect` 或测量回调的行为在文档字符串中写明。

---

## 五、技术选型与依赖

### （一）工具链（具体版本在实现时锁定并写入 `libs.versions.toml`）

| 项 | 选择 | 说明 |
|---|---|---|
| 语言 | Kotlin 2.x（K2） | Compose 编译器插件与 Kotlin 同版本 |
| 构建 | AGP 8.x | 与 JDK 17 配套 |
| JDK | 17 | Gradle 与 AGP 的要求 |
| compileSdk / targetSdk | 36 | 实现时以本机 SDK 为准 |
| minSdk | 24 | 已拍板（2026-10-21） |
| Compose | BOM 最新稳定版 | 只用 runtime / ui / foundation / animation |

### （二）依赖清单

允许：

```text
androidx.compose.runtime
androidx.compose.ui
androidx.compose.ui:ui-text
androidx.compose.foundation
androidx.compose.animation
```

**明确禁止**：

```text
androidx.compose.material3      ← 立项前提
androidx.compose.material
androidx.compose.material-icons-*
androidx.navigation:navigation-compose
任何依赖注入框架
```

### （三）版本天花板

Compose 与 androidx 的版本受 compileSdk 约束。新增依赖前先读 AAR 内 `META-INF/com/android/build/gradle/aar-metadata.properties` 的 `minCompileSdk`，把安全上限与理由写进 `libs.versions.toml` 注释，**不靠试错**。

### （四）测试

- 单元测试只测纯逻辑：色对对比度校验、栅格列宽计算、字号降级规则。
- 不引入 Robolectric；Compose UI 测试仅在需要时按组件补。
- 对比度校验是**构建门槛**：Spectrum 全部色对与主题色对必须通过，否则构建失败。

---

## 六、构建与发布

### （一）版本号集中管理

- 版本号写在 `gradle.properties` 的 `OM_VERSION`，由根构建脚本注入三个模块，避免多模块版本漂移。
- demo app 的 `versionName` 引用同一变量，并在注释里写明「这一版为什么是这个号」。

### （二）发布

- `com.android.library` + `maven-publish`，`groupId = io.openmetro`。
- 四个模块各发布 `release` 变体 + sources jar，坐标 `io.openmetro:omui-core / omui-components / omui-icons / omui-test`。
- 版本与 groupId 从 `gradle.properties` 的 `OM_VERSION` / `OM_GROUP` 读取，不写死。
- **已用 `publishToMavenLocal` 验证**四个模块的 AAR、sources jar、POM 都能生成并落盘。
- 尚未配置：签名（`signing` 块）、Maven Central 仓库地址与凭证。这两项需要账号，等具备条件再补。
- 依赖版本用 `api` / `implementation` 严格区分：`omui-core` 对 compose 用 `api`（组件签名会暴露 `Dp`/`Color`/`TextStyle`），`omui-components` 对 core 与 icons 用 `api`。

### （三）本地消费

```text
include(":omui-core", ":omui-components", ":omui-icons", ":sample")
```

宿主项目在未发布期间用 `includeBuild` 或本地 `mavenLocal` 接入。

---

## 七、demo app：活规范

demo 不是「组件列表页」，而是**可以在运行时验证规范的工具**。四个区域：

| 区域 | 内容 | 存在理由 |
|---|---|---|
| **Spec** | 色板（含对比度数值）、字阶表（真实字重与行高）、间距标尺、动效曲线播放器、描边与圆形示例 | 把 00 的表格变成可看的实物，任何数值改动立刻可见 |
| **Gallery** | 每个组件一行，带参数开关（尺寸、变体、禁用、RTL、大字号） | 组件的全部状态必须能在同一屏对比 |
| **Showcase** | 4 个真实页面：新闻流、音乐、邮件、设置 | 只靠组件画廊无法判断这套语言能否构成完整界面 |
| **Lab** | 运行时调 accent 色相、主题（Paper/Ink）、Spectrum 开关、字号缩放模拟、网格叠加开关 | 验证「单色相 + 网格」在不同取值下是否仍成立 |

**网格叠加（Grid Overlay）**：可随时叠加 4dp 基线与栏网格，用 1dp 半透明线绘制在最上层。这既是开发工具，也是这套设计语言的自证——任何歪掉 1dp 的元素都会立刻暴露。

demo app 的导航用标签枚举 + 返回栈，不引 Navigation Compose。

---

## 八、代码地图（规划）

```text
omui-core/src/main/kotlin/io/openmetro/ui/core/
├── token/
│   ├── OmColorScheme.kt        # 语义色 + Paper/Ink 两套 + accent 推导
│   ├── OmTypography.kt         # 10 个字阶 + 系统/Inter 两套预设
│   ├── OmSpacing.kt
│   ├── OmGrid.kt               # 宽度类别与栏参数
│   ├── OmShapes.kt
│   └── OmMotion.kt
├── theme/
│   ├── OmTheme.kt              # 入口 + CompositionLocal 定义
│   └── Contrast.kt             # 对比度计算与校验（纯函数，可单测）
├── interaction/
│   ├── OmIndication.kt         # 明度下沉按压反馈
│   ├── OmFocusRing.kt
│   └── OmReveal.kt             # 色块揭示
├── layout/
│   ├── OmGridContainer.kt      # 栏网格容器
│   ├── OmScreen.kt             # 页边距 + 内容最大宽度
│   └── FontScale.kt            # 三条不变量的工具函数
└── modifier/
    ├── OmGridSpan.kt
    └── OmBaseline.kt

omui-components/src/main/kotlin/io/openmetro/ui/component/
├── button/  tile/  navigation/  input/  content/  feedback/  overlay/
```

---

## 九、版本策略

- 库版本 `v0.MINOR.PATCH`，写在 `OM_VERSION`。
- 新需求（提一个算一个）→ MINOR；缺陷与小调整按**条数** → PATCH；文档与构建脚本 → 不动版本号。
- 首个代码版本为 `v0.1.0`（Token 层 + P0 组件 + demo 骨架）。**当前文档阶段不占版本位、不打标签。**
- 一版一提交一标签，tag 名与 `OM_VERSION` 一致。

---

## 十、待定的技术问题

| 编号 | 问题 | 备选 | 影响 |
|---|---|---|---|
| T1 | 输入框的文本选择与 IME | **已定（v0.3.0）**：`BasicTextField` + `decorationBox`，光标用 accent，选择手柄与选区走平台默认 | 自绘手柄的收益不足以抵消维护成本；平台默认行为在无障碍与 IME 上更稳 |
| T2 | 磁贴翻转的实现 | **已定（v0.4.0）**：`graphicsLayer.rotationY` + 双面内容，背面再转 180° 消除镜像，`cameraDistance = 12×density` 抑制透视 | 真 3D 翻转是磁贴的性格；交叉淡入省事但不像「翻」 |
| T3 | 图标实现方式 | `ImageVector` 构建器 / Canvas 直接绘制 Path | 前者可复用、可 tint；后者包体更小 |
| T4 | 对话框与 Sheet | **已定（v0.6.0）**：平台 `Dialog` + `decorFitsSystemWindows = false` + 自绘 scrim | 返回键、焦点陷阱、无障碍交给系统；自绘这两样很容易漏且难测 |
| T5 | 栅格容器 | 自研 `Layout` / 基于 `BoxWithConstraints` 计算 | 自研可做跨栏定位，后者简单但不支持 span |
| T6 | 是否提供 `omui-test` 模块 | **已定（v0.7.0）**：提供 `OmCheck`，覆盖对比度 / 网格 / 触摸目标 / 排版 / 间距 | 宿主换掉 accent 之后能直接跑同一份清单，不必自己重写判据 |

T1、T2 在实现 P0 组件前必须先定，其余可在实现中定。
