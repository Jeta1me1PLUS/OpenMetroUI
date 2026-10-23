package io.openmetro.ui.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 侧边导航条：图标在上、标签在下，竖向排列。
 *
 * 用于 ≥840dp 的大屏（平板、折叠屏展开态）。选中态是 start 侧 3dp accent 竖条
 * + 图标着色 —— 与底部导航同一套选中语言，只是方向从「顶部条」换成「侧边条」。
 *
 * 宽度固定 80dp；高度撑满父容器，因此**父容器必须给定高度**。
 */
@Composable
fun OmRail(
    items: List<OmBottomBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Column(
        modifier = modifier
            .width(RailWidth)
            .fillMaxHeight(),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(RailItemHeight)
                    .drawBehind {
                        if (selected) {
                            drawRect(
                                color = colors.accent,
                                size = Size(3.dp.toPx(), size.height),
                            )
                        }
                    }
                    .omFocusRing()
                    .omClickable(role = Role.Tab) { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OmIcon(
                    imageVector = item.icon,
                    contentDescription = null,
                    size = 24.dp,
                    tint = if (selected) colors.accent else colors.textSecondary,
                )
                Spacer(Modifier.height(spacing.space1))
                OmText(
                    text = item.label,
                    style = typography.label,
                    color = if (selected) colors.textPrimary else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val RailWidth = 80.dp
private val RailItemHeight = 72.dp
