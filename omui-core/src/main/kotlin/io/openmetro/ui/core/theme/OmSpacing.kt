package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 间距阶。取值必须是 4dp 的整数倍，命名即倍率（`space6` = 6 × 4dp = 24dp）。
 * 不接受 6dp / 10dp / 14dp / 20dp / 28dp 这类值。
 */
@Immutable
data class OmSpacing(
    val none: Dp = 0.dp,
    val space1: Dp = 4.dp,
    val space2: Dp = 8.dp,
    val space3: Dp = 12.dp,
    val space4: Dp = 16.dp,
    val space6: Dp = 24.dp,
    val space8: Dp = 32.dp,
    val space12: Dp = 48.dp,
    val space16: Dp = 64.dp,
    val space24: Dp = 96.dp,
) {
    /** 以 4dp 为单位的任意倍率。栅格计算与自定义布局使用，界面代码应优先用命名档位。 */
    fun of(units: Int): Dp = (units * UnitDp).dp

    companion object {
        const val UnitDp: Int = 4
        val Default: OmSpacing = OmSpacing()
    }
}
