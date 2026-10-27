package io.openmetro.ui.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 横向页签。选中项的 3dp 指示条**只和文字同宽**，不铺满整格 ——
 * 铺满会退化成普通 Tab，失去 Pivot「标题即导航」的性格。
 *
 * 指示条在未选中时用透明占位，切换时只变颜色，不改变高度。
 *
 * @param scrollable 标签超过屏宽时允许横滑（3 个以内通常不需要）
 * @param pagerState 提供时与轮播双向联动：点击标签会 `animateScrollToPage`，
 *   手动滑动页面也会把选中态同步回来。传入后 `onSelect` 仍然由宿主维护，
 *   它只是把 pager 的当前页转述给你。
 */
@Composable
fun OmPivot(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    pagerState: PagerState? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    val scope = rememberCoroutineScope()

    // 单向：pager -> onSelect。点击走 scrollToPage，再由这里回流，不会形成循环。
    if (pagerState != null) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                if (page != selectedIndex) onSelect(page)
            }
        }
    }

    val rowModifier = if (scrollable) {
        Modifier.horizontalScroll(rememberScrollState())
    } else {
        Modifier
    }

    Row(
        modifier = modifier.then(rowModifier),
        horizontalArrangement = Arrangement.spacedBy(spacing.space6),
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .omFocusRing()
                    .omClickable(role = Role.Tab) {
                        if (pagerState != null) {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        } else {
                            onSelect(index)
                        }
                    }
                    .padding(top = spacing.space2),
            ) {
                OmText(
                    text = tab,
                    style = typography.title,
                    color = if (selected) colors.textPrimary else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(spacing.space2))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(OmTheme.shapes.accentBar)
                        .background(if (selected) colors.accent else Color.Transparent),
                )
            }
        }
    }
}
