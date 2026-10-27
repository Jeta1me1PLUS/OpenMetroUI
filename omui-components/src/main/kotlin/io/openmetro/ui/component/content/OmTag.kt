package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.LocalOmStrings
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 标签 / 筛选片：视觉 28dp 高，**触控区 48dp 高**。
 *
 * 做法是整块占 48dp，背景与边框只画在垂直居中的 28dp 里。这样标签看起来仍然是
 * 一枚薄片，但整块标签和尾部的 × 都能按到 48dp 的下限 —— 不必为了触控把标签做成高块。
 *
 * 未选中是 1dp 描边，选中是 accent 实心。不做圆角胶囊 —— 胶囊是 Material 的
 * Chip 语言，和这套块面不兼容。
 *
 * @param onClick 提供时可点（筛选片），否则只作展示（标签）
 * @param onRemove 提供时尾部出现 ×，可单独删除
 */
@Composable
fun OmTag(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val shapes = OmTheme.shapes

    val surfaceColor = if (selected) colors.accent else Color.Transparent
    val contentColor = if (selected) colors.onAccent else colors.textPrimary
    val strokeColor = if (selected) null else colors.lineStrong

    val interaction = if (onClick != null) {
        Modifier.omFocusRing().omClickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            // 触控行高 48dp，视觉切片由 drawBehind 画在中间 28dp
            .heightIn(min = TouchHeight)
            .drawBehind {
                val sliceHeight = TagHeight.toPx()
                val top = (size.height - sliceHeight) / 2f
                val slice = Size(width = size.width, height = sliceHeight)
                val topLeft = Offset(0f, top)

                if (surfaceColor != Color.Transparent) {
                    drawRect(color = surfaceColor, topLeft = topLeft, size = slice)
                }
                if (strokeColor != null) {
                    drawRect(
                        color = strokeColor,
                        topLeft = Offset(shapes.hairline.toPx() / 2f, top + shapes.hairline.toPx() / 2f),
                        size = Size(
                            width = size.width - shapes.hairline.toPx(),
                            height = sliceHeight - shapes.hairline.toPx(),
                        ),
                        style = Stroke(width = shapes.hairline.toPx()),
                    )
                }
            }
            .then(interaction)
            .padding(start = spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmText(
            text = text,
            style = OmTheme.typography.caption,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (onRemove != null) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = TouchHeight, minHeight = TouchHeight)
                    .omClickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                OmIcon(
                    imageVector = OmIcons.Close,
                    contentDescription = LocalOmStrings.current.remove,
                    size = 14.dp,
                    tint = contentColor,
                )
            }
        } else {
            Box(modifier = Modifier.size(width = spacing.space3, height = 1.dp))
        }
    }
}

/** 视觉高度。 */
private val TagHeight = 28.dp

/** 触控高度，也是尾部 × 的最小见方。 */
private val TouchHeight = 48.dp
