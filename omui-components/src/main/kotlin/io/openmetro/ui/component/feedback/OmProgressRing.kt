package io.openmetro.ui.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 环形进度。圆形在这里是语义需要（进度环天然是圆的），不是装饰。
 *
 * 线端用 **Butt**，不用圆头 —— 圆头会让环看起来像 Material 的转圈。
 * 不确定态是一段 90° 的弧在旋转，不是两端渐隐的拖尾。
 *
 * @param progress 0..1 为确定态，传 null 为不确定态
 */
@Composable
fun OmProgressRing(
    progress: Float?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
) {
    val colors = OmTheme.colors

    val transition = rememberInfiniteTransition(label = "omProgressRing")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
        ),
        label = "omProgressRingSpin",
    )

    Canvas(modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val diameter = this.size.minDimension - stroke
        val topLeft = Offset(stroke / 2f, stroke / 2f)
        val arcSize = Size(diameter, diameter)

        drawArc(
            color = colors.line,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke),
        )

        if (progress != null) {
            val clamped = progress.coerceIn(0f, 1f)
            if (clamped > 0f) {
                drawArc(
                    color = colors.accent,
                    startAngle = -90f,
                    sweepAngle = 360f * clamped,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
            }
        } else {
            rotate(degrees = spin) {
                drawArc(
                    color = colors.accent,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
            }
        }
    }
}
