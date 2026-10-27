package io.openmetro.ui.component.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.LocalOmStrings
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 数值增减器：两侧 48dp 方形按钮夹一个数字。
 *
 * 到边界时对应按钮变灰且不可点，而不是回绕。数字用 `title` 字阶（22sp），
 * 因为它是这一行的主信息，不是辅助标签。
 */
@Composable
fun OmStepperInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val shapes = OmTheme.shapes
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    val strings = LocalOmStrings.current
    val canDecrease = enabled && value - step >= min
    val canIncrease = enabled && value + step <= max

    Row(
        modifier = modifier
            .heightIn(min = 48.dp)
            .border(width = shapes.hairline, color = colors.lineStrong),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepperButton(
            icon = OmIcons.Minus,
            contentDescription = strings.decrease,
            enabled = canDecrease,
            onClick = { onValueChange(value - step) },
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .drawBehind {
                    val stroke = shapes.hairline.toPx()
                    drawLine(colors.lineStrong, Offset(0f, 0f), Offset(0f, size.height), stroke)
                    drawLine(colors.lineStrong, Offset(size.width, 0f), Offset(size.width, size.height), stroke)
                }
                .padding(horizontal = spacing.space3, vertical = spacing.space2),
            contentAlignment = Alignment.Center,
        ) {
            OmText(
                text = value.toString(),
                style = typography.title,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        StepperButton(
            icon = OmIcons.Add,
            contentDescription = strings.increase,
            enabled = canIncrease,
            onClick = { onValueChange(value + step) },
        )
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = OmTheme.colors

    Box(
        modifier = Modifier
            .size(48.dp)
            .then(
                if (enabled) {
                    Modifier.omClickable(role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        OmIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            size = 20.dp,
            tint = if (enabled) colors.textPrimary else colors.textDisabled,
        )
    }
}
