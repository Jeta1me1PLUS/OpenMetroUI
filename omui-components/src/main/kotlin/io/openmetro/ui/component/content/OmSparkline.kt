package io.openmetro.ui.component.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 迷你趋势线。
 *
 * 只有一条 1.5dp 折线和末端一个 4dp 实心方块。没有填充、没有渐变、没有数据点圆点 ——
 * 这三个都是「图表库」的装饰手法，不属于这套语言。
 *
 * 值域自动缩放到 [min, max]，所以它只适合看形状，不适合读绝对值。
 */
@Composable
fun OmSparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp,
    color: Color? = null,
) {
    val lineColor = color ?: OmTheme.colors.accent

    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        if (values.size < 2) return@Canvas

        val stroke = 1.5.dp.toPx()
        val blockSize = 4.dp.toPx()
        // 内缩半个描边宽，避免首末点在画布边缘被裁掉一半
        val inset = stroke / 2f
        val usableWidth = size.width - stroke
        val usableHeight = size.height - stroke

        val minValue = values.min()
        val maxValue = values.max()
        val range = (maxValue - minValue).takeIf { it > 0f } ?: 1f
        val stepX = usableWidth / (values.size - 1)

        val path = Path()
        var lastY = 0f

        values.forEachIndexed { index, value ->
            val x = inset + index * stepX
            val y = inset + usableHeight - (value - minValue) / range * usableHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            lastY = y
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = stroke, cap = StrokeCap.Butt),
        )

        drawRect(
            color = lineColor,
            topLeft = Offset(
                x = size.width - inset - blockSize / 2f,
                y = lastY - blockSize / 2f,
            ),
            size = Size(blockSize, blockSize),
        )
    }
}
