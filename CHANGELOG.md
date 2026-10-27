# CHANGELOG

按版本倒序，只列改了什么。

## v0.17.3 · 2026-10-27

### 修复

- **`OmPivot` 从 0.5.0 起就只显示第一个页签**：指示条是用 `fillMaxWidth()` 的子 `Box` 实现的，放在 `Row` 里会把整列撑到全宽，第二个及以后的页签被推出屏幕右侧。改为在列自身的 `drawBehind` 里按文字宽度绘制，并把 3dp 高度计入底部内边距。
- 这个问题被组件测试暴露：`pivot_selection_changes` 点不到第二个页签，改用语义 action 能通过 —— 说明不是点击逻辑坏，而是页签根本不在屏幕内。

## v0.17.2 · 2026-10-27

组件测试从 9 个扩到 18 个，新测试又抓到两个无障碍缺陷。

### 新增

- 9 个测试：分段控件选中、增减器加减与边界、标签输入回车提交、搜索框清除、进度条语义、日期选择、懒加载只组合可见项、提示文本、无删除按钮的标签。

### 修复

- `OmTextField` 的可点尾部图标没有 `contentDescription`，读屏只会念「图片」。新增 `OmStrings.clear` 并接到搜索框的清除按钮上。
- `OmStepperInput` 的禁用按钮没有 `Disabled` 语义，读屏说不出「不可用」。把描述与禁用状态统一挂到容器节点，而不是图标节点。

### 变更（破坏性）

- `OmStrings` 新增 `clear` 字段。自己构造 `OmStrings` 实例的话需要补上这一项；用预设的 `Chinese` / `English` 不受影响。

## v0.17.0 · 2026-10-27

国际化、懒加载列表、组件测试与迁移文档。

### 新增

- `OmStrings` + `LocalOmStrings`：组件内置文案（读屏描述、占位符、月份名、星期名）不再硬编码，默认按系统语言在中英之间选，可用 `OmTheme(strings = …)` 覆盖。
- `OmLazyList`：`LazyColumn` 版本，几百条以上数据用它；`OmList` 保留给短列表。**不能嵌套在 `OmScreen(scrollable = true)` 里**。
- `MIGRATION.md`：补齐 0.10 到 0.17 的全部破坏性变更。
- `omui-components` 的 9 个行为测试（`src/androidTest`）：按钮点击、标签删除、左滑删除、页签切换、磁贴翻面、复选框切换、下拉选中、表格行点击、折叠展开。

### 修复

- `OmCheckbox` / `OmSwitch` / `OmRadio` 缺状态语义：只设了 `Role`，读屏说不出「已选中 / 未选中」。补上 `toggleableState` / `selected`。

### 变更（破坏性）

- 四个占位符/描述参数改为可空，不传时从 `OmStrings` 取：`OmDropdown.placeholder`、`OmSearchField.placeholder`、`OmChipInput.placeholder`、`OmTopBar.backContentDescription`。

### 未验证

- 英文环境下的实际渲染（月份名、星期缩写）
- 页签在测试环境里点击不触发（用语义 action 可以），demo 中触摸正常，原因待查

## v0.16.0 · 2026-10-27

无障碍第二批与 API 补全。

### 新增

- `OmAnchorPositionProvider`：`OmMenu` 与 `OmDropdown` 的弹出面板改为按窗口空间自动翻转。下方放得下就向下，放不下且上方更宽裕就向上，水平方向不越出窗口。
- `OmSheet` 增加 `dragHandleOnly`：内容不滚动时传 `false`，整块面板都能拖。
- `OmPivot` 增加 `pagerState`：传入后点击标签会 `animateScrollToPage`，手动滑页也会同步选中态（单向数据流，不循环）。
- `compileOnly` 引入预览注解，新增 5 个 `@Preview`（按钮、输入、磁贴、列表、Ink 主题）。

### 变更

- `OmCard` 的 Ink 变体现在会**自动反转内部文字与描边色**，调用方不必再手动传 `onInverse`。

### 未验证

- 菜单在窗口底部翻转后的实际位置（一次实测中发现会压住锚点 48dp，已改为向上翻时反向应用偏移，但未再次截图确认）
- `OmPivot` 联动 pager 时的滚动动画衔接

## v0.15.0 · 2026-10-27

无障碍第一批：RTL、语义、触摸目标、动效降级。

### 新增

- RTL：5 处 `drawBehind` 装饰跟随 `LayoutDirection` 镜像（列表选中条、Banner / Snackbar 色条、Rail 与 Dropdown 的选中条）；`OmSwipeToDismiss` 在 RTL 下改为右滑、删除区移到左侧。
- `OmMotion.scaledBy()`：`OmTheme` 读取系统的「动画时长缩放」，设为 0（减弱动态效果）时所有时长归零。
- Lab 增加「强制从右往左」开关，可随时检查镜像。

### 变更

- `OmTag`：视觉仍是 28dp 薄片，**触控区改为 48dp** —— 整块占 48dp，背景与边框只画在垂直居中的 28dp 里，尾部 × 也拿到 48dp 见方。
- `OmSnackbar` / `OmBanner`：加 `liveRegion`，出现时会被读屏朗读。
- `OmProgressBar` / `OmProgressRing`：补 `progressBarRangeInfo`，读屏能报出百分比。
- `OmSwipeToDismiss`：加自定义无障碍操作「删除」，TalkBack 用户不必执行滑动手势。

### 未验证

- TalkBack 的实际朗读顺序与措辞（需要真机开启读屏）
- 系统「动画时长缩放」设为 0 时的观感

## v0.14.7 · 2026-10-24

### 修复

- `OmSwipeToDismiss` 的删除区一直可见：内容层本身是透明的（列表项平时没有底色），下面的红色背景透过来了，看上去像「没滑就显示」。给内容层加上 `background(colors.background)`，现在只有左滑时才露出。

## v0.14.6 · 2026-10-24

### 修复

- `OmListItem` 的选中竖条上一版被画到了容器之外（页边距里），与 `accentQuiet` 色块分离成两个不相干的元素。改回色块内部最左，并给列表项加上恒定的 12dp 左内边距，使竖条与文字之间保持 9dp 间隙。内边距不随选中状态变化，所以不会跳动。

### 变更（影响现有布局）

- `OmListItem` 现在自带 12dp 左内边距。此前它完全依赖外层容器的页边距。列表文字会比同屏的磁贴向右偏 12dp。

## v0.14.5 · 2026-10-24

三个真机反馈的问题。

### 修复

- `OmListItem` 的选中竖条画在 `x = 0..3dp`，而文字也从 `x = 0` 开始，竖条把文字左边缘盖住了。改为画在容器左边界**之外**（也就是页边距里）：3dp 条 + 7dp 间隙，文字位置不变、选中与否不跳动。
- demo 的左滑删除把 `onDismiss` 写成空实现，滑完立刻弹回，看起来像「完全没反应」。改为真实移除条目，并用 `key` 保证列表重组正确。
- demo 的 Error Banner 漏了 `onDismiss`，那条红色提示关不掉。

## v0.14.2 · 2026-10-24

两个真机上暴露的问题。

### 修复

- `OmSwipeToDismiss` 的左滑完全无响应：拖动回调里用 `scope.launch { snapTo() }` 更新偏移，协程调度跟不上手指，看上去就像没动。改为同步的可变状态。
- `OmSwipeToDismiss` 的删除区原本整行铺 danger 色，内容左移后左侧也变红；改为只在右侧 96dp，左侧露出父容器底色。
- `OmBanner` 的动作区用了 `Box`（叠放布局），文字与 2dp 下划线重叠在一起；改用 `Column`。

## v0.14.0 · 2026-10-24

新增三个组件与磁贴轮播，总数 53 → 56。

### 新增

- `OmListSection`：全大写小标题 + 一组行 + 组间留白，统一设置页的分组节奏。
- `OmTimeline` + `OmTimelineItem`：2dp 竖线 + 方形节点，当前项为实心 accent，首尾不露出线头。
- `OmChipInput`：已提交的值显示为可删除标签，`FlowRow` 自动换行，回车提交、重复值忽略。
- `OmTile` 的 `rotatingTitles`：Live Tile 内容轮播，默认每 8 秒换一条，与翻面互不干扰。

### 修复

- `OmListSection` 的 `modifier` 位置不符合 Compose 规范（应是第一个可选参数），把 `title` 改为必填。

### 未验证

- Live Tile 轮播与翻面同时启用时的观感
- `OmChipInput` 在输入法弹起时的换行表现

## v0.13.0 · 2026-10-23

交互与布局两批一起做，总数 46 → 53。

### 新增

- `OmBanner`：通栏提示，左侧 4dp 色条，Info / Error 两档。底色只做极淡叠加，不抢内容。
- `OmErrorState`：与 `OmEmptyState` 同骨架，图形换成断开的十字 + danger 色断点。
- `OmOtpInput`：一个透明 `BasicTextField` 覆盖在 N 个方格上，不逐格建输入框。
- `OmSwipeToDismiss`：左滑露出 96dp 删除区，拖过 60% 触发。
- `OmStaggeredGrid`：按当前最短列填充，列宽固定、高度独立。
- `OmResponsivePane`：宽度 ≥840dp 时列表与详情并排，否则只显示列表。
- `OmSideSheet`：从 end 侧滑出，与 `OmDrawer` 对称。

### 修复

- demo 的 OVERLAY 区四个按钮挤在一排，最后一个被挤出屏幕；改成 2×2 网格。

### 未验证

- `OmSideSheet` 的真机滑入方向（截图时未捕获到面板）
- `OmSwipeToDismiss` 与纵向滚动的冲突

## v0.12.0 · 2026-10-23

新增数据展示与上下文菜单，总数 42 → 46。

### 新增

- `OmStat`：大数字（48sp Light）+ 全大写标签 + 可选变化量，无边框无底色。
- `OmSparkline`：1.5dp 折线 + 末端 4dp 实心方块。无填充、无渐变、无数据点。
- `OmTable` + `OmTableColumn` + `OmColumnAlign`：表头 1dp 结构线，行间 hairline，列宽按 weight 分配，数字列可右对齐。
- `OmMenu` + `OmMenuItem`：`Popup` 锚定的直角面板，支持 `destructive` 项。

### 未验证

- 菜单在屏幕下半部时的表现（当前不自动翻转，会超出屏幕）
- 表格列在超长文案下的截断

## v0.11.0 · 2026-10-23

补齐表单类组件，总数 38 → 42。

### 新增

- `OmSegmentedControl`：等分固定宽度的互斥选项，选中段整块填 accent，段间 1dp 竖线；与 `OmPivot` 的区别是强制等分、不滚动。
- `OmStepperInput`：两侧 48dp 方形按钮夹一个数字，到边界时按钮变灰而非回绕。
- `OmDropdown`：触发区用填充变体形态，面板贴在下方、宽度对齐触发区；用 `Popup` 而不是 `Dialog`。选中项沿用列表的 `accentQuiet` + 3dp 竖条。
- `OmDatePicker` + `OmDate`：7 列月历，选中为 accent 实心方块，今天为 2dp 描边。用 `Calendar` 而非 `java.time`，避免引入 desugaring。
- `OmIcons.ChevronDown` / `OmIcons.Minus`（图标共 15 个）。

### 变更

- `versionCode` 规则改为 `MINOR * 100 + PATCH`（0.11.0 → 1100）。旧规则 `MINOR + PATCH` 在 0.11.0 会算出 11，小于 0.10.10 的 20，不满足单调递增。

### 未验证

- 下拉面板在长列表中的定位、日期选择器的跨月切换、分段控件在超长文案下的表现。

## v0.10.10 · 2026-10-23

### 新增

- `images/` 四张模拟器截图（Pixel 6 / API 35）：Paper 资讯流、Ink 深色、Lab 调参台、组件画廊。README 顶部以表格并排展示。

### 修复

- `OmTile` 的 Paper 色调缺少 1dp 描边。白磁贴在米白底上、深灰磁贴在近黑底上边界完全不可见，三块磁贴会糊成一片。规范 `00` 里本来就要求这条描边，是实现漏做。

### 变更

- demo 的收件箱磁贴改为 `title = "收件箱"` + `label = "3 条新消息"`，避免 28sp 标题在 156dp 宽的磁贴里断行成「3 条新消 / 息」。

## v0.10.9 · 2026-10-23

### 变更（坐标迁移，**会影响依赖写法**）

- groupId 从 `io.openmetro` 改为 `io.github.jeta1me1plus`。`io.github.*` 由 GitHub 账号验证，不需要域名，Maven Central 与 GitHub Packages 都接受。
- 依赖写法：`io.github.jeta1me1plus:omui-components:<版本>`。
- POM 的 `url` / `scm` / `developer` 指向 `github.com/Jeta1me1PLUS/OpenMetroUI`。
- `dist/maven` 的目录结构随之变为 `io/github/jeta1me1plus/...`。

### 新增

- GitHub Packages 发布通道：`./gradlew publishToGithub`。仓库只在检测到凭证时挂载，不影响本地构建。
- `.github/workflows/publish.yml`：打 `v*` 标签自动运行测试并发布到 GitHub Packages，也支持手动触发。

## v0.10.8 · 2026-10-23

外部项目接入反馈的三条修正。

### 新增

- `OmIconButton`：图标按钮，48dp 触摸区 + 24dp 图标，支持 `enabled` 与自定义 `tint`。`OmTopBar` 的返回键改为复用它。

### 变更（**升级需注意**）

- `OmTheme` 默认铺一层 `background` 底色。深色主题下不再露出浅色窗口底；局部嵌套主题请传 `paintBackground = false`（内部用 `fillMaxSize`，无界高度里会抛约束异常）。
- `OmScreen` 默认用 `windowInsetsPadding(WindowInsets.safeDrawing)` 避开系统栏。骨架外层已统一处理 insets 时传 `applyInsets = false`，否则会双倍间距。
- sample 骨架随之上移一层职责：内容区交给 `OmScreen`，底部导航自己 `navigationBarsPadding()`。

## v0.10.5 · 2026-10-21

### 新增

- `dist/` 交付目录：`dist/maven` 是可直接拷走的本地 Maven 仓库（AAR + sources + POM + 校验和），`dist/README.md` 是面向使用者的完整说明。
- 根项目 `publishToDist` 聚合任务，一次生成四个模块的产物。

### 修复

- **发布产物里的依赖没有版本号**：原先用 `api(platform(compose-bom))` 导出 Compose 依赖，Gradle 不会把版本写进 POM / module metadata，宿主解析会直接失败。改为 `compose = "1.11.4"` 显式版本，POM 与 metadata 均带版本。

### 变更

- `libs.versions.toml` 增加 `compose` 版本，六个 Compose 依赖改为显式引用。
- `.gitignore` 忽略 `dist/maven`（可重新生成），保留 `dist/README.md`。

## v0.10.4 · 2026-10-21

### 新增

- 四个模块的 `maven-publish` 配置：`release` 变体 + sources jar，坐标 `io.openmetro:omui-core / omui-components / omui-icons / omui-test`。
- README 增加「快速开始」：本地仓库接入、最小页面、替换强调色、配色校验。

### 修复

- `OmTag` 删除按钮的命中区由 14dp 扩大到标签完整高度 28dp。这是触摸目标 48dp 规则的唯一例外，已登记在 `00`。
- `OmSheet` 的拖拽手势从整块面板移到顶部 48dp 手柄区，面板内放滚动列表时不再被拖拽抢走手势。代价是不能从内容区上滑展开。

### 变更

- `build.sh` 支持 `--release`，按构建类型选择任务、产物路径与归档命名。

## v0.10.2 · 2026-10-21

### 修复

- `OmTileMode.Spectrum` 开关无效：`tileMode` 只下发了 CompositionLocal，没有组件读取。新增 `OmTileTone`（Paper / Quiet / Ink / Accent）与 `OmTileColors.resolve(tone, mode)`，`OmTile` 的 `color` 改为可空覆盖，默认按色调取色。
- 底部栏 `items` 每次重组重新 `map`，List 引用一直变，`OmBottomBar` 无法跳过重组。改为顶层 `by lazy` 缓存。

### 新增

- sample 的 `release` buildType：使用 debug 签名、不开启混淆，产物可直接安装，用于判断卡顿是否只来自 debug 构建。

### 变更

- `OmTile` 调用点由 `color = colors.tiles.xxx` 改为 `tone = OmTileTone.xxx`。

## v0.10.0 · 2026-10-21

P2 完成，37 个组件全部落地。

### 新增

- `OmProgressRing`：确定态按比例画弧（起点 -90°，端点为 Butt），不确定态为 90° 弧段旋转。
- `OmPager`：基于 foundation `HorizontalPager` 的分页轮播；`OmPagerIndicator` 用 24×3dp 直角线段。
- `OmCollapsingTopBar`：滚动超过 48px 后从 48sp 大标题切到 22sp 紧凑标题，`AnimatedContent` 淡入 + 上滑，不做连续字号插值。

### 变更

- `OmScreen` 增加 `scrollState` 参数，可把同一个滚动状态交给 `OmCollapsingTopBar`。
- NEWS 页改为「固定页头 + 滚动内容」，滚动时标题收缩。
- demo 画廊新增 PAGER 区，Progress 区补环形进度。

### 修复

- `OmCollapsingTopBar` 在组合期直接读 `scrollState.value`，会随每一帧滚动重组；改用 `derivedStateOf` 只在跨阈值时变化。

### 未验证

- 页头收缩的切换时机、Pager 吸附与页码同步、环形进度旋转的视觉速度。

## v0.9.0 · 2026-10-21

### 新增

- `OmAccordion`：标题行 + 副标题，展开用 `expandVertically` + `fadeIn`，指示器同步旋转 90°；展开状态由组件内部持有。
- `OmRail`：侧边导航，宽 80dp、每项 72dp，图标在上标签在下，选中态为 start 侧 3dp accent 竖条。
- `OmDrawer`：从 start 侧滑出的直角面板，最宽 320dp（窄屏 82%），滑入方向按 `LocalLayoutDirection` 镜像。
- demo 画廊新增 ACCORDION / RAIL 两区，OVERLAY 区增加 DRAWER 入口（内容为三项导航列表）。

### 未验证

- 折叠展开的高度动画、侧栏在 840dp+ 设备上的宽度占比、抽屉在 RTL 下的滑入方向。

## v0.8.0 · 2026-10-21

### 新增

- `OmSnackbar`：反白底 + 左侧 4dp accent 竖条；淡入 + 上移 24dp；操作文字用 `onInverse` 加 2dp accent 下划线。
- `OmBadge`：直角角标，18dp 高；`text = null` 时显示 8dp 实心方块（未读点）。
- `OmSkeleton`：明度脉冲（40%–100% 呼吸），不是扫光渐变。
- `OmEmptyState`：巨字标题 + 网格线「取景框」图形（外框 / 十字 / 2dp accent 中心块），支持描述与动作槽位。
- `OmSearchField`：填充变体输入框 + 搜索图标 + 有内容时的清除按钮。
- `OmTooltip`：反白底直角提示，无箭头。

### 变更

- `OmTextField` 增加 `onTrailingIconClick`；尾部图标用 48dp Box 撑触摸区（`OmIcon` 会覆盖 modifier 的 size）。
- demo 画廊新增 STATUS 区（Badge / Skeleton / Tooltip / EmptyState / Snackbar），INPUT 区加搜索框。

### 未验证

- Snackbar 入场动画、骨架脉冲在深色主题下的可读性、EmptyState 图形比例。

## v0.7.0 · 2026-10-21

### 新增

- `omui-test` 模块与 `OmCheck`：
  - `requireContrast` / `contrastViolations`：遍历语义色对与磁贴色对
  - `requireOnGrid` / `gridViolations`：4dp 网格
  - `requireTouchTarget` / `touchTargetViolation`：48dp 下限
  - `requireTypography` / `typographyViolations`：字号必须在字阶表内、行高是 4sp 倍数、字重只有 300/400/600
  - `requireSpacing` / `spacingViolations`、`gridConfigViolations`
- demo 的 LAB 页：实时调 accent（6 个预设）、Paper/Ink、Monochrome/Spectrum、字号缩放 0.85–1.60×。
- 网格叠加层 `GridOverlay`：4dp 基线（中性色 10%）+ 栏网格（accent 30%），叠加在所有页面之上。
- `OmIcons.Beaker`（第 13 个图标）。

### 变更

- `OmColorScheme.contrastChecks()` 与 `ContrastCheck` 改为 public，供 `omui-test` 复用同一份清单。
- `OmContrast` 增加接受 `Color` 的 `ratio` 重载。
- `build.sh` 串入 `:omui-test:testDebugUnitTest` 与 `:omui-test:lintDebug`。
- demo 由四页变五页，`LabSettings` 状态提升到 `MainActivity`。

### 未验证

- 字号缩放到 1.6× 时各页面的实际布局、网格叠加层在真机上的可读性。

## v0.6.0 · 2026-10-21

P1 完成。

### 新增

- `OmCard`：Flat / Outlined / Ink 三变体，直角、无阴影，内边距 16dp。
- `OmCheckbox`：20dp 方形，选中填充 accent + 2dp 白勾（方头直角），勾用 `Path` 绘制。
- `OmRadio`：20dp 圆环 + accent 实心内点。
- `OmSlider`：2dp 轨道 + 16dp 方形滑块；用 `awaitEachGesture` 同时支持点击定位与拖动跟手。
- `OmProgressBar`：4dp 直角；确定态按比例填充，不确定态为硬边方块扫描（无渐隐拖尾）。
- `OmTag`：28dp 直角标签，未选中 1dp 描边、选中 accent 填充，支持可点与可删除。
- `OmAvatar`：圆形首字母头像，字号随直径缩放（0.4 倍），24/32/40/56dp。
- `OmDialog`：平台 Dialog + 自绘全屏 scrim，宽度上限 400dp，淡入 + 上移 24dp，按钮行右对齐。
- `OmSheet`：底部面板，两档高度（半屏 / 全屏），拖拽跟手（`snap()`）、松手 250ms 回弹。
- demo 画廊新增 CARD / FORM / TAG·AVATAR / OVERLAY 四区。

### 变更

- `OmDialog` 参数顺序：`actions` 放在 `content` 之前，使正文可用 trailing lambda。

### 未验证

- Dialog 的返回键与点击外部关闭、Sheet 拖拽阈值与高度切换、Slider 点击定位精度、ProgressBar 扫描动画。

## v0.5.0 · 2026-10-21

### 新增

- `OmPivot`：横向页签，选中项下方 3dp accent 指示条，宽度等于文字宽度；未选中时透明占位，切换不改变高度。
- `OmBottomBar`：3–5 项等分，选中态为顶部 3dp accent 条 + accent 着色。
- `OmSwitch`：44×24 直角轨道 + 20dp 方形滑块，150ms 位移，触摸区 48dp。
- `OmIcons` 新增 Grid / Sliders / Article 三个图标（共 12 个）。
- demo 四页骨架：底部导航切换 NEWS / SPEC / UI / SETTINGS。
- Showcase 资讯流页：大标题页头 + Pivot + 磁贴（含翻转）+ 文章列表。
- Showcase 设置页：四个分组列表 + 三个开关 + 主题切换 + 版本号读 `BuildConfig`。

### 变更

- `OmScreen` 增加 `scrollable` 参数（整页滚动）。
- Spec 与 Gallery 拆成两个独立页面，页面容器统一由 `OmScreen` 负责。
- sample 启用 `buildConfig`，设置页版本号不再手写。

### 修复

- `OmSwitch` 的滑块偏移改用 `Modifier.offset {}` lambda 版本，避免选中时每帧重组。
- `NewsScreen` 的选中页签索引改用 `mutableIntStateOf`，避免 Int 装箱。

### 未验证

- 四页切换时底部导航的选中反馈、Pivot 指示条与文字宽度是否严格一致、开关在列表行右侧的垂直对齐。

## v0.4.0 · 2026-10-21

P0 完成。

### 新增

- `OmListItem`：22sp 主文本 + 可选次级文本；选中态为整行 `accentQuiet` + start 侧 3dp accent 竖条（竖条用 `drawBehind` 绘制，不占布局宽度）；最小高度 56dp，高度由内容决定。
- `OmList`：数据驱动，自动在项之间插入 hairline 分隔线，最后一项不画。
- `OmTile`：色块磁贴，整块可点；Y 轴翻转（350ms，`cameraDistance = 12×density`，背面再转 180° 防镜像）；标题字号按实际宽度自适应（<100dp 用 18sp，<160dp 用 28sp，否则 36sp）。
- `OmTileGrid`：`OmGrid` 的语义包装，为后续出血与尺寸预设留位置。
- demo 增加列表与磁贴区（含翻转磁贴、Spectrum 四色）。

### 未验证

- 磁贴翻转的透视观感、背面内容可读性、选中竖条与分隔线是否打架。

## v0.3.0 · 2026-10-21

### 新增

- `OmTextField`：下划线 / 填充两种变体；标签固定在输入区上方（无浮动动画）；聚焦 2dp accent 线、错误 2dp danger 线；底部线槽位恒为 2dp，线变粗时布局不抖；支持多行、前置/后置图标、辅助与错误文本。
- `OmTopBar`：大标题（48sp Light + 可选全大写标签）与紧凑（22sp）两种形态；高度由内容决定；返回按钮 48dp 触摸区 + 焦点环。
- demo 增加输入区（下划线/填充/错误/禁用、多行带图标）与页头示例（大标题、紧凑 + 操作区）。

### 变更

- `omui-components` 增加对 `omui-icons` 的 `api` 依赖，用于页头的默认返回箭头。

### 未验证

- 输入框聚焦时下划线由 1dp 变 2dp、多行输入的高度增长、大标题在窄屏的两行换行。

## v0.2.0 · 2026-10-21

### 新增

- `Modifier.omClickable`：明度下沉式按压反馈（8% 黑 / 10% 白覆盖层），替代水波纹。
- `Modifier.omFocusRing`：2dp accent 焦点环，画在元素外侧，不改变布局。
- `OmGrid` + `Modifier.omGridSpan(span, offset)`：跨栏布局，一行排满自动换行，行高取该行最高子项。
- `OmScreen`：页边距 + 正文最大宽度（640dp）。
- `OmSection`：固定区块节奏（上间距 32dp + 可选标签/标题）。
- `OmIcon`：单色几何图标渲染，默认 24dp。
- `OmButton`：5 变体（Filled / FilledInk / Outlined / Text / Destructive）× 3 尺寸 × 禁用/图标/填充宽度。
- `OmIcons`：9 个几何图标（ArrowBack、ArrowForward、Close、Check、Add、ChevronEnd、Menu、Search、MoreVert），24dp 画布、1.5dp 描边、方头端点。
- demo 的 `GallerySection`：按钮全状态、图标集、跨栏网格演示。

### 变更

- `omui-icons` 由空模块变为可用模块。

### 未验证

- 按钮按压与焦点环的实际观感、网格在窄屏的换行、图标路径的视觉重量。

## v0.1.0 · 2026-10-21

### 新增

- Gradle 四模块工程：`omui-core`、`omui-components`、`omui-icons`、`sample`（AGP 9.1.0 / Gradle 9.3.1 / Kotlin 2.2.20 / compileSdk 36 / minSdk 24）。
- Token 层：语义配色（Paper / Ink）、10 个字阶、9 档间距、栏网格、形状与描边、动效时长与缓动。
- `OmTheme` 与 7 个细粒度 CompositionLocal；强调色与 `onAccent` 自动推导。
- 对比度体系：`OmContrast` 纯 Kotlin 实现 + 构建期色对校验（8 个单元测试）。
- 过浅 / 过暗的强调色自动拉回可辨识范围（对最不利表面 ≥ 3:1）。
- L1 原语：`OmText`、`OmSurface`、`OmDivider`。
- demo 的 Spec 页：色板、语义色、字阶、间距、描边、磁贴色，支持 Paper / Ink 切换。

### 变更

- Paper `neutral.500`：`#8E8E88` → `#85857F`（对 `surfaceVariant` 由 2.94:1 提升到 3.31:1）。
- Ink `neutral.600`：`#7A7A74` → `#90908A`（对 `surfaceVariant` 由 3.92:1 提升到 4.71:1）。
- 新增语义 token `fieldLine`，承担输入框底线等必须可辨识的控件边界。

### 未验证

- 真机渲染、Ink 主题下的系统栏图标、`display.xl` 在窄屏的实际观感、Spectrum 八色的协调度。
