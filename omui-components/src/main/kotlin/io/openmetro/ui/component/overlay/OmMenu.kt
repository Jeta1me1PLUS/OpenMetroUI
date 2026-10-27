package io.openmetro.ui.component.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.OmTheme

/**
 * 上下文菜单：锚定在某元素旁的直角面板。
 *
 * 用 `Popup(focusable = true)`，所以点外部或按返回键都会关闭。
 * 偏移量由调用方给 —— 组件无从知道触发元素有多高，硬算会错。
 *
 * ```kotlin
 * var open by remember { mutableStateOf(false) }
 * Box {
 *     OmIconButton(OmIcons.MoreVert, "更多") { open = true }
 *     OmMenu(expanded = open, onDismissRequest = { open = false }, offset = IntOffset(0, 48.dp.roundToPx())) {
 *         OmMenuItem("重命名", onClick = { })
 *         OmMenuItem("删除", onClick = { }, destructive = true)
 *     }
 * }
 * ```
 */
@Composable
fun OmMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!expanded) return

    val colors = OmTheme.colors
    val shapes = OmTheme.shapes
    val spacing = OmTheme.spacing

    Popup(
        // 靠近窗口底部时自动向上翻，避免面板被裁掉
        popupPositionProvider = remember(offset) {
            OmAnchorPositionProvider(extraOffset = offset)
        },
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = modifier
                .widthIn(min = 160.dp, max = 280.dp)
                .background(colors.surface)
                .border(width = shapes.hairline, color = colors.lineStrong)
                .padding(vertical = spacing.space1),
            content = content,
        )
    }
}

/** 菜单项。`destructive` 只改文字颜色，不加图标 —— 图标会把 48dp 的行挤得更满。 */
@Composable
fun OmMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .then(
                if (enabled) {
                    Modifier.omClickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = spacing.space3, vertical = spacing.space2),
        contentAlignment = Alignment.CenterStart,
    ) {
        OmText(
            text = text,
            style = typography.bodyM,
            color = when {
                !enabled -> colors.textDisabled
                destructive -> colors.danger
                else -> colors.textPrimary
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
