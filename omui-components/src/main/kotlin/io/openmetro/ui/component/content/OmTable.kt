package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.OmTheme

/** 列的水平对齐。用自己的枚举而不是 `TextAlign`，因为 `OmText` 不接受对齐参数。 */
enum class OmColumnAlign { Start, Center, End }

@Immutable
data class OmTableColumn(
    val title: String,
    val weight: Float = 1f,
    val align: OmColumnAlign = OmColumnAlign.Start,
)

/**
 * 数据表格。
 *
 * 表头下 1dp 结构线，行间 hairline，最后一行不画。列宽按 `weight` 分配，
 * 数字列用 `OmColumnAlign.End` 右对齐 —— 右对齐让位数不同的数字也能按位比较。
 */
@Composable
fun OmTable(
    columns: List<OmTableColumn>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier,
    onRowClick: ((Int) -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    // drawBehind 的 lambda 不是 @Composable，描边宽度必须先取出来
    val hairline = OmTheme.shapes.hairline

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val stroke = 1.dp.toPx()
                    drawLine(
                        color = colors.lineStrong,
                        start = Offset(0f, size.height - stroke / 2f),
                        end = Offset(size.width, size.height - stroke / 2f),
                        strokeWidth = stroke,
                    )
                }
                .padding(vertical = spacing.space2),
        ) {
            columns.forEach { column ->
                Box(
                    modifier = Modifier.weight(column.weight),
                    contentAlignment = column.align.toAlignment(),
                ) {
                    OmText(
                        text = column.title,
                        style = typography.caption,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        rows.forEachIndexed { rowIndex, row ->
            val isLast = rowIndex == rows.lastIndex

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isLast) {
                            Modifier
                        } else {
                            Modifier.drawBehind {
                                val stroke = hairline.toPx()
                                drawLine(
                                    color = colors.line,
                                    start = Offset(0f, size.height - stroke / 2f),
                                    end = Offset(size.width, size.height - stroke / 2f),
                                    strokeWidth = stroke,
                                )
                            }
                        },
                    )
                    .then(
                        if (onRowClick != null) {
                            Modifier.omClickable { onRowClick(rowIndex) }
                        } else {
                            Modifier
                        },
                    )
                    .heightIn(min = 48.dp)
                    .padding(vertical = spacing.space2),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                columns.forEachIndexed { columnIndex, column ->
                    Box(
                        modifier = Modifier.weight(column.weight),
                        contentAlignment = column.align.toAlignment(),
                    ) {
                        OmText(
                            text = row.getOrElse(columnIndex) { "" },
                            style = typography.bodyM,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

private fun OmColumnAlign.toAlignment(): Alignment = when (this) {
    OmColumnAlign.Start -> Alignment.CenterStart
    OmColumnAlign.Center -> Alignment.Center
    OmColumnAlign.End -> Alignment.CenterEnd
}
