# 迁移指南

## v0.17.2

### 破坏性变更

`OmStrings` 新增 `clear` 字段。用 `OmStrings.Chinese` / `English` 预设的不受影响；自己构造实例需要补一项：

```kotlin
OmStrings(…, clear = "清除")
```

---

## v0.17.0

### 破坏性变更

**1. `OmTheme` 增加 `strings` 参数**

组件内置文案不再硬编码中文，改为从 `OmStrings` 读取。默认按系统语言选中英，无需改动即可。

需要固定语言时：

```kotlin
OmTheme(strings = OmStrings.English) { … }
```

**2. 占位符参数改为可空**

原先有中文默认值的三个参数改成 `String?`，不传时使用 `OmStrings` 里的文案：

| 组件 | 参数 | 原来 | 现在 |
|---|---|---|---|
| `OmDropdown` | `placeholder` | `"请选择"` | `null` → 按语言取 |
| `OmSearchField` | `placeholder` | `"搜索"` | `null` → 按语言取 |
| `OmChipInput` | `placeholder` | `"输入后回车"` | `null` → 按语言取 |
| `OmTopBar` | `backContentDescription` | `"返回"` | `null` → 按语言取 |

显式传值的行为不变，只是类型变成可空。

**3. `OmCheckbox` / `OmSwitch` / `OmRadio` 补了状态语义**

之前只有 `Role`，读屏说不出「已选中」。现在补上了 `toggleableState` / `selected`。这是修复，但如果你在测试里断言过语义树，会看到新节点属性。

### 新增

- `OmLazyList`：`LazyColumn` 版本，适合几百条以上的数据。**不能放进 `OmScreen(scrollable = true)`**。
- `omui-components` 增加 9 个组件行为测试（`src/androidTest`），跑在设备上。

---

## v0.16.0

### 变更

- `OmCard` 的 `Ink` 变体会自动反转内部文字与描边色。之前需要手动传 `colors.onInverse`，现在不传也是对的；**仍传旧值不会报错，但会覆盖自动配色**。
- `OmMenu` / `OmDropdown` 的弹出位置改为自动避让窗口边缘，不再需要手动给负 `offset`。

### 新增

- `OmSheet(dragHandleOnly = false)`：内容不滚动时整块面板可拖。
- `OmPivot(pagerState = …)`：与轮播双向联动。

---

## v0.15.0

### 变更

- `OmTag` 的触控区从 28dp 改为 48dp（视觉仍是 28dp）。**布局高度变了**：一行标签以前占 28dp，现在占 48dp，依赖固定行高的布局需要复查。
- 所有 `drawBehind` 装饰在 RTL 下会镜像，不再固定画在左侧。

---

## v0.14.6

### 变更

- `OmListItem` 自带 12dp 左内边距（为容纳选中竖条）。**列表文字会比同屏磁贴右偏 12dp**，之前依赖「列表与磁贴左对齐」的页面需要调整。

---

## v0.14.0

### 新增

- `OmTile` 的 `rotatingTitles`（Live Tile 内容轮播）。

---

## v0.13.0

### 新增

- `OmBanner`、`OmErrorState`、`OmOtpInput`、`OmSwipeToDismiss`、`OmStaggeredGrid`、`OmResponsivePane`、`OmSideSheet`。

### 变更

- `OmListSection` 的 `title` 从可选改为**必填**（`modifier` 需要作为第一个可选参数）。原来不传 `title` 的调用点需要补上。

---

## v0.11.0

### 破坏性变更

**`OmTile` 的 `color` 参数拆成 `tone` + 可空 `color`**

```kotlin
// 旧
OmTile("邮件", color = colors.tiles.accent)

// 新
OmTile("邮件", tone = OmTileTone.Accent)
```

`tone` 让磁贴跟随 `OmTileMode.Spectrum` 自动换色；`color` 保留给需要精确指定色值的场景。

**`versionCode` 规则变更**

从 `MINOR + PATCH` 改为 `MINOR * 100 + PATCH`。旧规则在 `0.11.0` 会算出 11，小于 `0.10.10` 的 20，无法覆盖安装。

---

## v0.10.9

### 破坏性变更

**groupId 变更**

```kotlin
// 旧
io.openmetro:omui-components

// 新
io.github.jeta1me1plus:omui-components
```

原因是 `io.openmetro` 需要域名验证，改用 GitHub 账号即可验证的 `io.github.*`。
