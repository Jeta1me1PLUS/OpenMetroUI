package io.openmetro.ui.core.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import io.openmetro.ui.core.theme.OmTheme

/** 子项在网格中的位置。 */
@Immutable
data class OmGridSpanData(
    val span: Int = 1,
    val offset: Int = 0,
)

private class OmGridSpanModifier(
    private val span: Int,
    private val offset: Int,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = OmGridSpanData(span, offset)
}

/** 声明子项跨几栏、从第几栏开始（`offset = 0` 表示紧接上一个子项）。 */
fun Modifier.omGridSpan(span: Int = 1, offset: Int = 0): Modifier =
    this then OmGridSpanModifier(span.coerceAtLeast(1), offset.coerceAtLeast(0))

private class GridCell(
    val placeable: Placeable,
    val span: Int,
    val offset: Int,
) {
    var startColumn: Int = 0
}

/**
 * 栏网格容器：子项按 [OmGridSpanData] 跨栏排布，一行排满自动换行，行高取该行最高子项。
 *
 * 宽度必须是有界的（放在 `fillMaxWidth` 的 Column 里即可）。无界时退化为垂直堆叠，
 * 此时 span 不生效 —— 这是有意的降级，而不是让布局算出 0 宽。
 */
@Composable
fun OmGrid(
    modifier: Modifier = Modifier,
    columns: Int = OmTheme.grid.columns,
    gutter: Dp = OmTheme.grid.gutter,
    content: @Composable () -> Unit,
) {
    val gutterPx = with(LocalDensity.current) { gutter.roundToPx() }
    val safeColumns = columns.coerceAtLeast(1)

    Layout(modifier = modifier, content = content) { measurables, constraints ->
        if (!constraints.hasBoundedWidth) {
            val placeables = measurables.map { it.measure(Constraints()) }
            val width = placeables.maxOfOrNull { it.width } ?: 0
            val height = placeables.sumOf { it.height } +
                gutterPx * (placeables.size - 1).coerceAtLeast(0)
            return@Layout layout(width, height) {
                var y = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(0, y)
                    y += placeable.height + gutterPx
                }
            }
        }

        val width = constraints.maxWidth
        val columnWidth = ((width - gutterPx * (safeColumns - 1)) / safeColumns).coerceAtLeast(0)

        val cells = measurables.map { measurable ->
            val data = measurable.parentData as? OmGridSpanData ?: OmGridSpanData()
            val span = data.span.coerceIn(1, safeColumns)
            val itemWidth = columnWidth * span + gutterPx * (span - 1)
            GridCell(
                placeable = measurable.measure(
                    Constraints(
                        minWidth = itemWidth,
                        maxWidth = itemWidth,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight,
                    ),
                ),
                span = span,
                offset = data.offset,
            )
        }

        val rows = mutableListOf<MutableList<GridCell>>()
        var currentRow = mutableListOf<GridCell>()
        var cursor = 0

        fun flushRow() {
            if (currentRow.isNotEmpty()) rows += currentRow
            currentRow = mutableListOf()
            cursor = 0
        }

        cells.forEach { cell ->
            if (cell.offset > 0) {
                // 显式起点：能落在本行剩余空间就跳过去，否则另起一行
                if (cell.offset < cursor || cell.offset + cell.span > safeColumns) {
                    flushRow()
                } else {
                    cursor = cell.offset
                }
            }
            if (cursor + cell.span > safeColumns) flushRow()

            cell.startColumn = cursor
            currentRow += cell
            cursor += cell.span
            if (cursor >= safeColumns) flushRow()
        }
        if (currentRow.isNotEmpty()) rows += currentRow

        val rowHeights = rows.map { cellsInRow -> cellsInRow.maxOfOrNull { it.placeable.height } ?: 0 }
        val totalHeight = rowHeights.sum() + gutterPx * (rows.size - 1).coerceAtLeast(0)

        layout(width, totalHeight) {
            var y = 0
            rows.forEachIndexed { index, cellsInRow ->
                cellsInRow.forEach { cell ->
                    cell.placeable.placeRelative(
                        x = cell.startColumn * (columnWidth + gutterPx),
                        y = y,
                    )
                }
                y += rowHeights[index] + gutterPx
            }
        }
    }
}

/** 整行跨栏。 */
fun Modifier.omGridFullSpan(columns: Int): Modifier = omGridSpan(span = columns)
