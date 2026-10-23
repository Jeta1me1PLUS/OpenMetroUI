package io.openmetro.ui.component.base

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import io.openmetro.ui.core.theme.OmTheme

/**
 * 文本原语：只接受字阶 token 与语义色，不接受任意 [TextStyle] 构造。
 *
 * 用 `BasicText` 而非 Material 的 `Text` —— 后者会带入 M3 的默认颜色语义与
 * 内容颜色混入（contentColor 继承），与本库「颜色必须显式取自语义别名」的规则冲突。
 */
@Composable
fun OmText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = OmTheme.typography.bodyL,
    color: Color = OmTheme.colors.textPrimary,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color),
        maxLines = maxLines,
        overflow = overflow,
    )
}
