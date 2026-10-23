package io.openmetro.sample

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 网格叠加：4dp 基线（淡）+ 栏网格（明显）。
 *
 * 这是这套设计语言的自证工具 —— 任何偏离 4dp 的元素在叠加层下都会立刻显形。
 * 基线用中性色而非 accent：4dp 一条很密，用彩色会整屏泛色，反而看不清错位。
 */
@Composable
fun GridOverlay(modifier: Modifier = Modifier) {
    val grid = OmTheme.grid
    val colors = OmTheme.colors

    Canvas(modifier) {
        val unit = 4.dp.toPx()
        val baselineColor = colors.textPrimary.copy(alpha = 0.10f)
        val columnColor = colors.accent.copy(alpha = 0.30f)

        var y = unit
        while (y < size.height) {
            drawLine(
                color = baselineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f,
            )
            y += unit
        }

        val margin = grid.margin.toPx()
        val gutter = grid.gutter.toPx()
        val contentWidth = size.width - margin * 2
        if (contentWidth <= 0f) return@Canvas

        val columnWidth = (contentWidth - gutter * (grid.columns - 1)) / grid.columns
        if (columnWidth <= 0f) return@Canvas

        var x = margin
        repeat(grid.columns) {
            drawLine(
                color = columnColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
            )
            drawLine(
                color = columnColor,
                start = Offset(x + columnWidth, 0f),
                end = Offset(x + columnWidth, size.height),
                strokeWidth = 1.dp.toPx(),
            )
            x += columnWidth + gutter
        }
    }
}
