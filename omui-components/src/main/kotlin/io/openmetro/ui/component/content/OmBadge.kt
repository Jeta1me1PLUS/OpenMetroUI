package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 角标。直角方块，不是圆形气泡。
 *
 * @param text 传入计数或短文本；传 null 时只显示一个 8dp 的实心方块（未读点）
 */
@Composable
fun OmBadge(
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    val colors = OmTheme.colors

    if (text == null) {
        Box(modifier = modifier.size(8.dp).background(colors.accent))
        return
    }

    Box(
        modifier = modifier
            .heightIn(min = 18.dp)
            .background(colors.accent)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        OmText(
            text = text,
            style = OmTheme.typography.caption,
            color = colors.onAccent,
            maxLines = 1,
        )
    }
}
