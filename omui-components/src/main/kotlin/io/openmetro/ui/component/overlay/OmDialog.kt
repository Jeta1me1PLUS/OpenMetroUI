package io.openmetro.ui.component.overlay

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme
import kotlin.math.roundToInt

/**
 * 直角对话框：宽度上限 400dp，标题 `headline`，按钮行右对齐。
 *
 * 入场只有「淡入 + 上移 24dp」，250ms —— 位移在 24dp 以内符合位移时长规则。
 * 用平台 `Dialog` 而不是自绘覆盖层：返回键、焦点陷阱、无障碍都由系统处理，
 * 自绘这两样都很容易漏。
 *
 * @param actions 按钮行；为 null 时不渲染底部区域
 */
@Composable
fun OmDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            // 让 scrim 盖住状态栏与导航栏，否则四周会露出一圈桌面
            decorFitsSystemWindows = false,
        ),
    ) {
        val colors = OmTheme.colors
        val spacing = OmTheme.spacing
        val motion = OmTheme.motion

        var shown by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { shown = true }

        val alpha by animateFloatAsState(
            targetValue = if (shown) 1f else 0f,
            animationSpec = tween(durationMillis = motion.normal, easing = motion.decelerate),
            label = "omDialogAlpha",
        )
        val rise by animateDpAsState(
            targetValue = if (shown) 0.dp else 24.dp,
            animationSpec = tween(durationMillis = motion.normal, easing = motion.decelerate),
            label = "omDialogRise",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.scrim)
                .pointerInput(Unit) { detectTapGestures { onDismiss() } }
                .graphicsLayer { this.alpha = alpha },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = spacing.space6)
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .offset { IntOffset(0, rise.roundToPx()) }
                    .background(colors.surface)
                    // 吃掉面板上的点击，避免穿透到 scrim 触发关闭
                    .pointerInput(Unit) { detectTapGestures { } }
                    .padding(spacing.space6),
            ) {
                OmText(title, style = OmTheme.typography.headline)
                Spacer(Modifier.height(spacing.space4))
                content()

                if (actions != null) {
                    Spacer(Modifier.height(spacing.space6))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}
