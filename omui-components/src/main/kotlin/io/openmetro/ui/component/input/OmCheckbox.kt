package io.openmetro.ui.component.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 方形复选框：20dp 描边方框，选中后填充 accent 并画一条 2dp 白勾（方头、直角）。
 *
 * 勾是画出来的，不是图标 —— 它只有三个点，用 `Path` 比维护一个图标资源更直接，
 * 也能保证线宽与描边规则一致。
 */
@Composable
fun OmCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    val fill = if (enabled) colors.accent else colors.textDisabled
    val mark = colors.onAccent
    val outline = if (enabled) colors.lineStrong else colors.textDisabled

    Row(
        modifier = modifier
            .omFocusRing()
            .heightIn(min = 48.dp)
            .omClickable(enabled = enabled, role = Role.Checkbox) { onCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(20.dp)) {
            val strokeWidth = 1.5.dp.toPx()
            if (checked) {
                drawRect(color = fill)
                val path = Path().apply {
                    moveTo(size.width * 0.22f, size.height * 0.52f)
                    lineTo(size.width * 0.42f, size.height * 0.72f)
                    lineTo(size.width * 0.78f, size.height * 0.30f)
                }
                drawPath(
                    path = path,
                    color = mark,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Butt,
                        join = StrokeJoin.Miter,
                    ),
                )
            } else {
                drawRect(
                    color = outline,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(
                        width = size.width - strokeWidth,
                        height = size.height - strokeWidth,
                    ),
                    style = Stroke(width = strokeWidth),
                )
            }
        }

        if (label != null) {
            Spacer(Modifier.width(spacing.space3))
            OmText(
                text = label,
                style = OmTheme.typography.bodyL,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
            )
        }
    }
}
