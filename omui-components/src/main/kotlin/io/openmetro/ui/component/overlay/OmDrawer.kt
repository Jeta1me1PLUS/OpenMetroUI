package io.openmetro.ui.component.overlay

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.openmetro.ui.core.theme.OmTheme
import kotlin.math.roundToInt

/**
 * 侧边抽屉：从 start 侧滑出，直角面板，最大宽度 320dp（窄屏占 82%）。
 *
 * 滑入方向按 `LocalLayoutDirection` 镜像 —— RTL 下从右侧滑出。Compose 的
 * `offset` 不会自动镜像，这一步必须自己做。
 *
 * 入场只有位移（250ms），不做缩放与透明度渐变以外的效果。
 */
@Composable
fun OmDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
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
        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

        var shown by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { shown = true }

        val slide by animateDpAsState(
            targetValue = if (shown) 0.dp else DrawerMaxWidth,
            animationSpec = tween(durationMillis = motion.normal, easing = motion.standard),
            label = "omDrawerSlide",
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
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
                    .fillMaxWidth(0.82f)
                    .widthIn(max = DrawerMaxWidth)
                    .offset {
                        // RTL 下取反，否则会从错误的方向滑入
                        val dx = slide.roundToPx() * if (rtl) -1 else 1
                        IntOffset(dx, 0)
                    }
                    .background(colors.surface)
                    .pointerInput(Unit) { detectTapGestures { } }
                    .padding(horizontal = spacing.space4, vertical = spacing.space6),
                content = content,
            )
        }
    }
}

private val DrawerMaxWidth = 320.dp
