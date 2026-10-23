package io.openmetro.ui.component.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

@Immutable
data class OmBottomBarItem(
    val label: String,
    val icon: ImageVector,
)

/**
 * 底部导航：3–5 项，等分宽度。
 *
 * 选中态 = 顶部 3dp accent 条 + 图标与文字转为 accent / textPrimary。
 *
 * 规范里写的是「选中用填充图标」，但图标集目前只有线性版本，用「着色 + 顶部条」
 * 替代了造型切换。超过 5 项应改用抽屉或侧栏。
 *
 * 高度由内容决定，不写死 80dp；底部安全区由外层容器处理。
 */
@Composable
fun OmBottomBar(
    items: List<OmBottomBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .weight(1f)
                    .omFocusRing()
                    .omClickable(role = Role.Tab) { onSelect(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(OmTheme.shapes.accentBar)
                        .background(if (selected) colors.accent else Color.Transparent),
                )
                Column(
                    modifier = Modifier.padding(vertical = spacing.space2),
                    horizontalAlignment = Alignment.CenterHorizontally,
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
}
