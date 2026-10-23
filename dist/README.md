# OpenMetroUI · 使用说明

版本 **0.10.10**。

一个融合 **Metro 动态磁贴** 与 **瑞士国际主义排版** 的 Android Compose UI 库。

> 直角、无阴影、单一强调色、4dp 网格。用瑞士的栅格与色彩纪律，约束 Metro 的色块与触感。

这个文件夹是一个**可直接使用的本地 Maven 仓库**：

```text
dist/
├── README.md          ← 你正在读的这份
└── maven/             ← 仓库本体，宿主直接指向这里
    └── io/github/jeta1me1plus/
        ├── omui-core/0.10.10/          AAR + sources + POM
        ├── omui-components/0.10.10/
        ├── omui-icons/0.10.10/
        └── omui-test/0.10.10/
```

不需要 Maven Central，也不需要 `mavenLocal`。整个 `dist` 目录可以拷到任何项目中。

---

## 一、30 秒接入

把 `dist` 拷进你的工程（例如放在项目根目录），然后在 `settings.gradle.kts` 里加一个仓库：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            // 指向 dist/maven，相对路径按你的实际位置调整
            url = uri(rootDir.resolve("dist/maven"))
        }
    }
}
```

`app/build.gradle.kts`：

```kotlin
dependencies {
    implementation("io.github.jeta1me1plus:omui-components:0.10.10")

    // 只做设计约束校验时用，必须放 testImplementation
    testImplementation("io.github.jeta1me1plus:omui-test:0.10.10")
}
```

`omui-components` 传递依赖 `omui-core` 与 `omui-icons`，一般只写这一行就够。

### 环境要求

| 项 | 要求 |
|---|---|
| minSdk | ≥ 24 |
| Compose | 1.11.4 或更高（库以 `api` 导出了带版本号的依赖，冲突时 Gradle 取高版本） |
| 依赖范围 | 只用 `androidx.compose.runtime / ui / foundation / animation` |
| 不依赖 | Material3、Material Icons、任何注入框架、任何注解处理器 |

**不要和 Material3 组件混着用**：两套语言的圆角、阴影、水波纹、色彩语义会互相打架，混用后视觉会明显割裂。

---

## 二、最小页面

```kotlin
setContent {
    OmTheme {
        OmScreen(scrollable = true) {
            Spacer(Modifier.height(32.dp))
            OmTopBar(title = "晨间简报", label = "TODAY / 今日")

            Spacer(Modifier.height(24.dp))
            OmButton("开始", onClick = { /* ... */ })
        }
    }
}
```

三条必须遵守的规则：

1. **`OmTheme` 必须在最外层**。所有 token 都从它的 CompositionLocal 读取，脱离主题使用组件会直接抛异常。
2. **页边距与滚动交给 `OmScreen`**，不要自己在外面再套 `padding`，否则会与栏网格错位。
3. **组件参数只传语义**（`variant`、`tone`、`size`），不要传 `Color` 或 `TextStyle` —— 需要定制就覆盖 `OmTheme`。

### 两个自动处理的事（v0.10.10 起）

| 行为 | 说明 |
|---|---|
| `OmTheme` 自动铺 `background` 底色 | 否则深色主题下会露出窗口的浅色底。局部嵌套主题时传 `paintBackground = false`（它用 `fillMaxSize`，在无界高度里会崩） |
| `OmScreen` 自动避开状态栏与导航栏 | 不用再写 `statusBarsPadding()`。如果骨架外层已经统一处理过 insets，传 `applyInsets = false`，否则会双倍间距 |

自定义骨架（不用 `OmScreen`）时，这两件事要自己负责：外层 `background(colors.background)` + `statusBarsPadding()` / `navigationBarsPadding()`。

---

## 三、主题

```kotlin
OmTheme(
    darkTheme = false,                        // 默认跟随系统；Paper 是默认主题
    accent = Color(0xFF1B4FD6),               // 全局唯一强调色
    tileMode = OmTileMode.Monochrome,         // 磁贴配色：单色相 / 八色组
    typography = OmTypography.System,         // 跟随系统字体；可换成 Inter
) { ... }
```

### 强调色

- 全局只能有一个。所有选中、焦点、进度、主要按钮都用它。
- `onAccent` 不需要你填：库会在黑白之间选对比度更高的那个。
- 传了过浅的颜色（比如浅黄）会被**自动拉深**到对最不利表面 3:1，保证焦点环可见。

### 磁贴颜色

磁贴不直接要颜色，而是声明**分量**：

```kotlin
OmTile("邮件", tone = OmTileTone.Accent)   // 强调块
OmTile("日历", tone = OmTileTone.Paper)    // 浅中性块
OmTile("音乐", tone = OmTileTone.Ink)      // 反白块，页面里的节奏锚点
```

切到 `OmTileMode.Spectrum` 时，磁贴会自动换成八色相（等明度、只变色相），调用点不用改。Spectrum 下 `Ink` 仍取反白块 —— 八色全浅会让整屏失去重量。

### 字体

库不打包任何字体文件。默认跟随系统（Roboto + 系统中文）。要拿最佳效果，宿主自己提供 Inter + 思源黑体：

```kotlin
OmTheme(typography = OmTypography.System.withFontFamily(yourFontFamily)) { ... }
```

---

## 四、组件一览

| 类别 | 组件 |
|---|---|
| 基础 | `OmText`、`OmSurface`、`OmDivider`、`OmIcon` |
| 布局 | `OmScreen`、`OmSection`、`OmGrid`（跨栏）、`Modifier.omGridSpan` |
| 导航 | `OmTopBar`、`OmCollapsingTopBar`、`OmPivot`、`OmBottomBar`、`OmRail`、`OmDrawer` |
| 内容 | `OmCard`、`OmList`、`OmListItem`、`OmTile`、`OmTileGrid`、`OmTag`、`OmAvatar`、`OmBadge`、`OmTooltip`、`OmEmptyState`、`OmAccordion`、`OmPager`、`OmPagerIndicator` |
| 输入 | `OmButton`、`OmIconButton`、`OmTextField`、`OmSearchField`、`OmCheckbox`、`OmRadio`、`OmSwitch`、`OmSlider` |
| 反馈 | `OmProgressBar`、`OmProgressRing`、`OmSkeleton`、`OmSnackbar` |
| 浮层 | `OmDialog`、`OmSheet` |

交互原语：`Modifier.omClickable`（明度下沉按压，替代水波纹）、`Modifier.omFocusRing`（2dp 焦点环）。

### 常用片段

**磁贴网格**（尺寸由跨栏决定，不由组件自己算）：

```kotlin
OmTileGrid(columns = 4, gutter = 8.dp) {
    OmTile("邮件", Modifier.omGridSpan(2).aspectRatio(1f), tone = OmTileTone.Accent)
    OmTile("音乐", Modifier.omGridSpan(4).aspectRatio(2f), tone = OmTileTone.Ink)
}
```

**表单**

```kotlin
OmTextField(value = email, onValueChange = { email = it }, label = "EMAIL / 邮箱")
OmSwitch(checked = notify, onCheckedChange = { notify = it })
OmCheckbox(checked = agree, onCheckedChange = { agree = it }, label = "同意条款")
```

**浮层**

```kotlin
if (showDialog) {
    OmDialog(
        visible = showDialog,
        onDismiss = { showDialog = false },
        title = "删除这条记录？",
        actions = {
            OmButton("取消", { showDialog = false }, variant = OmButtonVariant.Text)
            OmButton("删除", { showDialog = false }, variant = OmButtonVariant.Destructive)
        },
    ) {
        OmText("删除后无法恢复。")
    }
}
```

---

## 五、校验自己的配色

换掉强调色之后，用它确认配色还站得住：

```kotlin
@Test
fun `配色符合规范`() {
    OmCheck.requireContrast(OmColorScheme.paper())
    OmCheck.requireContrast(OmColorScheme.ink())
    OmCheck.requireTypography(OmTypography.System)
    OmCheck.requireSpacing(OmSpacing.Default)
}
```

也可以只取问题清单，自己做报告：

```kotlin
val problems = OmCheck.contrastViolations(OmColorScheme.paper())
```

覆盖范围：对比度（含全部磁贴色对）、4dp 网格、触摸目标下限、字阶与行高、间距档位。

---

## 六、这套库的视觉约束（会影响你的用法）

| 约束 | 具体表现 |
|---|---|
| 零圆角 | 所有矩形元素圆角为 0；只有头像、状态点、单选、进度环是圆的 |
| 零阴影 | 没有 elevation 参数。层级靠底色明度与字号，不靠投影 |
| 单一强调色 | 一个 accent 打天下；状态色只允许小面积使用 |
| 4dp 网格 | 一切尺寸是 4 的倍数，文字行高落在基线上 |
| 动效白名单 | 只有位移、淡入、色块揭示、磁贴翻转、颜色过渡；没有弹簧与回弹 |
| 不写死高度 | 承载文字的组件高度由内容决定，大字号下不会被裁 |

如果你需要圆角卡片、悬浮按钮、水波纹、渐变背景 —— 这套库不提供，也不会加。它是为「有明确立场的视觉系统」准备的，不是通用组件集。

---

## 七、已知限制

| 限制 | 说明 |
|---|---|
| `OmTag` 删除按钮 | 命中区 28dp，低于 48dp 常规下限（标签本身只有 28dp 高） |
| `OmSheet` | 拖拽只挂在顶部 48dp 手柄区，不能从内容区上滑展开 |
| `OmCard` 的 Ink 变体 | 只换底色，内部文字要自己用 `colors.onInverse` |
| RTL / TalkBack | 未做过系统性走查 |
| Maven Central | 尚未发布，目前只提供本地仓库 |

---

## 八、版本与产物校验

每个模块都带 `sha1 / md5 / sha256 / sha512` 校验文件，可用于分发校验。

```bash
# 校验某个产物
shasum -a 256 maven/io/github/jeta1me1plus/omui-core/0.10.10/omui-core-0.10.10.aar
```

产物包含：

| 文件 | 说明 |
|---|---|
| `*.aar` | 编译产物 |
| `*-sources.jar` | 源码，IDE 里可以直接跳转 |
| `*.pom` / `*.module` | 依赖元数据，Gradle 靠它解析传递依赖 |

重新生成这份 `dist`：在源码工程根目录执行 `./gradlew publishToDist`。
