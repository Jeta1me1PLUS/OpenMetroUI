package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * 不等高瀑布流：按「当前最短的列」依次放置子项。
 *
 * 与 `OmGrid` 的区别是列内高度独立 —— 磁贴高度一致时用 `OmGrid`，
 * 混排不同高度的块时才需要它。列宽固定，子项只决定高度。
 */
@Composable
fun OmStaggeredGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    gutter: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier.fillMaxWidth()) { measurables, constraints ->
        val gutterPx = gutter.roundToPx()
        val totalGutter = gutterPx * (columns - 1)
        val columnWidth = (constraints.maxWidth - totalGutter) / columns
        val heights = IntArray(columns)

        val placeables = measurables.map { measurable ->
            measurable.measure(
                Constraints(minWidth = columnWidth, maxWidth = columnWidth),
            )
        }

        val positions = placeables.map { placeable ->
            // 放进当前最矮的一列
            val column = heights.indices.minByOrNull { heights[it] } ?: 0
            val x = column * (columnWidth + gutterPx)
            val y = heights[column]
            heights[column] = y + placeable.height + gutterPx
            IntOffset(x, y)
        }

        // 最后一行的 gutter 不计入总高
        val totalHeight = (heights.maxOrNull() ?: 0).let { if (it > 0) it - gutterPx else 0 }

        layout(constraints.maxWidth, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(positions[index])
            }
        }
    }
}
