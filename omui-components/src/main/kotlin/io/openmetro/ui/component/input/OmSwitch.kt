package io.openmetro.ui.component.input

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 开关：直角轨道 + 方形滑块，滑动 150ms。
 *
 * 不用圆形轨道与圆形滑块 —— 这是这套语言里少数几个「本该是圆却做成方」的地方，
 * 目的是与磁贴、按钮的直角块面保持一致。滑块同时是触摸指示，尺寸 20dp。
 *
 * 轨道 44×24dp，触摸区扩到 48×48dp。
 */
@Composable
fun OmSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val motion = OmTheme.motion

    val trackColor = when {
        !enabled -> colors.textDisabled
        checked -> colors.accent
        else -> colors.lineStrong
    }
    val knobColor = if (enabled) colors.onAccent else colors.background

    val knobOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = tween(durationMillis = motion.fast, easing = motion.standard),
        label = "omSwitchKnob",
    )

    Box(
        modifier = modifier
            .omFocusRing()
            .heightIn(min = 48.dp)
            .widthIn(min = 48.dp)
            .omClickable(enabled = enabled, role = Role.Switch) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 24.dp)
                .background(trackColor),
        ) {
            Box(
                modifier = Modifier
                    // 偏移量由动画状态驱动，必须用 lambda 版本，避免每帧重组
                    .offset {
                        IntOffset(x = knobOffset.roundToPx(), y = 2.dp.roundToPx())
                    }
                    .size(20.dp)
                    .background(knobColor),
            )
        }
    }
}
