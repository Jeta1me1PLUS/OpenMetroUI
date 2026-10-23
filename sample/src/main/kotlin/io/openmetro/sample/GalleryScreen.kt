package io.openmetro.sample

import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import io.openmetro.ui.component.content.OmStat
import io.openmetro.ui.component.content.OmSparkline
import io.openmetro.ui.component.content.OmTable
import io.openmetro.ui.component.content.OmTableColumn
import io.openmetro.ui.component.content.OmColumnAlign
import io.openmetro.ui.component.overlay.OmMenu
import io.openmetro.ui.component.overlay.OmMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmDivider
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmSection
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.button.OmButtonSize
import io.openmetro.ui.component.button.OmButtonVariant
import io.openmetro.ui.component.button.OmIconButton
import io.openmetro.ui.component.card.OmCard
import io.openmetro.ui.component.card.OmCardVariant
import io.openmetro.ui.component.content.OmAccordion
import io.openmetro.ui.component.content.OmAvatar
import io.openmetro.ui.component.content.OmBadge
import io.openmetro.ui.component.content.OmEmptyState
import io.openmetro.ui.component.content.OmList
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.component.content.OmTooltip
import io.openmetro.ui.component.content.OmPager
import io.openmetro.ui.component.content.OmPagerIndicator
import io.openmetro.ui.component.feedback.OmProgressBar
import io.openmetro.ui.component.feedback.OmProgressRing
import io.openmetro.ui.component.feedback.OmSkeleton
import io.openmetro.ui.component.feedback.OmSnackbar
import io.openmetro.ui.component.input.OmCheckbox
import io.openmetro.ui.component.input.OmDatePicker
import io.openmetro.ui.component.input.OmDate
import io.openmetro.ui.component.input.OmDropdown
import io.openmetro.ui.component.input.OmSegmentedControl
import io.openmetro.ui.component.input.OmStepperInput
import io.openmetro.ui.component.input.OmRadio
import io.openmetro.ui.component.input.OmSearchField
import io.openmetro.ui.component.input.OmSlider
import io.openmetro.ui.component.input.OmTextField
import io.openmetro.ui.component.input.OmTextFieldVariant
import io.openmetro.ui.component.navigation.OmBottomBarItem
import io.openmetro.ui.component.navigation.OmRail
import io.openmetro.ui.component.overlay.OmDialog
import io.openmetro.ui.component.overlay.OmDrawer
import io.openmetro.ui.component.overlay.OmSheet
import io.openmetro.ui.component.navigation.OmTopBar
import io.openmetro.ui.component.tile.OmTile
import io.openmetro.ui.component.tile.OmTileGrid
import io.openmetro.ui.core.layout.OmGrid
import io.openmetro.ui.core.layout.omGridSpan
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileTone
import io.openmetro.ui.icon.OmIcons

/**
 * 组件画廊的第一批：按钮全状态、图标集、跨栏网格。
 *
 * 每个变体都必须能在同一屏里对比 —— 只看一个「主按钮」看不出变体之间的差别。
 */
@Composable
fun GalleryScreen() {
    val spacing = OmTheme.spacing
    val colors = OmTheme.colors
    val typography = OmTheme.typography

    var agreed by remember { mutableStateOf(true) }
    var choice by remember { mutableIntStateOf(1) }
    var slider by remember { mutableFloatStateOf(0.4f) }
    var dialogVisible by remember { mutableStateOf(false) }
    var sheetVisible by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var snackbarVisible by remember { mutableStateOf(false) }
    var segment by remember { mutableIntStateOf(0) }
    var count by remember { mutableIntStateOf(1) }
    var dropdownIndex by remember { mutableIntStateOf(0) }
    var date by remember { mutableStateOf<OmDate?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var drawerVisible by remember { mutableStateOf(false) }
    var railIndex by remember { mutableIntStateOf(0) }

    val railItems = listOf(
        OmBottomBarItem(label = "NEWS", icon = OmIcons.Article),
        OmBottomBarItem(label = "SPEC", icon = OmIcons.Grid),
        OmBottomBarItem(label = "LAB", icon = OmIcons.Beaker),
    )

    OmScreen(scrollable = true) {
    Spacer(Modifier.height(spacing.space8))
    OmTopBar(title = "组件画廊", label = "GALLERY / 全状态")
    Spacer(Modifier.height(spacing.space2))

    OmSection(label = "BUTTON / 按钮") {
        SubTitle("五种变体")
        OmGrid(columns = 4, gutter = spacing.space2) {
            OmButton(
                text = "FILLED",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                fillWidth = true,
            )
            OmButton(
                text = "FILLED INK",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                variant = OmButtonVariant.FilledInk,
                fillWidth = true,
            )
            OmButton(
                text = "OUTLINED",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                variant = OmButtonVariant.Outlined,
                fillWidth = true,
            )
            OmButton(
                text = "TEXT",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                variant = OmButtonVariant.Text,
                fillWidth = true,
            )
            OmButton(
                text = "DELETE FOREVER",
                onClick = {},
                modifier = Modifier.omGridSpan(4),
                variant = OmButtonVariant.Destructive,
                fillWidth = true,
            )
        }

        SubTitle("尺寸")
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            OmButton("SMALL", {}, size = OmButtonSize.Small)
            OmButton("MEDIUM", {}, size = OmButtonSize.Medium)
            OmButton("LARGE", {}, size = OmButtonSize.Large)
        }

        SubTitle("状态与图标")
        OmGrid(columns = 4, gutter = spacing.space2) {
            OmButton(
                text = "DISABLED",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                enabled = false,
                fillWidth = true,
            )
            OmButton(
                text = "WITH ICON",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                leadingIcon = OmIcons.Add,
                fillWidth = true,
            )
            OmButton(
                text = "TRAILING",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                variant = OmButtonVariant.Outlined,
                trailingIcon = OmIcons.ChevronEnd,
                fillWidth = true,
            )
            OmButton(
                text = "OUTLINE OFF",
                onClick = {},
                modifier = Modifier.omGridSpan(2),
                variant = OmButtonVariant.Outlined,
                enabled = false,
                fillWidth = true,
            )
        }

        SubTitle("图标按钮")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmIconButton(OmIcons.Search, "搜索", {})
            OmIconButton(OmIcons.Add, "新增", {})
            OmIconButton(OmIcons.MoreVert, "更多", {})
            OmIconButton(OmIcons.Close, "删除", {}, tint = colors.danger)
            OmIconButton(OmIcons.Check, "禁用", {}, enabled = false)
        }
        Spacer(Modifier.height(spacing.space2))
        OmText(
            text = "48dp 触摸区，图标 24dp；点击无底色变化，只有明度下沉",
            style = typography.caption,
            color = colors.textSecondary,
        )
    }

    OmSection(label = "INPUT / 输入") {
        SubTitle("下划线 · 填充 · 错误 · 禁用")
        var email by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        OmTextField(
            value = email,
            onValueChange = { email = it },
            label = "EMAIL / 邮箱",
            placeholder = "name@example.com",
            helperText = "密码重置链接会发送到这里",
        )
        Spacer(Modifier.height(spacing.space6))
        OmTextField(
            value = note,
            onValueChange = { note = it },
            label = "NOTE / 备注",
            placeholder = "写点什么",
            variant = OmTextFieldVariant.Filled,
            leadingIcon = OmIcons.Menu,
            singleLine = false,
            minLines = 3,
        )
        Spacer(Modifier.height(spacing.space6))
        OmTextField(
            value = "not-an-email",
            onValueChange = {},
            label = "ERROR / 错误",
            errorText = "格式不正确",
        )
        Spacer(Modifier.height(spacing.space6))
        OmTextField(
            value = "",
            onValueChange = {},
            label = "DISABLED / 禁用",
            placeholder = "不可编辑",
            enabled = false,
        )

        SubTitle("Search")
        OmSearchField(
            value = search,
            onValueChange = { search = it },
        )
    }

    OmSection(label = "TOP BAR / 页头") {
        SubTitle("大标题")
        OmTopBar(
            title = "新闻流",
            label = "SECTION / 今日",
            onBack = {},
        )
        SubTitle("紧凑 + 操作区")
        OmTopBar(
            title = "设置",
            large = false,
            onBack = {},
            actions = {
                OmButton(
                    text = "SAVE",
                    onClick = {},
                    variant = OmButtonVariant.Text,
                    size = OmButtonSize.Small,
                )
            },
        )
    }

    OmSection(label = "ICON / 图标") {
        SubTitle("24dp · 1.5dp 描边 · 方头端点")
        val icons: List<ImageVector> = listOf(
            OmIcons.ArrowBack,
            OmIcons.ArrowForward,
            OmIcons.Close,
            OmIcons.Check,
            OmIcons.Add,
            OmIcons.ChevronEnd,
            OmIcons.Menu,
            OmIcons.Search,
            OmIcons.MoreVert,
        )
        icons.chunked(5).forEach { rowIcons ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.space6),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                rowIcons.forEach { vector ->
                    OmIcon(imageVector = vector, contentDescription = null, tint = colors.textPrimary)
                }
            }
            Spacer(Modifier.height(spacing.space4))
        }
    }

    OmSection(label = "GRID / 栏网格") {
        SubTitle("span 1 / 2 / 4 混排")
        OmGrid(columns = 4, gutter = spacing.space2) {
            GridBlock("1", Modifier.omGridSpan(1))
            GridBlock("1", Modifier.omGridSpan(1))
            GridBlock("2", Modifier.omGridSpan(2))
            GridBlock("4", Modifier.omGridSpan(4))
            GridBlock("1", Modifier.omGridSpan(1))
            GridBlock("1", Modifier.omGridSpan(1))
            GridBlock("2", Modifier.omGridSpan(2, offset = 2))
            GridBlock("3", Modifier.omGridSpan(3))
            GridBlock("1", Modifier.omGridSpan(1))
        }
    }

    OmSection(label = "LIST / 列表") {
        SubTitle("两级文本 + 尾随图标")
        val rows = listOf(
            "收件箱" to "3 封未读",
            "已发送" to "今天 14:20",
            "草稿" to "无",
        )
        OmList(items = rows) { (title, subtitle) ->
            OmListItem(
                title = title,
                subtitle = subtitle,
                trailing = {
                    OmIcon(
                        imageVector = OmIcons.ChevronEnd,
                        contentDescription = null,
                        size = 20.dp,
                        tint = colors.textSecondary,
                    )
                },
                onClick = {},
            )
        }
        Spacer(Modifier.height(spacing.space4))
        OmListItem(
            title = "选中项",
            subtitle = "整行 accentQuiet + 3dp accent 竖条",
            selected = true,
            onClick = {},
        )
        Spacer(Modifier.height(spacing.space2))
        OmListItem(title = "禁用项", subtitle = "不可点，文字降为 38%", enabled = false)
    }

    OmSection(label = "TILE / 磁贴") {
        SubTitle("点击翻转 · 标题字号随宽度自适应")
        OmTileGrid(columns = 4, gutter = spacing.space2) {
            OmTile(
                title = "邮件",
                modifier = Modifier.omGridSpan(2).aspectRatio(1f),
                label = "3 封新",
                tone = OmTileTone.Accent,
                backTitle = "2 封未读邮件",
            )
            OmTile(
                title = "日历",
                modifier = Modifier.omGridSpan(2).aspectRatio(1f),
                tone = OmTileTone.Paper,
                backTitle = "今天 2 个日程",
            )
            OmTile(
                title = "音乐",
                modifier = Modifier.omGridSpan(4).aspectRatio(2f),
                label = "NOW PLAYING",
                tone = OmTileTone.Ink,
            )
        }

        SubTitle("Spectrum 四色")
        OmTileGrid(columns = 4, gutter = spacing.space2) {
            colors.tiles.spectrum.take(4).forEachIndexed { index, tile ->
                OmTile(
                    title = "0${index + 1}",
                    modifier = Modifier.omGridSpan(1).aspectRatio(1f),
                    color = tile,
                )
            }
        }
    }

    OmSection(label = "CARD / 卡片") {
        SubTitle("三种变体")
        OmCard(variant = OmCardVariant.Flat) {
            OmText("Flat", style = typography.title)
            Spacer(Modifier.height(spacing.space2))
            OmText(
                "surfaceVariant 底色，用于承载一段内容。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }
        Spacer(Modifier.height(spacing.space3))
        OmCard(variant = OmCardVariant.Outlined) {
            OmText("Outlined", style = typography.title)
            Spacer(Modifier.height(spacing.space2))
            OmText(
                "透明底 + 1dp 描边，适合放在已有底色的区域。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }
        Spacer(Modifier.height(spacing.space3))
        OmCard(variant = OmCardVariant.Ink) {
            OmText("Ink", style = typography.title, color = colors.onInverse)
            Spacer(Modifier.height(spacing.space2))
            OmText(
                "反白块。内部文字要显式用 onInverse，组件不替你换色。",
                style = typography.bodyM,
                color = colors.onInverse,
            )
        }
    }

    OmSection(label = "FORM / 表单控件") {
        SubTitle("Checkbox / Radio")
        OmCheckbox(checked = agreed, onCheckedChange = { agreed = it }, label = "接收每周摘要")
        OmCheckbox(checked = false, onCheckedChange = {}, label = "禁用项", enabled = false)
        Spacer(Modifier.height(spacing.space2))
        OmRadio(selected = choice == 0, onClick = { choice = 0 }, label = "Paper")
        OmRadio(selected = choice == 1, onClick = { choice = 1 }, label = "Ink")

        SubTitle("Slider")
        Row(verticalAlignment = Alignment.CenterVertically) {
            OmSlider(
                value = slider,
                onValueChange = { slider = it },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(spacing.space4))
            OmText("${(slider * 100).toInt()}", style = typography.title)
        }

        SubTitle("Progress")
        OmProgressBar(progress = slider)
        Spacer(Modifier.height(spacing.space3))
        OmProgressBar(progress = null)

        Spacer(Modifier.height(spacing.space4))
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmProgressRing(progress = slider)
            OmProgressRing(progress = null)
            OmProgressRing(progress = 1f, size = 56.dp, strokeWidth = 6.dp)
        }

        SubTitle("Segmented / Stepper")
        OmSegmentedControl(
            options = listOf("日", "周", "月"),
            selectedIndex = segment,
            onSelect = { segment = it },
        )
        Spacer(Modifier.height(spacing.space3))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OmText("数量", style = typography.bodyM, modifier = Modifier.weight(1f))
            OmStepperInput(
                value = count,
                onValueChange = { count = it },
                min = 1,
                max = 9,
                modifier = Modifier.width(160.dp),
            )
        }

        SubTitle("Dropdown")
        OmDropdown(
            options = listOf("Paper", "Ink", "Spectrum"),
            selectedIndex = dropdownIndex,
            onSelect = { dropdownIndex = it },
        )

        SubTitle("Date")
        OmDatePicker(selected = date, onSelect = { date = it })
    }

    OmSection(label = "DATA / 数据") {
        SubTitle("Stat")
        OmGrid(columns = 2, gutter = spacing.space4) {
            OmStat(
                value = "1,284",
                label = "MONTHLY ACTIVE",
                delta = "+12%",
                modifier = Modifier.omGridSpan(1),
            )
            OmStat(
                value = "98.2%",
                label = "UPTIME",
                delta = "-0.3%",
                deltaPositive = false,
                modifier = Modifier.omGridSpan(1),
            )
        }

        SubTitle("Sparkline")
        OmSparkline(values = listOf(3f, 5f, 4f, 8f, 6f, 9f, 7f, 12f))

        SubTitle("Table")
        OmTable(
            columns = listOf(
                OmTableColumn("名称", weight = 2f),
                OmTableColumn("数量", weight = 1f, align = OmColumnAlign.End),
                OmTableColumn("状态", weight = 1f, align = OmColumnAlign.End),
            ),
            rows = listOf(
                listOf("收件箱", "128", "正常"),
                listOf("草稿", "12", "同步中"),
                listOf("归档", "2,041", "正常"),
            ),
            onRowClick = {},
        )

        SubTitle("Menu")
        Box {
            OmButton(
                text = "打开菜单",
                onClick = { menuOpen = true },
                variant = OmButtonVariant.Outlined,
            )
            OmMenu(
                expanded = menuOpen,
                onDismissRequest = { menuOpen = false },
                offset = IntOffset(0, with(density) { 48.dp.roundToPx() }),
            ) {
                OmMenuItem("重命名", onClick = { menuOpen = false })
                OmMenuItem("复制链接", onClick = { menuOpen = false })
                OmMenuItem("删除", onClick = { menuOpen = false }, destructive = true)
            }
        }
    }

    OmSection(label = "TAG / AVATAR") {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            OmTag("全部", selected = true, onClick = {})
            OmTag("科技", onClick = {})
            OmTag("设计")
        }
        Spacer(Modifier.height(spacing.space3))
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            OmTag("可删除", onRemove = {})
        }

        SubTitle("头像")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmAvatar("Ava", size = 56.dp)
            OmAvatar("Ada", size = 40.dp)
            OmAvatar("Linus", size = 32.dp)
            OmAvatar("Z", size = 24.dp)
        }
    }

    OmSection(label = "STATUS / 状态") {
        SubTitle("Badge")
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmBadge(text = "3")
            OmBadge(text = "99+")
            OmBadge()
            Spacer(Modifier.width(spacing.space2))
            OmButton(
                text = if (snackbarVisible) "HIDE" else "SNACKBAR",
                onClick = { snackbarVisible = !snackbarVisible },
                size = OmButtonSize.Small,
                variant = OmButtonVariant.Outlined,
            )
        }

        SubTitle("Skeleton")
        OmSkeleton(Modifier.fillMaxWidth().height(20.dp))
        Spacer(Modifier.height(spacing.space2))
        OmSkeleton(Modifier.fillMaxWidth(0.7f).height(20.dp))
        Spacer(Modifier.height(spacing.space2))
        OmSkeleton(Modifier.fillMaxWidth().height(48.dp))

        SubTitle("Tooltip")
        OmTooltip("动作只对当前选中项生效")

        SubTitle("Empty state")
        OmEmptyState(
            title = "还没有内容",
            description = "网格线是这套语言里唯一允许的插图。",
        ) {
            OmButton("新建", {})
        }

        Spacer(Modifier.height(spacing.space4))
        OmSnackbar(
            visible = snackbarVisible,
            message = "已保存到本地",
            modifier = Modifier.fillMaxWidth(),
            actionLabel = "撤销",
            onAction = { snackbarVisible = false },
        )
    }

    OmSection(label = "ACCORDION / 折叠") {
        OmAccordion(title = "什么是 4dp 网格", subtitle = "基础规则") {
            OmText(
                text = "所有尺寸都是 4dp 的整数倍，文字行高落在基线上。这条规则约束了间距、组件高度与栅格，是整套界面对齐的前提。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }
        OmDivider()
        OmAccordion(title = "为什么不用圆角", subtitle = "造型规则") {
            OmText(
                text = "圆角会削弱块面的几何稳定性。这套语言靠色块边界与网格线建立结构，圆角会让边界变得模糊。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }
    }

    OmSection(label = "RAIL / 侧边导航") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(colors.background),
        ) {
            OmRail(
                items = railItems,
                selectedIndex = railIndex,
                onSelect = { railIndex = it },
                modifier = Modifier.align(Alignment.CenterStart),
            )
            OmText(
                text = "≥840dp 时\n用侧栏替代底栏",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = spacing.space4),
                style = typography.caption,
                color = colors.textSecondary,
            )
        }
    }

    OmSection(label = "PAGER / 轮播") {
        val pagerState = rememberPagerState(pageCount = { 3 })
        OmPager(
            pageCount = 3,
            state = pagerState,
            modifier = Modifier.height(160.dp),
        ) { page ->
            val tile = colors.tiles.spectrum[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(tile.surface),
                contentAlignment = Alignment.Center,
            ) {
                OmText(
                    text = "PAGE ${page + 1}",
                    style = typography.displayM,
                    color = tile.onSurface,
                )
            }
        }
        Spacer(Modifier.height(spacing.space3))
        OmPagerIndicator(pageCount = 3, currentPage = pagerState.currentPage)
    }

    OmSection(label = "OVERLAY / 浮层") {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            OmButton("DIALOG", { dialogVisible = true })
            OmButton(
                text = "SHEET",
                onClick = { sheetVisible = true },
                variant = OmButtonVariant.Outlined,
            )
            OmButton(
                text = "DRAWER",
                onClick = { drawerVisible = true },
                variant = OmButtonVariant.Outlined,
            )
        }
    }

    if (drawerVisible) {
        OmDrawer(visible = drawerVisible, onDismiss = { drawerVisible = false }) {
            OmText("导航", style = typography.displayM)
            Spacer(Modifier.height(spacing.space6))
            railItems.forEach { item ->
                OmListItem(
                    title = item.label,
                    onClick = { drawerVisible = false },
                    trailing = {
                        OmIcon(
                            imageVector = item.icon,
                            contentDescription = null,
                            size = 20.dp,
                            tint = colors.textSecondary,
                        )
                    },
                )
            }
        }
    }

    if (dialogVisible) {
        OmDialog(
            visible = dialogVisible,
            onDismiss = { dialogVisible = false },
            title = "删除这条记录？",
            actions = {
                OmButton("取消", { dialogVisible = false }, variant = OmButtonVariant.Text)
                Spacer(Modifier.width(spacing.space2))
                OmButton("删除", { dialogVisible = false }, variant = OmButtonVariant.Destructive)
            },
        ) {
            OmText(
                "删除后无法恢复。这条记录包含 12 个附件。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }
    }

    if (sheetVisible) {
        OmSheet(visible = sheetVisible, onDismiss = { sheetVisible = false }) {
            OmText("面板", style = typography.displayM)
            Spacer(Modifier.height(spacing.space4))
            OmText(
                "向上拖拽展开到全屏，向下拖拽关闭。",
                style = typography.bodyM,
                color = colors.textSecondary,
            )
            Spacer(Modifier.height(spacing.space6))
            OmButton("关闭", { sheetVisible = false }, fillWidth = true)
        }
    }
    }
}

@Composable
private fun ColumnScope.SubTitle(text: String) {
    Spacer(Modifier.height(OmTheme.spacing.space6))
    OmText(text, style = OmTheme.typography.headline)
    Spacer(Modifier.height(OmTheme.spacing.space3))
}

@Composable
private fun GridBlock(
    text: String,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
) {
    val colors = OmTheme.colors
    Box(
        modifier = modifier
            .height(height)
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        OmText(text, style = OmTheme.typography.label, color = colors.textSecondary)
    }
}
