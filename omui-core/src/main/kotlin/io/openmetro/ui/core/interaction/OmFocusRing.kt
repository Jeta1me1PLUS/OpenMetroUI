package io.openmetro.ui.core.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import io.openmetro.ui.core.theme.OmTheme

/**
 * 键盘 / DPAD / 遥控的焦点环：2dp accent，画在元素**外侧**，不改变布局。
 *
 * 只做绘制，不负责让元素可聚焦 —— 可聚焦性由 `clickable` 或调用方提供的
 * `focusable()` 决定。这样它在纯展示元素上也能安全使用。
 */
@Composable
fun Modifier.omFocusRing(): Modifier {
    val accent = OmTheme.colors.accent
    val strokeWidth = OmTheme.shapes.emphasis
    var focused by remember { mutableStateOf(false) }

    return this
        .onFocusChanged { focused = it.isFocused }
        .drawBehind {
            if (!focused) return@drawBehind
            val stroke = strokeWidth.toPx()
            drawRect(
                color = accent,
                topLeft = Offset(-stroke, -stroke),
                size = Size(size.width + stroke * 2, size.height + stroke * 2),
                style = Stroke(width = stroke),
            )
        }
}
