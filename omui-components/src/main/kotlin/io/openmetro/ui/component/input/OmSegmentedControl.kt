package io.openmetro.ui.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 分段控件：固定宽度的等分选项，与 [io.openmetro.ui.component.navigation.OmPivot] 的区别是
 * 后者按内容宽度排布且可横滑，这里强制等分、不滚动，适合 2–4 个互斥选项。
 *
 * 选中段整块填 accent，段与段之间 1dp 竖线分隔。外层 1dp 边框包住整组，
 * 所以「选中」是块面变化而不是描边变化。
 */
@Composable
fun OmSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val shapes = OmTheme.shapes
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(width = shapes.hairline, color = colors.lineStrong),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
                    .background(
                        when {
                            !enabled -> colors.surfaceVariant
                            selected -> colors.accent
                            else -> Color.Transparent
                        },
                    )
                    .drawBehind {
                        // 分隔线画在每段左侧，第一段不画
                        if (index > 0) {
                            drawLine(
                                color = colors.lineStrong,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = shapes.hairline.toPx(),
                            )
                        }
                    }
                    .omFocusRing()
                    .then(
                        if (enabled) {
                            Modifier.omClickable(role = Role.Tab) { onSelect(index) }
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = spacing.space2, vertical = spacing.space2),
                contentAlignment = Alignment.Center,
            ) {
                OmText(
                    text = label,
                    style = typography.label,
                    color = when {
                        !enabled -> colors.textDisabled
                        selected -> colors.onAccent
                        else -> colors.textPrimary
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
