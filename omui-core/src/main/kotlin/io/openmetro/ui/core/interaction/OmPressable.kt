package io.openmetro.ui.core.interaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import io.openmetro.ui.core.theme.OmTheme

/**
 * 可点元素的统一入口：**明度下沉**式按压反馈，不是水波纹。
 *
 * 圆形水波是 Material 的语言，一出现就会把整体气质带偏。这里改为在内容之上叠一层
 * 极淡的黑（浅色主题 8%）或白（深色主题 10%）—— 视觉上等于色块明度降了一档，
 * 而且与元素原本的底色无关，任何磁贴色、反白块上都能用。
 *
 * 实现上不用 `Indication` 接口：那套 API（`IndicationInstance` / `rememberUpdatedInstance`）
 * 已废弃，官方要求迁到 `Modifier.Node`。这里直接在绘制阶段叠加，行为等价且不引额外节点。
 *
 * 不做缩放、不做位移：块面的几何稳定性是这套语言的前提。
 */
fun Modifier.omClickable(
    enabled: Boolean = true,
    role: Role? = null,
    onClickLabel: String? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val overlay = if (OmTheme.colors.isDark) WhiteOverlay else BlackOverlay

    this
        .drawWithContent {
            drawContent()
            if (pressed) drawRect(overlay)
        }
        .clickable(
            interactionSource = interactionSource,
            // 关掉平台默认指示：按压态由上面的 drawWithContent 负责。
            indication = null,
            enabled = enabled,
            role = role,
            onClickLabel = onClickLabel,
            onClick = onClick,
        )
}

private val BlackOverlay: Color = Color.Black.copy(alpha = 0.08f)
private val WhiteOverlay: Color = Color.White.copy(alpha = 0.10f)
