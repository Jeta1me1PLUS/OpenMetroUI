package io.openmetro.ui.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.LocalOmStrings
import io.openmetro.ui.core.theme.OmTheme

enum class OmTextFieldVariant {
    /** 无底色，只有底部线。默认。 */
    Underline,

    /** `surfaceVariant` 底色 + 底部线。 */
    Filled,
}

/**
 * 文本输入：**标签固定在输入区上方**，不做浮动标签。
 *
 * 浮动标签的缩放上浮动画与这套「稳定块面」的语言冲突，而且动画过程中文字会脱离
 * 4dp 基线。固定标签也让字段在任何状态下占同样的高度，表单不会因为聚焦而抖动。
 *
 * 底部线槽位恒为 2dp 高，聚焦/错误时把线从 1dp 换成 2dp —— 线变粗但布局不动。
 */
@Composable
fun OmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    variant: OmTextFieldVariant = OmTextFieldVariant.Underline,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    val shapes = OmTheme.shapes

    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val isError = errorText != null
    val message = errorText ?: helperText

    val lineColor = when {
        !enabled -> colors.line
        isError -> colors.danger
        focused -> colors.accent
        else -> colors.fieldLine
    }
    val lineThickness = if (enabled && (isError || focused)) shapes.emphasis else shapes.hairline

    val textStyle = typography.bodyL.copy(
        color = if (enabled) colors.textPrimary else colors.textDisabled,
    )
    val surfaceColor = if (variant == OmTextFieldVariant.Filled) {
        colors.surfaceVariant
    } else {
        Color.Transparent
    }
    val placeholderMaxLines = if (singleLine) 1 else Int.MAX_VALUE

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(if (enabled) colors.accent else colors.textDisabled),
        decorationBox = { innerTextField ->
            Column(Modifier.fillMaxWidth()) {
                if (label != null) {
                    OmText(label, style = typography.label, color = colors.textSecondary)
                    Spacer(Modifier.height(spacing.space2))
                }

                Column(Modifier.fillMaxWidth().background(surfaceColor)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = if (variant == OmTextFieldVariant.Filled) {
                                    spacing.space3
                                } else {
                                    spacing.none
                                },
                            )
                            .heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (leadingIcon != null) {
                            OmIcon(
                                imageVector = leadingIcon,
                                contentDescription = null,
                                size = 20.dp,
                                tint = colors.textSecondary,
                            )
                            Spacer(Modifier.width(spacing.space2))
                        }

                        Box(Modifier.weight(1f)) {
                            if (value.isEmpty() && placeholder != null) {
                                OmText(
                                    text = placeholder,
                                    style = textStyle,
                                    color = colors.textDisabled,
                                    maxLines = placeholderMaxLines,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            innerTextField()
                        }

                        if (trailingIcon != null) {
                            Spacer(Modifier.width(spacing.space2))
                            // 用 Box 撑出 48dp 触摸区：OmIcon 内部会覆盖 modifier 的 size
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .then(
                                        if (onTrailingIconClick != null) {
                                            Modifier.omClickable(onClick = onTrailingIconClick)
                                        } else {
                                            Modifier
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                OmIcon(
                                    imageVector = trailingIcon,
                                    // 可点的尾部图标必须有描述，否则读屏只念「图片」
                                    contentDescription = if (onTrailingIconClick != null) {
                                        LocalOmStrings.current.clear
                                    } else {
                                        null
                                    },
                                    size = 20.dp,
                                    tint = colors.textSecondary,
                                )
                            }
                        }
                    }

                    // 固定 2dp 槽位：线从 1dp 变 2dp 时布局不变。
                    Box(
                        modifier = Modifier.fillMaxWidth().height(shapes.emphasis),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(lineThickness)
                                .background(lineColor),
                        )
                    }
                }

                if (message != null) {
                    Spacer(Modifier.height(spacing.space2))
                    OmText(
                        text = message,
                        style = typography.caption,
                        color = if (isError) colors.danger else colors.textSecondary,
                    )
                }
            }
        },
    )
}
