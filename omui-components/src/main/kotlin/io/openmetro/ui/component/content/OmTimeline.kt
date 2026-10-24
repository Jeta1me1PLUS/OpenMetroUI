package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

@Immutable
data class OmTimelineItem(
    val title: String,
    val time: String? = null,
    val description: String? = null,
    /** 当前节点用实心 accent 方块，其余是空心描边。 */
    val current: Boolean = false,
)

/**
 * 时间线：左侧 2dp 竖线 + 方形节点，右侧内容。
 *
 * 节点是方的而不是圆的 —— 唯一的圆形留给头像、状态点、单选和进度环。
 * 竖线在首尾两项只画到节点为止，不会在列表两端各露出一截。
 */
@Composable
fun OmTimeline(
    items: List<OmTimelineItem>,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            val isFirst = index == 0
            val isLast = index == items.lastIndex

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                Spacer(
                    modifier = Modifier
                        .width(24.dp)
                        .fillMaxHeight()
                        .drawBehind {
                            val stroke = 2.dp.toPx()
                            val nodeSize = 8.dp.toPx()
                            val centerX = size.width / 2f
                            val nodeCenterY = 12.dp.toPx()

                            drawLine(
                                color = colors.line,
                                start = Offset(centerX, if (isFirst) nodeCenterY else 0f),
                                end = Offset(centerX, if (isLast) nodeCenterY else size.height),
                                strokeWidth = stroke,
                            )

                            val topLeft = Offset(
                                x = centerX - nodeSize / 2f,
                                y = nodeCenterY - nodeSize / 2f,
                            )
                            val nodeStyle = if (item.current) {
                                androidx.compose.ui.graphics.drawscope.Fill
                            } else {
                                Stroke(width = 1.5.dp.toPx())
                            }
                            drawRect(
                                color = if (item.current) colors.accent else colors.lineStrong,
                                topLeft = topLeft,
                                size = Size(nodeSize, nodeSize),
                                style = nodeStyle,
                            )
                        },
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = spacing.space2,
                            bottom = if (isLast) spacing.space2 else spacing.space6,
                        ),
                ) {
                    OmText(item.title, style = typography.subtitle, maxLines = 2)
                    if (item.time != null) {
                        Spacer(Modifier.height(spacing.space1))
                        OmText(
                            text = item.time,
                            style = typography.caption,
                            color = colors.textSecondary,
                            maxLines = 1,
                        )
                    }
                    if (item.description != null) {
                        Spacer(Modifier.height(spacing.space1))
                        OmText(
                            text = item.description,
                            style = typography.bodyM,
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}
