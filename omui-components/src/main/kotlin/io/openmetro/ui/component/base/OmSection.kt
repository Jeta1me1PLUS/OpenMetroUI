package io.openmetro.ui.component.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.openmetro.ui.core.theme.OmTheme

/**
 * 区块：统一「上间距 → 可选标签 → 可选标题 → 内容」的节奏。
 *
 * 区块间距固定 `space8`，不开放覆盖 —— 纵向节奏靠固定值建立，一旦各处可调就会散。
 *
 * @param label 11sp 全大写标签，用于分类提示
 * @param title 区块大标题，36sp Light
 */
@Composable
fun OmSection(
    modifier: Modifier = Modifier,
    label: String? = null,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = OmTheme.spacing
    val colors = OmTheme.colors
    val typography = OmTheme.typography

    Column(modifier = modifier) {
        Spacer(Modifier.height(spacing.space8))
        if (label != null) {
            OmText(label, style = typography.label, color = colors.textSecondary)
            Spacer(Modifier.height(spacing.space2))
        }
        if (title != null) {
            OmText(title, style = typography.displayM)
            Spacer(Modifier.height(spacing.space3))
        }
        content()
    }
}
