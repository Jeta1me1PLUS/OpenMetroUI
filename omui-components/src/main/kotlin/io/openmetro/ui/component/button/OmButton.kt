package io.openmetro.ui.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

enum class OmButtonVariant {
    /** accent 填充，页面上的主要动作。 */
    Filled,

    /** 反白填充（深色块），次要动作。 */
    FilledInk,

    /** 1dp 描边，无底色。 */
    Outlined,

    /** 无底色无描边，最低权重。 */
    Text,

    /** 破坏性动作的确认按钮。 */
    Destructive,
}

enum class OmButtonSize { Small, Medium, Large }

/**
 * 直角按钮。视觉高度 32/40/48dp，触摸区统一扩到 48dp —— 视觉重量与可点区域是两件事。
 *
 * 按压反馈走 `Modifier.omClickable`（明度下沉），
 * 不使用水波纹；焦点环 2dp accent，画在外侧不改变布局。
 *
 * 文字规格固定为 14sp / 600 / 0.5sp 字距：不引入第 11 个字阶，而是在 `body.m` 上
 * 调整字重与字距 —— 按钮不是新的排版层级。
 */
@Composable
fun OmButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: OmButtonVariant = OmButtonVariant.Filled,
    size: OmButtonSize = OmButtonSize.Medium,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    fillWidth: Boolean = false,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val shapes = OmTheme.shapes

    val visualHeight = when (size) {
        OmButtonSize.Small -> 32.dp
        OmButtonSize.Medium -> 40.dp
        OmButtonSize.Large -> 48.dp
    }
    val horizontalPadding = when (size) {
        OmButtonSize.Small -> spacing.space3
        else -> spacing.space4
    }

    val background = when (variant) {
        OmButtonVariant.Filled -> colors.accent
        OmButtonVariant.FilledInk -> colors.textPrimary
        OmButtonVariant.Destructive -> colors.danger
        OmButtonVariant.Outlined, OmButtonVariant.Text -> Color.Transparent
    }
    val contentColor = when (variant) {
        OmButtonVariant.Filled -> colors.onAccent
        OmButtonVariant.FilledInk -> colors.background
        OmButtonVariant.Destructive -> colors.onDanger
        OmButtonVariant.Outlined, OmButtonVariant.Text -> colors.textPrimary
    }

    val resolvedBackground = when {
        variant == OmButtonVariant.Outlined || variant == OmButtonVariant.Text -> Color.Transparent
        !enabled -> background.copy(alpha = 0.12f)
        else -> background
    }
    val resolvedContent = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)
    val resolvedBorder = if (enabled) colors.lineStrong else colors.lineStrong.copy(alpha = 0.38f)

    val buttonTextStyle = OmTheme.typography.bodyM.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
    )

    Box(
        modifier = modifier
            .omFocusRing()
            .heightIn(min = 48.dp)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .omClickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .height(visualHeight)
                .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
                .background(resolvedBackground)
                .then(
                    if (variant == OmButtonVariant.Outlined) {
                        Modifier.border(width = shapes.hairline, color = resolvedBorder)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (leadingIcon != null) {
                OmIcon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    size = 18.dp,
                    tint = resolvedContent,
                )
                Spacer(Modifier.width(spacing.space2))
            }

            OmText(
                text = text,
                style = buttonTextStyle,
                color = resolvedContent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (trailingIcon != null) {
                Spacer(Modifier.width(spacing.space2))
                OmIcon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    size = 18.dp,
                    tint = resolvedContent,
                )
            }
        }
    }
}
