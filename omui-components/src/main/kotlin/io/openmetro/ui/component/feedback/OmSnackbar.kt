package io.openmetro.ui.component.feedback

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.rememberIsRtl
import io.openmetro.ui.core.theme.OmTheme

/**
 * 提示条：反白底 + 左侧 4dp accent 竖条。
 *
 * 组件**不负责定位**，由调用方摆在左下角（`Box` + `Alignment.BottomStart`）。
 * 入场是淡入 + 上移 24dp，250ms —— 位移在 24dp 以内，符合位移时长规则。
 *
 * 操作文字用 `onInverse` 而不是 accent：深红在反白底上只有 3:1，不够正文对比度；
 * 强调靠下方 2dp accent 下划线完成。
 */
@Composable
fun OmSnackbar(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val rtl = rememberIsRtl()
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    val motion = OmTheme.motion

    val progress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = motion.normal, easing = motion.standard),
        label = "omSnackbar",
    )

    if (progress <= 0f) return

    Row(
        modifier = modifier
            .graphicsLayer {
                alpha = progress
                translationY = (1f - progress) * 24.dp.toPx()
            }
            .background(colors.inverseSurface)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .drawBehind {
                val barWidth = 4.dp.toPx()
                drawRect(
                    color = colors.accent,
                    topLeft = Offset(if (rtl) size.width - barWidth else 0f, 0f),
                    size = Size(barWidth, size.height),
                )
            }
            .padding(
                start = spacing.space4 + 4.dp,
                end = spacing.space4,
                top = spacing.space3,
                bottom = spacing.space3,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmText(
            text = message,
            style = typography.bodyM,
            color = colors.onInverse,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(spacing.space3))
            Box(
                modifier = Modifier
                    .omClickable(role = Role.Button, onClick = onAction)
                    .padding(vertical = spacing.space1),
            ) {
                OmText(
                    text = actionLabel,
                    style = typography.label,
                    color = colors.onInverse,
                    maxLines = 1,
                )
                Spacer(Modifier.height(spacing.space1))
                Box(
                    Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(colors.accent),
                )
            }
        }
    }
}
