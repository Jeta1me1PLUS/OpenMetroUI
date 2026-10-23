package io.openmetro.ui.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 线性进度条，高 4dp，直角。
 *
 * @param progress 0..1 为确定态；传 null 为不确定态
 *
 * 不确定态是**硬边扫描**：一块固定宽度的实心方块循环划过，不是 Material 那种
 * 两端渐隐的拖尾 —— 渐变在这套语言里不存在。
 */
@Composable
fun OmProgressBar(
    progress: Float?,
    modifier: Modifier = Modifier,
    height: Dp = 4.dp,
) {
    val colors = OmTheme.colors

    val transition = rememberInfiniteTransition(label = "omProgress")
    val sweep by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
        ),
        label = "omProgressSweep",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        drawRect(color = colors.line)

        if (progress != null) {
            val clamped = progress.coerceIn(0f, 1f)
            if (clamped > 0f) {
                drawRect(
                    color = colors.accent,
                    size = Size(width = size.width * clamped, height = size.height),
                )
            }
        } else {
            val barWidth = size.width * 0.3f
            val startX = sweep * (size.width + barWidth) - barWidth
            drawRect(
                color = colors.accent,
                topLeft = Offset(startX, 0f),
                size = Size(width = barWidth, height = size.height),
            )
        }
    }
}
