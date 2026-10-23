package io.openmetro.ui.component.content

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 左滑删除：向左拖过 60% 触发 [onDismiss]，否则回弹。
 *
 * 删除区宽度固定 96dp，是「一眼能看出是删除」的最小宽度。
 * 组件自己不负责把条目从列表里移除 —— 那是数据层的事，这里只报告手势结果。
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
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        // 背景：整块 danger，内容左移后露出的就是它
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(colors.danger),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .width(SwipeRevealWidth)
                    .padding(vertical = 4.dp),
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
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + delta).coerceIn(-maxSwipe, 0f))
                        }
                    },
                    onDragStopped = {
                        if (offsetX.value <= -maxSwipe * 0.6f) {
                            onDismiss()
                            scope.launch { offsetX.snapTo(0f) }
                        } else {
                            scope.launch { offsetX.animateTo(0f, tween(durationMillis = 150)) }
                        }
                    },
                ),
        ) {
            content()
        }
    }
}

private val SwipeRevealWidth = 96.dp
