package io.openmetro.ui.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 验证码输入：N 个方格，当前待输入的格子用 2dp accent 边框。
 *
 * 只放一个透明的 `BasicTextField` 盖在方块上，而不是每格一个输入框 ——
 * 后者要自己处理焦点跳转、退格回退和粘贴分发，三个都是坑。
 *
 * 输入只保留数字，长度由 [length] 截断。
 */
@Composable
fun OmOtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val shapes = OmTheme.shapes
    val typography = OmTheme.typography
    val spacing = OmTheme.spacing

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val cursorIndex = value.length.coerceAtMost(length - 1)

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.space2),
        ) {
            repeat(length) { index ->
                val isCursor = enabled && focused && index == cursorIndex && value.length < length

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(colors.surface)
                        .border(
                            width = if (isCursor) 2.dp else shapes.hairline,
                            color = when {
                                !enabled -> colors.line
                                isCursor -> colors.accent
                                else -> colors.lineStrong
                            },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    OmText(
                        text = value.getOrNull(index)?.toString().orEmpty(),
                        style = typography.title,
                        color = if (enabled) colors.textPrimary else colors.textDisabled,
                        maxLines = 1,
                    )
                }
            }
        }

        // 透明输入层：盖满整行，点击任意位置都能聚焦
        BasicTextField(
            value = value,
            onValueChange = { input ->
                onValueChange(input.filter { it.isDigit() }.take(length))
            },
            modifier = Modifier
                .matchParentSize()
                .alpha(0f),
            enabled = enabled,
            textStyle = typography.title.copy(color = Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            cursorBrush = SolidColor(Color.Transparent),
            interactionSource = interactionSource,
        )
    }
}
