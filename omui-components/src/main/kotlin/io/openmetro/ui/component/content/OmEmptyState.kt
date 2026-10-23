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
 * 空状态：巨字标题 + 网格线图形。
 *
 * 图形不是插画，是一个「取景框」：外框、十字、中心方块都用 1–2dp 线画出来，
 * 与整个界面的线条语言一致。插图会引入第二种造型语言，这里刻意不用。
 */
@Composable
fun OmEmptyState(
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
        EmptyGlyph()
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
private fun EmptyGlyph(glyphSize: Dp = 96.dp) {
    val colors = OmTheme.colors

    Canvas(Modifier.size(glyphSize)) {
        val stroke = 1.dp.toPx()
        val w = size.width
        val h = size.height

        drawRect(color = colors.line, style = Stroke(width = stroke))
        drawLine(colors.line, Offset(w / 2, 0f), Offset(w / 2, h), strokeWidth = stroke)
        drawLine(colors.line, Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = stroke)
        drawRect(
            color = colors.accent,
            topLeft = Offset(w * 0.25f, h * 0.25f),
            size = Size(w * 0.5f, h * 0.5f),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
