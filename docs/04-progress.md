# 04 · 进度与版本

本文记录当前状态、版本历史、构建验证方式、未验证项与待办。**写当前状态之前先取证**（`git log --oneline`、`git tag`、`wc -l`），不凭记忆。

---

## 一、当前状态

| 项 | 值 |
|---|---|
| 版本 | `v0.10.10`（**P0 + P1 + P2 全部完成**，真机验证通过，坐标已迁到 GitHub 命名空间） |
| 分支 | `main` |
| 代码规模 | 主源码 5817 行（59 个 Kotlin 文件）+ 测试 180 行 |
| 模块 | `omui-core` / `omui-components` / `omui-icons` / `omui-test` / `sample` / `smoke` |
| 构建 | `./build.sh --lint` 成功，零错误零告警 |
| 测试 | 21 个用例全过（core 8 + test 13） |
| lint | 五个模块均 0 issue |

### （一）已交付

- Gradle 四模块骨架（AGP 9.1.0 / Gradle 9.3.1 / Kotlin 2.2.20 / compileSdk 36 / minSdk 24）。
- Token 层：`OmColorScheme`、`OmTypography`、`OmSpacing`、`OmGrid`、`OmShapes`、`OmMotion`、`OmTheme`（7 个细粒度 CompositionLocal）。
- 对比度体系：`OmContrast`（纯 Kotlin，可 JVM 单测）+ 构建期色对校验（Paper / Ink / Spectrum / 自定义 accent 共 8 个用例）。
- 交互原语：`Modifier.omClickable`（明度下沉按压）、`Modifier.omFocusRing`（2dp accent 焦点环）。
- L1 原语：`OmText`、`OmSurface`、`OmDivider`、`OmIcon`。
- L2 结构：`OmScreen`（页边距 + 最大行宽）、`OmSection`（固定区块节奏）、`OmGrid`（跨栏布局 + `Modifier.omGridSpan`）。
- 组件：`OmButton`（5 变体 × 3 尺寸）、`OmTextField`（下划线/填充、固定上标签、聚焦与错误态、多行）、`OmTopBar`（大标题/紧凑）、`OmList`/`OmListItem`（选中竖条、自动分隔）、`OmTile`/`OmTileGrid`（Y 轴翻转、标题字号随宽度自适应）。
- 导航：`OmPivot`（3dp 文字宽度指示条）、`OmBottomBar`（3–5 项、顶部 accent 条）。
- P1 表单与展示：`OmCard`（3 变体）、`OmCheckbox`、`OmRadio`、`OmSwitch`、`OmSlider`、`OmProgressBar`（含硬边扫描不确定态）、`OmTag`、`OmAvatar`。
- P1 浮层：`OmDialog`（平台 Dialog + 自绘 scrim + 淡入上移）、`OmSheet`（两档高度 + 拖拽跟手）。
- 图标集：12 个几何图标（`OmIcons`），方头端点、直角拐角。
- P2 第一批：`OmSnackbar`、`OmBadge`、`OmSkeleton`（明度脉冲）、`OmEmptyState`（网格线取景框）、`OmSearchField`、`OmTooltip`。
- P2 第二批：`OmAccordion`（高度展开 + 指示器旋转）、`OmRail`（侧边导航，start 侧 3dp 竖条）、`OmDrawer`（从 start 滑出，RTL 镜像）。
- P2 收尾：`OmProgressRing`（确定/不确定态）、`OmPager` + `OmPagerIndicator`（直角线段）、`OmCollapsingTopBar`（滚动收缩，阈值用 `derivedStateOf`）。
- 校验工具 `omui-test`：`OmCheck` 校验对比度、4dp 网格、触摸目标、字阶与行高、间距；自带 13 个单元测试。
- 交付目录 `dist/`：本地 Maven 仓库（AAR + sources + POM + 校验和）+ 面向使用者的独立说明 `dist/README.md`，`./gradlew publishToDist` 生成。
- 接入验证模块 `smoke`：只用 Maven 坐标依赖本库，`./gradlew :smoke:assembleDebug` 能编译即说明 dist 完整可用（依赖树确实从 `dist/maven` 解析，Compose 版本为 1.11.4）。
- demo：五页骨架（NEWS / SPEC / UI / LAB / SET）+ 两个 Showcase 页面 + 画廊九区 + Lab 实时调参与网格叠加。NEWS 页改为固定页头 + 滚动收缩。

**37 个组件全部实现（P0 13 + P1 12 + P2 12）。**

### （二）未交付

- Maven 发布配置（模块结构已就绪，签名与凭证未配）。
- 3 个待定技术问题（T3 图标实现、T5 栅格容器）—— 当前实现已够用，暂不动。
- P1 / P2 全部组件。
- Maven 发布配置（本阶段只保证模块结构可发布）。
- demo 的 Showcase（真实页面）与 Lab（运行时调 token、叠加网格）。

---

## 二、版本历史

### v0.10.10（2026-10-23）

用模拟器截图准备 README 素材时发现的缺陷。

- 修复：`OmTile` 的 Paper 色调缺少 1dp 描边。白磁贴在米白底上、深灰磁贴在近黑底上边界完全不可见，磁贴糊成一片。规范里本来就要求这条，是实现漏做。
- 变更：demo 的收件箱磁贴改为 `title = "收件箱"` + `label = "3 条新消息"`，避免 28sp 标题在 156dp 磁贴里断成「3 条新消 / 息」。
- 新增：`docs/images/` 四张模拟器截图（Paper 资讯流、Ink 深色、Lab、组件画廊），README 顶部展示。
- 验证：Pixel 6 AVD（API 35，1080×2800）实机截图；`publishToDist` + `:smoke:assembleDebug` 通过。

### v0.10.9（2026-10-23）

坐标迁移到 GitHub 命名空间，并打通 GitHub Packages。

- 变更：groupId 由 `io.openmetro` 改为 `io.github.jeta1me1plus`（`io.github.*` 不需要域名验证）。
- 变更：POM 的 `url` / `scm` / `developer` 指向 `github.com/Jeta1me1PLUS/OpenMetroUI`。
- 新增：GitHub Packages 发布通道（`./gradlew publishToGithub`），凭证存在时才挂仓库。
- 新增：`.github/workflows/publish.yml`，打 `v*` 标签自动跑测试并发布。
- 变更：`dist` 与文档中的坐标、目录结构同步更新。
- 未验证：GitHub Actions 上的首次发布（仓库尚未创建）。

### v0.10.8（2026-10-23）

外部项目接入反馈的三条问题。

- 修复 1：`OmTheme` 默认铺 `background` 底色（新增 `paintBackground` 开关），解决深色主题下露出浅色窗口底。
- 修复 2：`OmScreen` 默认处理系统栏内边距（新增 `applyInsets` 开关），不再需要每页手写 `statusBarsPadding()`。
- 新增 3：`OmIconButton`（48dp 触摸区、24dp 图标、可选 tint），`OmTopBar` 的返回键改为复用。
- 变更：sample 的骨架改为「内容区由 OmScreen 处理 insets、底部导航自己 `navigationBarsPadding()`」；NEWS 页因为是自定义骨架，自己加了 `statusBarsPadding()`。
- 未验证：宿主项目升级后是否出现双倍 inset。

### v0.10.5（2026-10-21）

发布产物整理与一个发布缺陷的修复。

- 修复：发布的 POM / module metadata 里 Compose 依赖**没有版本号**，宿主根本无法解析。根因是用 `api(platform(bom))` 导出依赖，Gradle 不会把版本写进 metadata。改为显式版本 `compose = "1.11.4"`。
- 新增：`dist/` 交付目录（本地 Maven 仓库 + 独立使用说明），`publishToDist` 聚合任务，`.gitignore` 只保留 `dist/README.md`。
- 已验证：`smoke` 模块（纯 Maven 坐标）编译通过，依赖树从 `dist/maven` 解析、Compose 版本 1.11.4 完整。

### v0.10.4（2026-10-21）

真机验证通过后的缺陷收尾与发布准备。

- 修复 1：`OmTag` 删除按钮命中区由 14dp 扩到 28dp（标签完整高度），并在 00 登记为触摸目标的唯一例外。
- 修复 2：`OmSheet` 的手势从整块面板移到 48dp 手柄区，面板内滚动不再被拖拽抢走。
- 新增：四个模块的 `maven-publish` 配置与 sources jar，`publishToMavenLocal` 验证通过。
- 新增：README 的快速开始（接入方式、最小页面、换强调色、配色校验）。
- 未验证：两处修复的真机表现；TalkBack 与 RTL。

### v0.10.2（2026-10-21）

真机走查（第一轮冒烟 + 第二轮重点 + 第三轮极端条件）反馈两条问题。

- 修复 1：`OmTileMode.Spectrum` 开关无效 —— `tileMode` 只是下发了 CompositionLocal，没有组件读它。新增 `OmTileTone` 语义与 `OmTileColors.resolve()`，`OmTile` 改为按色调取色。
- 修复 2：底部栏 `items` 每次重组新建 List，导致切页时整条底栏无法跳过重组。提到顶层缓存。
- 新增：sample 的 `release` buildType（debug 签名、不混淆），用于判断「卡顿」是否只是 debug 构建。
- 未验证：release 构建下切页的实际手感。

### v0.10.0（2026-10-21）

P2 收官：最后 3 个组件落地，组件清单的 37 项全部实现。

- 新增：`OmProgressRing`、`OmPager` / `OmPagerIndicator`、`OmCollapsingTopBar`。
- 变更：`OmScreen` 增加 `scrollState` 参数；NEWS 页改为固定页头 + 滚动收缩。
- 修复：`OmCollapsingTopBar` 组合期直读 `scrollState.value` 导致每帧重组，改用 `derivedStateOf`（lint 的 `FrequentlyChangingValue` 抓到）。
- 未验证：页头收缩的两个状态切换、Pager 的分页吸附与页码同步、环形进度旋转。

### v0.9.0（2026-10-21）

P2 第二批：三个「展开/收起」组件。

- 新增：`OmAccordion`、`OmRail`、`OmDrawer`。
- 变更：无。
- 修复：无。
- 未验证：折叠展开的高度动画、侧栏在 840dp 以上设备的实际宽度占比、抽屉的 RTL 滑入方向。

### v0.8.0（2026-10-21）

P2 第一批：6 个「补全感」组件。

- 新增：`OmSnackbar`、`OmBadge`、`OmSkeleton`、`OmEmptyState`、`OmSearchField`、`OmTooltip`。
- 变更：`OmTextField` 增加 `onTrailingIconClick`，尾部图标可点且触摸区 48dp。
- 修复：无。
- 未验证：Snackbar 入场动画、骨架脉冲在深色主题下的可读性、EmptyState 图形比例。

### v0.7.0（2026-10-21）

工具链补齐：校验模块 + 运行时调参台。

- 新增：`omui-test` 模块与 `OmCheck`（对比度 / 4dp 网格 / 触摸目标 / 字阶行高 / 间距）。
- 新增：demo 的 LAB 页（实时调 accent、主题、Spectrum、字号缩放）与网格叠加层。
- 变更：`OmColorScheme.contrastChecks()` 与 `ContrastCheck` 由 internal 改为 public；`OmContrast` 增加 `Color` 版 `ratio`；`build.sh` 串入 `omui-test` 的测试。
- 修复：无。
- 未验证：Lab 的字号缩放对复杂布局的实际影响、网格叠加层在真机上的可读性。

### v0.6.0（2026-10-21）

P1 收官：9 个组件一轮做完，公共 API 达到 30+ 个。

- 新增：`OmCard`、`OmCheckbox`、`OmRadio`、`OmSlider`、`OmProgressBar`、`OmTag`、`OmAvatar`、`OmDialog`、`OmSheet`。
- 变更：无。
- 修复：`OmDialog` 的参数顺序调整（`actions` 在 `content` 之前，才能用 trailing lambda 写正文）。
- 未验证：Dialog 的返回键与点击外部关闭、Sheet 的拖拽阈值与两档高度、Slider 的点击定位精度、ProgressBar 扫描动画。

### v0.5.0（2026-10-21）

P1 起步 + 两个真实页面。走 MINOR：demo 从「组件罗列」变成「能用的应用」。

- 新增：`OmPivot`、`OmBottomBar`、`OmSwitch`、`OmIcons.Grid/Sliders/Article`。
- 新增 demo：四页底部导航、资讯流页、设置页。
- 变更：`OmScreen` 增加 `scrollable` 参数；Spec 与 Gallery 拆成两个独立页面。
- 修复：`OmSwitch` 的偏移改用 lambda 版本（选中时不再每帧重组）；`NewsScreen` 的页码改用 `mutableIntStateOf`。
- 未验证：底部导航在四页之间的切换、页签指示条宽度、开关滑块与触摸区。

### v0.4.0（2026-10-21）

P0 收官：列表与磁贴落地，13 个 P0 组件全部完成。

- 新增：`OmList` / `OmListItem`、`OmTile` / `OmTileGrid`。
- 变更：无。
- 修复：无。
- 未验证：磁贴翻转的透视观感、背面内容的可读性、列表选中竖条与分隔线的视觉冲突。

### v0.3.0（2026-10-21）

完成 P0 的输入与页头，走 MINOR：一个带表单的页面现在可以用本库完整画出来。

- 新增：`OmTextField`、`OmTopBar`。
- 变更：`omui-components` 传递依赖 `omui-icons`（页头需要默认返回箭头）。
- 修复：无。
- 未验证：输入框聚焦线切换、多行输入增长、大标题在窄屏换行。

### v0.2.0（2026-10-21）

新增 6 个公共 API 与图标集，走 MINOR：组件层从「3 个文本原语」进入「能画出真实界面」。

- 新增：`omClickable`、`omFocusRing`、`OmGrid` + `omGridSpan`、`OmScreen`、`OmSection`、`OmIcon`、`OmButton`、`OmIcons`（9 个图标）。
- 变更：`omui-icons` 由空模块变为可用。
- 修复：无。
- 未验证：按钮按压与焦点环的实际观感、网格换行在窄屏的表现、图标路径的视觉重量。

### v0.1.0（2026-10-21）

首个代码版本。走 MINOR 起算：建立了 Token 层与工程骨架，项目从「只有文档」进入「可编译、可运行、可校验」。

- 新增：Gradle 四模块工程、Token 层、对比度校验体系、L1 三个原语、Spec 页 demo。
- 修复：无。
- 未验证：真机渲染、深色主题下的系统栏、大字号与 RTL（见第四节）。

---

## 三、构建与验证

```bash
# 一键：单元测试 → 编译 → 归档 → 删旧包
./build.sh

# 附带 lint
./build.sh --lint

# 构建后安装并启动到设备 / 模拟器
./build.sh --install
```

手动调用 Gradle：

```bash
# 构建 demo
./gradlew :sample:assembleDebug

# 单元测试（对比度校验 + 校验工具自测，秒级）
./gradlew :omui-core:testDebugUnitTest :omui-test:testDebugUnitTest

# lint
./gradlew :omui-core:lintDebug :omui-components:lintDebug :omui-test:lintDebug :sample:lintDebug

# 检查编译告警（应为空）
./gradlew :sample:assembleDebug --console=plain 2>&1 | grep -E "^[ew]: "
```

产物：Gradle 输出在 `sample/build/outputs/apk/debug/sample-debug.apk`；`build.sh` 会额外归档一份到项目根目录，命名 `OpenMetroUI-v<版本>-debug.apk`，并只保留最新一版（旧包在成功构建并校验后才删除）。日志写入 `build.log`，两者都被 `.gitignore` 忽略。

### （一）lint 的既有例外

以下检查项在 `build.gradle.kts` 的 `lint {}` 里逐条关闭，理由写在代码注释里：

| 检查项 | 关闭理由 |
|---|---|
| `AndroidGradlePluginVersion` | AGP 9.1.0 最高支持 compileSdk 36，一批依赖被卡住 |
| `GradleDependency` | 同上（版本天花板，见 `libs.versions.toml` 注释） |
| `NewerVersionAvailable` | 同上 |
| `OldTargetApi` | targetSdk 不能高于 compileSdk |

除以上四项，lint 报告应保持为空。

---

## 四、真机验证状态

按协作规则，AI 不主动做设备验证，以下结果全部来自用户真机走查（2026-10-21，v0.10.2）。

### （一）已通过

| 项目 | 结果 |
|---|---|
| 启动、五页切换、各页滚动 | 正常 |
| Ink 主题（含状态栏图标） | 正常 |
| 磁贴翻转（背面不镜像） | 正常 |
| Sheet 拖拽（展开 / 关闭） | 正常 |
| Drawer 从 start 滑出 | 正常 |
| 字号缩放到 1.6× | 无裁切 |
| 网格叠加 | 正常 |
| accent 切换（COBALT / FOREST） | 正常 |
| Ink + 网格叠加、横屏 | 正常 |

### （二）修复后待复验

| 项目 | 说明 |
|---|---|
| Spectrum 切换 | v0.10.2 之前开关无效（`tileMode` 没人读），现已接上 `OmTileTone`，需再切一次确认磁贴变色 |

### （三）未测

| 项目 | 期望 |
|---|---|
| 页头滚动收缩 | 滚过 48px 从 48sp 切 22sp |
| Pager 左右滑 | 整页吸附，指示器同步 |
| 输入框聚焦 | 底线由 1dp 灰变 2dp 红 |
| Accordion 展开 | 高度平滑展开，箭头旋转 |
| RTL | 文本与位移镜像（未做语言切换验证） |

### （四）性能结论

切页卡顿只出现在 **debug 构建**（11M）；`release`（6.3M，debug 签名）下明显流畅。后续性能判断一律以 release 为准，debug 构建不作为依据。

---

## 五、待办

### （一）等待用户输入

- [01 第七节](01-requirements.md#七待拍板项) 的 Q3（demo APK 是否随库发布）、Q4（`omui-test` 模块）。
- [00 第十一节](00-design-language.md#十一待拍板) 的 D1–D5：v0.1.0 已按建议值落地，待确认。

### （二）已决的待决设计问题

无。

### （三）工程基础设施缺口

- 无 CI；当前靠本地 `./build.sh --lint` 与 `:smoke:assembleDebug` 手工验证。
- Maven Central 的签名与凭证未配置（四件前置与步骤见 [05 · 发布指南](05-releasing.md)）。
- 图标用 `Path` 手工编写，没有绘制工具链（T3）。

### （四）后续需求候选

- P0 剩余组件（按 [03](03-component-inventory.md#三p0-组件规格) 的顺序）。
- Gallery / Showcase / Lab 三个 demo 区域。
- `OmGrid` 的调试网格叠加（4dp 基线 + 栏网格）。

### （五）已决定不做

- Material3 兼容层、View 双体系、KMP 版本、打包字体：理由见 [01 第二节](01-requirements.md#二目标与非目标)。
- 阴影、渐变、毛玻璃、ripple：理由见 [00 第一节](00-design-language.md#一设计立场)。
