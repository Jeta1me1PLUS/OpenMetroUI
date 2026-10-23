package io.openmetro.ui.component.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme
import kotlin.math.roundToInt

/**
 * 滑杆：2dp 轨道 + 16dp 方形滑块，无气泡。
 *
 * 点击轨道任意位置直接跳过去，拖动跟手 —— 用 `awaitEachGesture` 自己处理，
 * 因为 `detectDragGestures` 只有超过 touch slop 才触发，纯点击不会响应。
 *
 * 数值展示交给外部文本（比如右侧一个 22sp 的数字），组件本身不画气泡。
 */
@Composable
fun OmSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val clamped = value.coerceIn(0f, 1f)

    val activeColor = if (enabled) colors.accent else colors.textDisabled
    val thumbColor = if (enabled) colors.accent else colors.textDisabled

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onValueChange((down.position.x / size.width).coerceIn(0f, 1f))
                    drag(down.id) { change ->
                        onValueChange((change.position.x / size.width).coerceIn(0f, 1f))
                        change.consume()
                    }
                }
            },
    ) {
        val thumbSize = 16.dp

        Canvas(Modifier.fillMaxSize()) {
            val centerY = size.height / 2
            val strokeWidth = 2.dp.toPx()
            drawLine(
                color = colors.line,
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = strokeWidth,
            )
            if (clamped > 0f) {
                drawLine(
                    color = activeColor,
                    start = Offset(0f, centerY),
                    end = Offset(size.width * clamped, centerY),
                    strokeWidth = strokeWidth,
                )
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    val travel = constraints.maxWidth - thumbSize.toPx()
                    IntOffset(
                        x = (travel * clamped).roundToInt(),
                        y = ((constraints.maxHeight - thumbSize.toPx()) / 2f).roundToInt(),
                    )
                }
                .size(thumbSize)
                .background(thumbColor),
        )
    }
}
