package io.openmetro.ui.component.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/** 只有「中性提示」与「错误」两种。规范里没有 warning 色，不为它单独造一个。 */
enum class OmBannerTone { Info, Error }

/**
 * 通栏提示：贴在内容区顶部，不浮层、不遮挡。
 *
 * 左侧 4dp 色条是它唯一的强调手段 —— 底色只做极淡的叠加以避免抢走内容注意力。
 * 与 [OmSnackbar] 的分工：Banner 常驻直到用户处理，Snackbar 几秒后自己消失。
 */
@Composable
fun OmBanner(
    text: String,
    modifier: Modifier = Modifier,
    tone: OmBannerTone = OmBannerTone.Info,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    val accentColor = when (tone) {
        OmBannerTone.Info -> colors.lineStrong
        OmBannerTone.Error -> colors.danger
    }
    val background = when (tone) {
        OmBannerTone.Info -> colors.surfaceVariant
        // 8% 叠加：够看出「这是错误」，又不至于变成一整块红
        OmBannerTone.Error -> colors.danger.copy(alpha = 0.08f).compositeOver(colors.surface)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .drawBehind {
                drawRect(color = accentColor, size = Size(4.dp.toPx(), size.height))
            }
            .padding(
                start = spacing.space4,
                end = spacing.space2,
                top = spacing.space3,
                bottom = spacing.space3,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmText(
            text = text,
            style = typography.bodyM,
            modifier = Modifier.weight(1f),
        )

        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(spacing.space3))
            Box(
                modifier = Modifier
                    .omClickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = spacing.space2, vertical = spacing.space1),
            ) {
                OmText(
                    text = actionLabel,
                    style = typography.label,
                    color = if (tone == OmBannerTone.Error) colors.danger else colors.textPrimary,
                    maxLines = 1,
                )
                Spacer(Modifier.height(spacing.space1))
                Box(
                    Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(accentColor),
                )
            }
        }

        if (onDismiss != null) {
            Spacer(Modifier.width(spacing.space1))
            Box(
                modifier = Modifier
                    .omClickable(role = Role.Button, onClick = onDismiss)
                    .padding(spacing.space2),
                contentAlignment = Alignment.Center,
            ) {
                OmIcon(
                    imageVector = OmIcons.Close,
                    contentDescription = "关闭",
                    size = 16.dp,
                    tint = colors.textSecondary,
                )
            }
        }
    }
}
