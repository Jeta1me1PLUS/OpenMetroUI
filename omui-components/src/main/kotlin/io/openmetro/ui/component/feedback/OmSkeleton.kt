package io.openmetro.ui.component.feedback

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.openmetro.ui.core.theme.OmTheme

/**
 * 骨架占位：**明度脉冲**，不是扫光。
 *
 * Material 的骨架是一条高光从左扫到右（`Brush.linearGradient`），那属于渐变，
 * 与「零渐变」的约束冲突。这里改为整体明度在 40%–100% 之间呼吸。
 *
 * 尺寸完全交给调用方：`OmSkeleton(Modifier.fillMaxWidth().height(16.dp))`。
 */
@Composable
fun OmSkeleton(modifier: Modifier = Modifier) {
    val colors = OmTheme.colors

    val transition = rememberInfiniteTransition(label = "omSkeleton")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "omSkeletonAlpha",
    )

    Box(modifier = modifier.background(colors.surfaceVariant.copy(alpha = alpha)))
}
