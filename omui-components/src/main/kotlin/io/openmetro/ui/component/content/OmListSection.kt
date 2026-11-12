package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 分组列表：一个全大写小标题 + 一组行 + 组间留白。
 *
 * 设置页里每个分组都长这样，抽出来是为了让组间节奏一致 ——
 * 手写的话很容易有的地方 16dp、有的 24dp。
 *
 * 标题会自动转大写；中文不受影响。
 */
@Composable
fun OmListSection(
    title: String,
    modifier: Modifier = Modifier,
    /** 分组标题字阶，默认 `caption`。 */
    titleStyle: TextStyle? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        OmText(
            text = title.uppercase(),
            style = titleStyle ?: typography.caption,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = spacing.space2),
            maxLines = 1,
        )
        content()
        Spacer(Modifier.height(spacing.space6))
    }
}
