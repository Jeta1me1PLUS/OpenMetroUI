# 03 · 组件清单与规格

视觉数值来自 [00-design-language.md](00-design-language.md)，本文只定义「有哪些组件、每个组件长什么样、怎么交互、何时交付」。

优先级含义：**P0** = `v0.1.0` 必须；**P1** = 第一批扩展；**P2** = 后续，按需。

---

## 一、组件分层

| 层 | 内容 | 说明 |
|---|---|---|
| **L0 Token** | 主题、色/字/距/栅格/形/动效 | 不是组件，但被所有组件依赖 |
| **L1 原语** | 文本、表面、图标、分隔线、按压反馈、焦点环、揭示容器 | 只做视觉原子，不含业务语义 |
| **L2 结构** | 屏幕容器、栏网格、区块、行/列节奏 | 决定布局纪律，防止组件各自发明间距 |
| **L3 组件** | 按钮、磁贴、输入、导航、反馈、浮层 | 用户直接使用 |
| **L4 模式** | 空状态、骨架、搜索、设置页模板 | 组合出来的常见解法，可选依赖 |

---

## 二、总表

**当前进度（v0.10.8）**：**38 个组件全部实现** —— P0（#1–#13）、P1（#14–#25）、P2（#26–#37），以及外部使用反馈补入的 #38 `OmIconButton`。另加 `Modifier.omClickable` / `omFocusRing` 两个交互原语与 13 个图标的 `OmIcons`。

| # | 组件 | 层 | 优先级 | 一句话 |
|---|---|---|---|---|
| 1 | `OmTheme` | L0 | P0 | 主题入口与 token 下发 |
| 2 | `OmText` | L1 | P0 | 强约束字阶的文本 |
| 3 | `OmSurface` | L1 | P0 | 纯色容器（唯一允许的「卡片底」） |
| 4 | `OmIcon` | L1 | P0 | 单色几何图标 |
| 5 | `OmDivider` | L1 | P0 | 1dp / 2dp 结构线 |
| 6 | `OmButton` | L3 | P0 | 直角按钮，5 变体 |
| 7 | `OmTile` | L3 | P0 | 磁贴（旗舰） |
| 8 | `OmTileGrid` | L3 | P0 | 磁贴网格容器 |
| 9 | `OmTextField` | L3 | P0 | 下划线 / 填充输入框 |
| 10 | `OmTopBar` | L3 | P0 | 大标题页头 |
| 11 | `OmList` / `OmListItem` | L3 | P0 | 贴齐网格的列表 |
| 12 | `OmScreen` / `OmSection` | L2 | P0 | 页边距与区块节奏 |
| 13 | `OmGrid` | L2 | P0 | 栏网格容器（支持跨栏） |
| 14 | `OmPivot` | L3 | P1 | 横向页签，大字 + 3dp 指示条 |
| 15 | `OmBottomBar` | L3 | P1 | 底部导航，3–5 项 |
| 16 | `OmCard` | L3 | P1 | 直角容器，3 变体 |
| 17 | `OmCheckbox` | L3 | P1 | 方形勾选 |
| 18 | `OmRadio` | L3 | P1 | 圆形单选 |
| 19 | `OmSwitch` | L3 | P1 | 方形滑块 |
| 20 | `OmSlider` | L3 | P1 | 2dp 轨道 + 方形 thumb |
| 21 | `OmProgressBar` | L3 | P1 | 线性进度 |
| 22 | `OmTag` | L3 | P1 | 标签 / 筛选片 |
| 23 | `OmAvatar` | L3 | P1 | 圆形头像（允许的圆） |
| 24 | `OmDialog` | L3 | P1 | 直角对话框，巨字标题 |
| 25 | `OmSheet` | L3 | P1 | 底部面板 |
| 26 | `OmAccordion` | L3 | P2 | 折叠面板 |
| 27 | `OmSnackbar` | L3 | P2 | 左下角提示条 |
| 28 | `OmBadge` | L3 | P2 | 角标 |
| 29 | `OmProgressRing` | L3 | P2 | 环形进度 |
| 30 | `OmSkeleton` | L4 | P2 | 骨架（明度脉冲） |
| 31 | `OmEmptyState` | L4 | P2 | 空状态（巨字 + 网格线图） |
| 32 | `OmSearchField` | L4 | P2 | 搜索输入 |
| 33 | `OmTooltip` | L3 | P2 | 提示气泡（直角） |
| 34 | `OmRail` | L3 | P2 | 大屏侧边导航 |
| 35 | `OmDrawer` | L3 | P2 | 侧边抽屉 |
| 36 | `OmPager` | L3 | P2 | 全出血横滑轮播 |
| 37 | `OmTopBar`（收缩态） | L3 | P2 | 滚动时大标题转紧凑 |
| 38 | `OmIconButton` | L3 | P0 | 图标按钮：48dp 触摸区、24dp 图标（v0.10.8 补充） |

---

## 三、P0 组件规格

### （一）`OmButton`

| 项 | 规格 |
|---|---|
| 变体 | `Filled`（accent 底）、`FilledInk`（反白底）、`Outlined`（1dp `lineStrong`）、`Text`、`Destructive`（`danger` 底） |
| 尺寸 | `sm` 32dp、`md` 40dp、`lg` 48dp（均为**视觉高度**，触摸区统一扩展到 48dp） |
| 内边距 | 水平 `space.4`；`sm` 用 `space.3` |
| 文字 | 14sp / 20sp 行高 / 600 字重 / 字距 0.5sp；拉丁文案允许全大写，中文不转换 |
| 图标 | 可选前置/后置，18dp，与文字间距 `space.2` |
| 形状 | 0 圆角；描边内缩绘制 |
| 状态 | default / pressed（明度下沉一档）/ focused（外侧 2dp accent 环）/ disabled（文字 38%，填充 12%） |
| 宽度 | 默认由内容决定（`wrapContentWidth`）；提供 `fillWidth` 变体用于表单主按钮 |
| 语义 | `Role.Button`；禁用时 `disabled()`；加载态用 `stateDescription` 而非转圈替换文字 |

**不做**：圆角胶囊按钮、悬浮按钮（FAB）、渐变按钮。

### （二）`OmTile`

旗舰组件，Metro 的性格主要靠它承载。

| 项 | 规格 |
|---|---|
| 尺寸 | 由**跨栏数**决定，不写死 dp：宽度 = `n × 栏宽 + (n−1) × 栏间距`；高度按 `1:1`（方块）或 `2:1`（横条）由跨行数换算 |
| 常驻尺寸 | `span 1`（小块）、`span 2`（标准方块）、`span 4`（全宽横条） |
| 内容槽 | `label`（可选，11sp 全大写 1.5 字距）、`title`（`headline` 或 `display.m`，随尺寸切换）、`content`（自定义槽）、`trailingIcon` |
| 颜色 | Monochrome 模式取 `tile.paper` / `tile.quiet` / `tile.ink` / `tile.accent`；Spectrum 模式取八色对 |
| 翻转 | `flipEnabled` 时点击翻至背面；背面必须是**信息**（如计数、状态、次级标题），不允许纯装饰；两段各 175ms；翻回点任意处 |
| 按压 | 明度下沉，不缩放、不位移 |
| 选中 | 外扩 2dp accent 描边（不改变布局） |
| 无障碍 | 整块为单一可点区域；`contentDescription` 合并标题与背面信息；翻转后更新 |
| 文本限制 | 标题最多 2 行；超出降一级字号；再超出省略 |

**规则**：一个屏幕内磁贴不超过 9 块；同一网格内色相不超过 3 种且至少一块中性色。

### （三）`OmTileGrid`

| 项 | 规格 |
|---|---|
| 布局 | 按栏网格排布，支持 `span` 1/2/4 混排；行高按块高对齐，不留半格空隙 |
| 间距 | 块间 = 栏间距（16/24dp），与栏网格共用 |
| 出血 | 可选 `bleed` 参数，让网格跨越页边距延伸到屏幕边缘（Metro 的全出血） |
| 滚动 | 垂直滚动由外层负责；网格本身不滚 |

### （四）`OmTextField`

| 项 | 规格 |
|---|---|
| 变体 | `Underline`（默认，底部线）、`Filled`（`surfaceVariant` 底 + 底部线） |
| 结构 | 上标签（`label` token，全大写，位于输入区上方 `space.2`）→ 输入区 → 底部线 → 辅助/错误文本（`caption`） |
| 高度 | 最小 48dp，**由内容决定**；多行时随行数增长（不设 maxLines 上限，由宿主控制） |
| 底部线 | 默认 1dp `line`；聚焦 2dp `accent`；错误 2dp `danger`；禁用 1dp `line` + 38% 文字 |
| 光标与选择 | 光标 `accent`；选择手柄方形；选区底色 `accentQuiet` |
| 图标 | 可选前置/后置，20dp |
| 语义 | 标签通过 `semantics` 关联；错误文本用 `error()` 语义；`imeAction` 由宿主传入 |
| 不做 | 浮动标签动画（会带来位移抖动，且与 flush-left 网格冲突） |

标签固定在输入区上方，而不是浮进输入框——浮动标签的缩放动画与这套「稳定块面」的语言冲突。

### （五）`OmTopBar`

| 项 | 规格 |
|---|---|
| 变体 | `Large`（页头）：可选 `label` + `display.l` 标题 + 可选操作；`Compact`：`title` 22sp + 返回 + 操作 |
| 高度 | **由内容决定**，不写死 64dp；最小高度 48dp（含触摸区） |
| 对齐 | flush-left，标题与页边距对齐；返回图标贴齐页边距 |
| 操作区 | 最多 2 个图标按钮 + 1 个溢出菜单 |
| 分隔 | 默认无分隔线；滚动内容时可用 1dp `line`（由宿主控制） |
| 滚动收缩 | P2 实现，且只允许「两个状态切换 + 淡入」，不做连续字号动画 |

### （六）`OmList` / `OmListItem`

| 项 | 规格 |
|---|---|
| 行高 | 最小 56dp，由内容决定；两级文本时不压缩行高 |
| 内边距 | 水平贴齐页边距；垂直 `space.3` |
| 文本 | 主文本 `title`（22sp/600），次要 `body.m`（14sp），元信息 `caption` |
| 槽位 | `leading`（图标/头像）、`trailing`（数值/箭头/开关） |
| 分隔 | 默认 1dp `line`，从 start 侧页边距起始（inset），最后一项不画 |
| 选中 | 整行 `accentQuiet` + start 侧 3dp accent 竖条 |
| 交互 | 整行可点（若提供了 `onClick`）；`trailing` 内有独立控件时只让控件区响应 |
| 分组 | 支持分组标题（`label` 全大写 + `space.2` 上间距） |

### （七）L1 原语

| 组件 | 规格要点 |
|---|---|
| `OmText` | 只接受字阶 token（不接受任意 `TextStyle`）；颜色只接受语义别名；`maxLines` 与 `overflow` 显式传入 |
| `OmSurface` | 纯色容器，`color` 只接受语义别名；无 elevation 参数（不提供） |
| `OmIcon` | 24dp 画布 / 1.5dp 描边 / 方形端点；`tint` 只接受语义别名 |
| `OmDivider` | 1dp 或 2dp；支持 `insetStart` / `insetEnd`；不提供虚线（虚线属于装饰） |

### （八）L2 结构

| 组件 | 规格要点 |
|---|---|
| `OmScreen` | 应用页边距（16/24/32/48 随宽度类别）、内容最大宽度（正文 640dp）、可选顶部/底部槽位 |
| `OmSection` | 统一的区块节奏：上间距 `space.8`，区块标题（可选，`display.m` 或 `label`）与内容间距 `space.4` |
| `OmGrid` | 自研 `Layout`，支持 `span` 与 `offset`；栏宽运行时计算；可叠加调试网格线 |

---

## 四、P1 组件规格摘要

| 组件 | 关键规格 |
|---|---|
| `OmPivot` | 页签标题用 `title`（22sp/600），选中项下方 3dp accent 指示条，指示条宽度 = 文字宽度；支持横向滑动切页；标签过多时可横滑 |
| `OmBottomBar` | 3–5 项，图标 + `label`（全大写）；未选中用线框图标，选中用填充图标 + 顶部 3dp accent 条；高度由内容决定 |
| `OmCard` | 变体 `Flat`（`surfaceVariant` 底）/ `Outlined`（1dp `line`）/ `Ink`（反白）；0 圆角；内边距 `space.4`；不提供 elevation |
| `OmCheckbox` | 20dp 方形，1.5dp 描边；选中填充 accent + 白色折线勾；触摸区 48dp |
| `OmRadio` | 20dp 圆形，1.5dp 描边；选中为 accent 实心内点（唯一允许的圆之一） |
| `OmSwitch` | 轨道 44×24dp 直角，滑块 20dp 方块；切换 150ms 位移；选中轨道 accent |
| `OmSlider` | 轨道 2dp，激活段 accent；thumb 为 16dp 方块；无气泡（数值由外部文本展示） |
| `OmProgressBar` | 高 4dp 直角；确定态 accent 填充；不确定态为硬边扫描（非渐隐拖尾） |
| `OmTag` | 直角，高 28dp，水平 `space.3`；未选中 1dp 描边，选中 accent 填充；支持可删除（尾部 ×） |
| `OmAvatar` | 圆形（允许的圆）；尺寸 24/32/40/56dp；无图片时用首字母 + 中性色底 |
| `OmDialog` | 直角面板，宽度 ≤ 400dp，标题 `headline`，正文 `body.l`；按钮行右对齐；出现动效仅淡入 + 上移 24dp |
| `OmSheet` | 从底部滑出，直角，顶部 32×2dp 拖拽条；支持半展开与全展开两档；无圆角无阴影，靠 scrim 区分层级 |

---

## 五、P2 组件清单

`OmAccordion`、`OmSnackbar`、`OmBadge`、`OmProgressRing`、`OmSkeleton`、`OmEmptyState`、`OmSearchField`、`OmTooltip`、`OmRail`、`OmDrawer`、`OmPager`、`OmTopBar` 收缩态。

这些组件的视觉规则已由 00 决定（直角、单色相、白名单动效），实现时按同一套细则填写规格，不再单独评审视觉方向。

---

## 六、明确不提供的组件

| 不提供 | 理由 |
|---|---|
| 悬浮按钮（FAB） | 悬浮与阴影是同一套语言，与零阴影冲突；主要动作放 TopBar 或磁贴 |
| 日期/时间选择器、图表、地图 | 业务组件，不属于视觉库；如需可基于 P1 组件自行组合 |
| 富文本编辑器 | 工作量与视觉规范无关 |
| 表格 | 移动端密度不足；用列表 + 网格线替代 |
| 底部导航 6 项以上 | 超过 5 项应改 `OmDrawer` 或 `OmRail` |

---

## 七、全局验收标准

每个组件交付前必须满足：

1. **编译零错误零告警**（`grep -E "^[ew]: "` 为空）。
2. 尺寸全部是 4dp 的整数倍；无写死高度导致文字裁切。
3. 圆角为 0（或属于允许的语义圆）；无阴影、渐变、半透明色块。
4. 颜色只来自语义别名；只有一种 accent 色相。
5. 按压反馈为明度下沉（无 ripple）；焦点态可见。
6. 触摸目标 ≥ 48dp。
7. `fontScale = 2.0` 与 RTL 下均不裁切、不错位。
8. Paper 与 Ink 两套主题下对比度达标（由单元测试遍历校验）。
9. 在 demo 的 Gallery 中有可交互示例，且在 Showcase 中至少被真实页面使用一次。
