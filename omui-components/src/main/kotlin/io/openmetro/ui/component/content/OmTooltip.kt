package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 提示气泡：反白底、直角、无箭头。
 *
 * 只负责外观 —— 什么时候显示、挂在哪里由调用方决定（通常配合 `Popup`）。
 * 不做箭头：箭头是装饰性元素，这套语言里没有。
 */
@Composable
fun OmTooltip(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    Box(
        modifier = modifier
            .background(colors.inverseSurface)
            .padding(horizontal = spacing.space3, vertical = spacing.space2),
    ) {
        OmText(
            text = text,
            style = OmTheme.typography.bodyM,
            color = colors.onInverse,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
