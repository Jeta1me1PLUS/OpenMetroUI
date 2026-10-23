package io.openmetro.ui.component.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 错误态：与 [OmEmptyState] 同一套骨架，图形换成「断开的取景框」。
 *
 * 十字不画穿，中间留 16dp 缺口，缺口里放一个 danger 色实心方块 ——
 * 断开表示中断，方块是断点本身。仍然是线条，不是插画。
 */
@Composable
fun OmErrorState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        ErrorGlyph()
        Spacer(Modifier.height(spacing.space6))
        OmText(title, style = typography.displayM)

        if (description != null) {
            Spacer(Modifier.height(spacing.space2))
            OmText(
                text = description,
                style = typography.bodyM,
                color = colors.textSecondary,
            )
        }

        if (action != null) {
            Spacer(Modifier.height(spacing.space6))
            action()
        }
    }
}

@Composable
private fun ErrorGlyph(glyphSize: Dp = 96.dp) {
    val colors = OmTheme.colors

    Canvas(Modifier.size(glyphSize)) {
        val stroke = 1.dp.toPx()
        val w = size.width
        val h = size.height
        val gap = 8.dp.toPx()

        drawRect(color = colors.line, style = Stroke(width = stroke))

        // 横线断开在中间
        drawLine(colors.line, Offset(0f, h / 2), Offset(w / 2 - gap, h / 2), stroke)
        drawLine(colors.line, Offset(w / 2 + gap, h / 2), Offset(w, h / 2), stroke)

        // 竖线同样断开
        drawLine(colors.line, Offset(w / 2, 0f), Offset(w / 2, h / 2 - gap), stroke)
        drawLine(colors.line, Offset(w / 2, h / 2 + gap), Offset(w / 2, h), stroke)

        // 断点本身：一个 danger 色实心方块
        val block = 8.dp.toPx()
        drawRect(
            color = colors.danger,
            topLeft = Offset(w / 2 - block / 2, h / 2 - block / 2),
            size = Size(block, block),
        )
    }
}
