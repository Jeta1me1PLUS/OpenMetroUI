package io.openmetro.ui.component.overlay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.openmetro.ui.core.theme.OmTheme
import kotlin.math.roundToInt

/**
 * 底部面板：直角、顶部 32×2dp 拖拽条，两档高度（半屏 / 全屏）。
 *
 * 拖拽跟手：拖动时偏移用 `snap()` 即时跟随手指，松手后用 250ms 回弹。
 * 向上拖过 120dp 展开到全屏，向下拖过 160dp 关闭，介于两者之间回到半屏。
 *
 * **拖拽只挂在顶部 48dp 的手柄区**，不挂整块面板 —— 否则面板里放纵向滚动列表时，
 * 拖列表会同时拖面板。代价是不能从内容区上滑展开，只能拖手柄。
 */
@Composable
fun OmSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * 只在 48dp 手柄区响应拖拽。
     *
     * 内容里有纵向滚动列表时必须保持 true，否则拖列表会连带拖面板。
     * 内容不滚动时可以传 false，让整块面板都能拖 —— 这是「嵌套滚动协商」的
     * 显式替代：由宿主声明内容能不能滚，比运行时猜测可靠。
     */
    dragHandleOnly: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        val colors = OmTheme.colors
        val spacing = OmTheme.spacing
        val motion = OmTheme.motion

        var expanded by remember { mutableStateOf(false) }
        var dragging by remember { mutableStateOf(false) }
        var dragOffset by remember { mutableFloatStateOf(0f) }

        val heightFraction by animateFloatAsState(
            targetValue = if (expanded) 1f else 0.5f,
            animationSpec = tween(durationMillis = motion.normal, easing = motion.standard),
            label = "omSheetHeight",
        )
        val offset by animateFloatAsState(
            targetValue = if (dragging) dragOffset else 0f,
            animationSpec = if (dragging) {
                snap()
            } else {
                tween(durationMillis = motion.normal, easing = motion.decelerate)
            },
            label = "omSheetOffset",
        )

        val dragModifier = Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                dragging = true
                dragOffset = (dragOffset + delta).coerceIn(-300f, 600f)
            },
            onDragStopped = {
                when {
                    dragOffset < -120f -> expanded = true
                    dragOffset > 160f -> onDismiss()
                    dragOffset > 40f -> expanded = false
                }
                dragging = false
                dragOffset = 0f
            },
        )

        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.scrim)
                    .pointerInput(Unit) { detectTapGestures { onDismiss() } },
            )

            Column(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(heightFraction)
                    .offset { IntOffset(0, offset.roundToInt()) }
                    .background(colors.surface)
                    .then(if (dragHandleOnly) Modifier else dragModifier),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SheetHandleHeight)
                        .then(if (dragHandleOnly) dragModifier else Modifier),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        Modifier
                            .size(width = 32.dp, height = 2.dp)
                            .background(colors.lineStrong),
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = spacing.space4),
                    content = content,
                )
            }
        }
    }
}

private val SheetHandleHeight = 48.dp
