package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme
import kotlin.math.roundToInt

/**
 * 左滑删除：向左拖过 60% 触发 [onDismiss]，否则复位。
 *
 * 偏移用**同步的可变状态**驱动，不能用 `Animatable.snapTo` ——
 * 那是挂起函数，每次拖动都要 `launch` 一个协程，跟手时会明显滞后甚至看起来没动。
 *
 * 组件自己不负责把条目从列表里移除，只报告手势结果。
 */
@Composable
fun OmSwipeToDismiss(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = OmTheme.colors
    val density = LocalDensity.current
    val maxSwipe = with(density) { SwipeRevealWidth.toPx() }

    var offsetX by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier) {
        // 删除区只占右侧，左侧露出的是父容器的底色而不是红色
        Row(modifier = Modifier.matchParentSize()) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(SwipeRevealWidth)
                    .fillMaxHeight()
                    .background(colors.danger),
                contentAlignment = Alignment.Center,
            ) {
                OmText(
                    text = "删除",
                    style = OmTheme.typography.label,
                    color = Color.White,
                    maxLines = 1,
                )
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // 必须同步更新：这里 launch 协程会让拖动完全跟不上手指
                        offsetX = (offsetX + delta).coerceIn(-maxSwipe, 0f)
                    },
                    onDragStopped = {
                        if (offsetX <= -maxSwipe * 0.6f) {
                            onDismiss()
                        }
                        offsetX = 0f
                    },
                ),
        ) {
            content()
        }
    }
}

private val SwipeRevealWidth = 96.dp
