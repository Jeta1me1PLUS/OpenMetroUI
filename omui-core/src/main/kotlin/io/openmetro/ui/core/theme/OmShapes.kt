package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 形状与描边。
 *
 * [radius] 恒为 0 —— 这是硬约束，不开放修改。圆形元素（头像、状态点、单选、进度环）
 * 用 `CircleShape`，不走圆角。
 *
 * 描边宽度是唯一不受 4dp 网格约束的尺寸：1 / 2 / 3dp 是视觉重量，不是布局尺寸。
 */
@Immutable
data class OmShapes(
    val radius: Dp = 0.dp,
    val hairline: Dp = 1.dp,
    val emphasis: Dp = 2.dp,
    val accentBar: Dp = 3.dp,
) {
    companion object {
        val Default: OmShapes = OmShapes()
    }
}
