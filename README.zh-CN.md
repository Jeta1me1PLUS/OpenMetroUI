# OpenMetroUI

![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)
![minSdk](https://img.shields.io/badge/minSdk-24-black.svg)
![Compose](https://img.shields.io/badge/Compose-1.11.4-4285F4.svg)

[English](README.md) · **简体中文**

融合 **Metro 动态磁贴** 与 **瑞士国际主义排版** 的 Jetpack Compose UI 库。直角、无阴影、单一强调色、4dp 网格。

<table>
  <tr>
    <td align="center"><img src="images/01-news-light.png" width="200" /><br/><sub>Paper · 资讯流</sub></td>
    <td align="center"><img src="images/02-news-dark.png" width="200" /><br/><sub>Ink · 深色</sub></td>
    <td align="center"><img src="images/03-lab.png" width="200" /><br/><sub>Lab · 实时调参</sub></td>
  </tr>
</table>

46 个组件，已在真机走查。要求 minSdk 24、Compose 1.11.4 以上。

---

## 1. 为什么做这个库

Metro 和瑞士国际主义有共同的底子：扁平、无衬线、网格、靠字号建立层级、拒绝装饰。差别在表达方式。Metro 用高饱和色块和动效说「这里能点」，瑞士用留白和极端的字号跨度说「这里重要」。

这个库让两者分工：**结构交给瑞士，色块和触感交给 Metro。**

| 约束 | 理由 |
|---|---|
| 圆角恒为 0 | 两派都是直角。圆角会削弱块面的几何稳定性 |
| 没有阴影 | 层级只由底色明度和字号建立 |
| 只有一个强调色 | 保留 Metro 的色块节奏，但色块要在瑞士的色彩纪律里活动 |
| 动效白名单 | 只允许位移、淡入、色块揭示、磁贴翻转、颜色过渡。没有弹簧和回弹 |

排版不做等比模数。UI 段按约 1.2 递进，展示段直接跳到 1.33 倍。层级靠跨度拉开，不靠均匀梯度。所有行高是 4sp 的整数倍，多列并排时基线才对得齐。

多色磁贴是 opt-in 的。八色在 OKLCH 里固定明度与彩度，只变色相，看起来有色彩但不花。默认仍是单色相。

---

## 2. 安装

### 2.1 Maven Central

```kotlin
implementation("io.github.jeta1me1plus:omui-components:0.11.0")
```

不需要额外仓库配置。已用只配 `mavenCentral()` 的消费者验证过，能正常解析。

### 2.2 GitHub Packages

```kotlin
// settings.gradle.kts
repositories {
    google()
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/Jeta1me1PLUS/OpenMetroUI")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}
```

包是 public，但 GitHub Packages 拉取仍需要 token（`read:packages` 权限）。

### 2.3 本地仓库

```bash
./gradlew publishToDist
```

```kotlin
repositories {
    maven { url = uri(rootDir.resolve("dist/maven")) }
}
```

### 2.4 环境要求

| 项 | 值 |
|---|---|
| minSdk | 24 |
| Compose | 1.11.4 以上 |
| 依赖范围 | 只用 `compose.runtime / ui / foundation / animation` |
| 不包含 | Material3、Material Icons、注入框架、注解处理器 |

**不要和 Material3 混用。** 圆角、阴影、按压反馈和色彩语义会互相打架。

---

## 3. 快速开始

```kotlin
setContent {
    OmTheme {
        OmScreen(scrollable = true) {
            Spacer(Modifier.height(32.dp))
            OmTopBar(
                title = "晨间简报",
                label = "TODAY",
                onBack = { },
                actions = { OmIconButton(OmIcons.Search, "搜索") { } },
            )
            Spacer(Modifier.height(24.dp))
            OmButton("开始", onClick = { })
        }
    }
}
```

三条规则：

1. `OmTheme` 放在最外层。所有 token 都从它的 CompositionLocal 读取。
2. 页边距、滚动、系统栏都交给 `OmScreen`，不要在外面再套 padding。
3. 参数传语义（`variant`、`tone`、`size`），传原始 `Color` 或 `TextStyle` 会绕过规范。

---

## 4. 组件

| 类别 | API |
|---|---|
| 基础 | `OmText` `OmSurface` `OmDivider` `OmIcon` |
| 按钮 | `OmButton` `OmIconButton` |
| 布局 | `OmScreen` `OmSection` `OmGrid` `Modifier.omGridSpan` |
| 导航 | `OmTopBar` `OmCollapsingTopBar` `OmPivot` `OmBottomBar` `OmRail` `OmDrawer` |
| 磁贴 | `OmTile` `OmTileGrid` |
| 内容 | `OmCard` `OmList` `OmListItem` `OmTag` `OmAvatar` `OmBadge` `OmTooltip` `OmEmptyState` `OmAccordion` `OmPager` `OmPagerIndicator` `OmStat` `OmSparkline` `OmTable` |
| 输入 | `OmTextField` `OmSearchField` `OmCheckbox` `OmRadio` `OmSwitch` `OmSlider` `OmSegmentedControl` `OmStepperInput` `OmDropdown` `OmDatePicker` |
| 反馈 | `OmProgressBar` `OmProgressRing` `OmSkeleton` `OmSnackbar` |
| 浮层 | `OmDialog` `OmSheet` |
| 原语 | `Modifier.omClickable` `Modifier.omFocusRing` |

### 4.1 按钮

五种变体、三种尺寸，视觉高度不同但触摸区统一 48dp。按压把底色压暗 8%，没有水波纹。

<img src="images/04-buttons.png" width="240" />

```kotlin
OmButton("保存", onClick = { })                                        // accent 填充
OmButton("取消", onClick = { }, variant = OmButtonVariant.Outlined)
OmButton("次要", onClick = { }, variant = OmButtonVariant.FilledInk)
OmButton("文字", onClick = { }, variant = OmButtonVariant.Text)
OmButton("删除", onClick = { }, variant = OmButtonVariant.Destructive)
OmButton("提交", onClick = { }, size = OmButtonSize.Large, fillWidth = true)
OmButton("继续", onClick = { }, leadingIcon = OmIcons.ArrowForward)

OmIconButton(OmIcons.Search, "搜索") { }
OmIconButton(OmIcons.Close, "删除", onClick = { }, tint = OmTheme.colors.danger)
```

### 4.2 输入

标签固定在输入区上方，不做浮动动画。底部线槽位恒为 2dp，聚焦时从 1dp 变 2dp，布局不动。

<img src="images/05-input.png" width="240" />

```kotlin
OmTextField(
    value = email,
    onValueChange = { email = it },
    label = "EMAIL",
    placeholder = "name@example.com",
    helperText = "密码重置链接会发送到这里",
    leadingIcon = OmIcons.Search,
)

OmTextField(
    value = note,
    onValueChange = { note = it },
    variant = OmTextFieldVariant.Filled,
    singleLine = false,
    minLines = 3,
)

OmTextField(value = value, onValueChange = {}, errorText = "格式不正确")
OmSearchField(value = query, onValueChange = { query = it })
```

### 4.3 列表与卡片

`OmList` 在项之间插入 hairline，最后一项不画。选中态是 `drawBehind` 画的 3dp 强调条，不占布局宽度，文字不会横向跳。

<img src="images/06-list.png" width="240" />
<img src="images/08-cards.png" width="240" />

```kotlin
OmList(items = rows, insetStart = 16.dp) { row ->
    OmListItem(
        title = row.title,
        subtitle = row.subtitle,
        trailing = { OmIcon(OmIcons.ChevronEnd, null) },
        onClick = { },
    )
}

OmListItem(title = "选中项", selected = true, onClick = { })

OmCard(variant = OmCardVariant.Outlined) {
    OmText("卡片标题", style = OmTheme.typography.title)
    OmText("正文内容")
}
```

### 4.4 磁贴

磁贴不直接接收颜色，只声明 `tone`。切到 Spectrum 时所有 tone 自动映射到八色，调用点不用改。尺寸由跨栏和宽高比决定，组件不自己算。

<img src="images/07-tiles.png" width="240" />

```kotlin
OmTileGrid(columns = 4, gutter = 8.dp) {
    OmTile(
        title = "收件箱",
        modifier = Modifier.omGridSpan(2).aspectRatio(1f),
        label = "3 条新消息",
        tone = OmTileTone.Accent,
    )
    OmTile(
        title = "设计周报",
        modifier = Modifier.omGridSpan(2).aspectRatio(1f),
        tone = OmTileTone.Paper,
        backTitle = "本周 8 篇，已读 3 篇",
    )
    OmTile(
        title = "阅读清单",
        modifier = Modifier.omGridSpan(4).aspectRatio(2f),
        tone = OmTileTone.Ink,
        onClick = { },
    )
}
```

`backTitle` 非空时点击绕 Y 轴翻转。背面必须承载信息，不能只是一个动画。

### 4.5 表单控件

<img src="images/09-forms.png" width="240" />

```kotlin
OmCheckbox(checked = agree, onCheckedChange = { agree = it }, label = "接收每周摘要")
OmRadio(selected = choice == 1, onClick = { choice = 1 }, label = "选项一")
OmSwitch(checked = notify, onCheckedChange = { notify = it })
OmSlider(value = level, onValueChange = { level = it })
```

<img src="images/13-controls.png" width="240" />
<img src="images/14-datepicker.png" width="240" />

```kotlin
OmSegmentedControl(
    options = listOf("日", "周", "月"),
    selectedIndex = segment,
    onSelect = { segment = it },
)

OmStepperInput(value = count, onValueChange = { count = it }, min = 1, max = 9)

OmDropdown(
    options = listOf("Paper", "Ink", "Spectrum"),
    selectedIndex = choice,
    onSelect = { choice = it },
)

OmDatePicker(selected = date, onSelect = { date = it })
```

### 4.6 反馈

<img src="images/10-status.png" width="240" />

```kotlin
OmProgressBar(progress = 0.6f)
OmProgressBar(progress = null)                        // 硬边扫描，没有渐隐拖尾
OmProgressRing(progress = 0.6f)
OmProgressRing(progress = null, size = 32.dp)

OmSkeleton(Modifier.fillMaxWidth().height(20.dp))     // 明度呼吸

OmTag("标签")
OmTag("已选", selected = true, onClick = { })
OmTag("可删除", onRemove = { })

OmBadge(text = "3")
OmAvatar("Ava", size = 40.dp)
OmTooltip("提示文案")

OmEmptyState(title = "还没有内容", description = "新建一条就可以开始。") {
    OmButton("新建", onClick = { })
}

OmAccordion(title = "折叠面板", subtitle = "副标题") {
    OmText("展开后的内容")
}
```

### 4.7 导航

<img src="images/11-rail.png" width="240" />

```kotlin
OmTopBar(title = "设置", large = false, onBack = { })
OmCollapsingTopBar(title = "资讯流", scrollState = scrollState)   // 滚动时 48sp 转 22sp

OmPivot(
    tabs = listOf("全部", "科技", "设计"),
    selectedIndex = tab,
    onSelect = { tab = it },
)

OmBottomBar(
    items = listOf(
        OmBottomBarItem("NEWS", OmIcons.Article),
        OmBottomBarItem("LAB", OmIcons.Beaker),
    ),
    selectedIndex = page,
    onSelect = { page = it },
)

OmRail(items = items, selectedIndex = page, onSelect = { page = it })   // >= 840dp
OmDrawer(visible = open, onDismiss = { open = false }) { /* 导航项 */ }
```

### 4.8 浮层

<img src="images/12-dialog.png" width="240" />

```kotlin
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

OmSheet(visible = showSheet, onDismiss = { showSheet = false }) { /* 内容 */ }

OmSnackbar(
    visible = showSnackbar,
    message = "已保存到本地",
    actionLabel = "撤销",
    onAction = { showSnackbar = false },
)
```

`OmSheet` 只从顶部 48dp 手柄拖动，内容区留给自己的滚动。


### 4.9 数据

<img src="images/15-data.png" width="240" />
<img src="images/16-menu.png" width="240" />

```kotlin
OmStat(value = "1,284", label = "MONTHLY ACTIVE", delta = "+12%")
OmStat(value = "98.2%", label = "UPTIME", delta = "-0.3%", deltaPositive = false)

OmSparkline(values = listOf(3f, 5f, 4f, 8f, 6f, 9f, 7f, 12f))

OmTable(
    columns = listOf(
        OmTableColumn("名称", weight = 2f),
        OmTableColumn("数量", weight = 1f, align = OmColumnAlign.End),
    ),
    rows = listOf(listOf("收件箱", "128"), listOf("草稿", "12")),
    onRowClick = { },
)

OmMenu(
    expanded = open,
    onDismissRequest = { open = false },
    offset = IntOffset(0, 48.dp.roundToPx()),
) {
    OmMenuItem("重命名", onClick = { open = false })
    OmMenuItem("删除", onClick = { open = false }, destructive = true)
}
```

---

## 5. 主题

```kotlin
OmTheme(
    darkTheme = false,
    accent = Color(0xFF1B4FD6),
    tileMode = OmTileMode.Monochrome,
    typography = OmTypography.System,
) { ... }
```

`onAccent` 由对比度算法自动选取。传入过浅的强调色会被逐步加深，直到对最不利的表面达到 3:1。

库不带字体文件。默认跟随系统字体，想要最佳效果需自己提供 Inter 与思源黑体：

```kotlin
OmTheme(typography = OmTypography.System.withFontFamily(myFontFamily)) { ... }
```

| Token | 取值 |
|---|---|
| 间距 | 4 / 8 / 12 / 16 / 24 / 32 / 48 / 64 / 96 dp |
| 字阶 | 11 / 12 / 14 / 16 / 18 / 22 / 28 / 36 / 48 / 64 sp |
| 字重 | 300 / 400 / 600 |
| 强调色 | 浅色 `#DA291C`，深色 `#FF5A4A` |
| 描边 | 1dp 结构线 / 2dp 聚焦与选中 / 3dp 页签指示条 |
| 动效 | 100 / 150 / 250 / 350 / 500 ms |

---

## 6. 会影响用法的约束

| 约束 | 表现 |
|---|---|
| 零圆角 | 只有头像、状态点、单选、进度环是圆的 |
| 零阴影 | 没有 elevation 参数，层级靠底色明度与字号 |
| 单一强调色 | 状态色只能小面积使用 |
| 4dp 网格 | 所有尺寸是 4 的倍数 |
| 不写死高度 | 带文字的组件高度由内容决定，大字号下不裁切 |

需要圆角卡片、悬浮按钮、水波纹、渐变背景的话，这个库不提供，也不会加。

---

## 7. 校验配色

换掉强调色之后跑一遍：

```kotlin
@Test
fun `配色符合规范`() {
    OmCheck.requireContrast(OmColorScheme.paper())
    OmCheck.requireContrast(OmColorScheme.ink())
    OmCheck.requireTypography(OmTypography.System)
    OmCheck.requireSpacing(OmSpacing.Default)
    OmCheck.requireOnGrid(listOf(16.dp, 24.dp))
}
```

覆盖对比度（含全部磁贴色对）、4dp 网格、触摸目标、字阶与行高、间距档位。必须用 `testImplementation` 引入。

---

## 8. Demo

`sample/` 是五页应用：资讯流、规范、组件画廊、Lab、设置。Lab 里可以实时改强调色、主题、字号缩放，并叠加 4dp 网格。

```bash
./build.sh              # 单测 + 编译 + 归档 APK
./build.sh --install    # 装到设备
```

每个 [Release](https://github.com/Jeta1me1PLUS/OpenMetroUI/releases) 都附带预编译的 APK。

---

## 9. 工程结构

```text
omui-core/         token、主题、交互原语、栅格布局
omui-components/   46 个组件
omui-icons/        13 个几何图标
omui-test/         OmCheck 设计约束校验
sample/            五页 demo
smoke/             只用 Maven 坐标消费发布产物，验证其完整可用
```

`dist/` 由 `publishToDist` 生成，不进版本库。

---

## 10. 已知限制

- `OmTag` 删除按钮命中区 28dp，低于 48dp 常规下限
- `OmSheet` 只能从手柄展开，不能从内容区上滑
- `OmCard` 的 Ink 变体只换底色，文字颜色要调用方自己处理
- `OmMenu` 不会在屏幕边缘自动翻转，靠近底部时需要传负的 offset
- RTL 与 TalkBack 未做系统性走查

---

## 11. 许可证

Apache License 2.0
