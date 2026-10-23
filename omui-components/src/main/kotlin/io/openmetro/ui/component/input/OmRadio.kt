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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 圆形单选。
 *
 * 这是这套语言里少数保留圆形的地方之一 —— 圆形的语义（一组中只能选一个）比
 * 造型一致性更重要。选中态是 accent 实心内点，不是打勾。
 */
@Composable
fun OmRadio(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    val ring = if (enabled) colors.lineStrong else colors.textDisabled
    val dot = if (enabled) colors.accent else colors.textDisabled

    Row(
        modifier = modifier
            .omFocusRing()
            .heightIn(min = 48.dp)
            .omClickable(enabled = enabled, role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(Modifier.size(20.dp)) {
            val strokeWidth = 1.5.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            drawCircle(color = ring, radius = radius, style = Stroke(width = strokeWidth))
            if (selected) {
                drawCircle(color = dot, radius = size.minDimension * 0.26f)
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
