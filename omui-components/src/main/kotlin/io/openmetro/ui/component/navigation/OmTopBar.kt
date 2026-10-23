package io.openmetro.ui.component.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmIconButton
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 页头。两种形态共用一套结构，只换标题的字阶：
 * - [large] = true：可选全大写标签 + 48sp Light 大标题（页面的第一屏）
 * - [large] = false：22sp 标题（滚动后的紧凑态、次级页面）
 *
 * 高度**由内容决定**，不写死 64dp —— 大标题换行或字号放大时不能被裁掉。
 * 标题严格 flush-left，与页边距对齐。
 *
 * @param actions 右侧操作区，建议放 1–2 个图标按钮
 */
@Composable
fun OmTopBar(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    large: Boolean = true,
    onBack: (() -> Unit)? = null,
    backIcon: ImageVector = OmIcons.ArrowBack,
    backContentDescription: String = "返回",
    actions: @Composable RowScope.() -> Unit = {},
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            BackButton(
                icon = backIcon,
                contentDescription = backContentDescription,
                onClick = onBack,
            )
            Spacer(Modifier.width(spacing.space2))
        }

        Column(Modifier.weight(1f)) {
            if (label != null) {
                OmText(label, style = typography.label, color = colors.textSecondary)
                Spacer(Modifier.height(spacing.space1))
            }
            OmText(
                text = title,
                style = if (large) typography.displayL else typography.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.space1),
            content = actions,
        )
    }
}

@Composable
private fun BackButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    OmIconButton(
        icon = icon,
        contentDescription = contentDescription,
        onClick = onClick,
    )
}
