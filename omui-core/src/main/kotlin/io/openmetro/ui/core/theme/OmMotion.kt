package io.openmetro.ui.core.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable

/**
 * 动效。时长单位为毫秒，直接可传给 `tween(durationMillis = …)`。
 *
 * 只允许五类动画：位移、透明度、色块揭示、磁贴翻转、颜色过渡。
 * 禁止弹簧与回弹 —— `easing.standard` 是快出慢入、无 overshoot 的曲线。
 */
@Immutable
class OmMotion(
    val instant: Int = 100,
    val fast: Int = 150,
    val normal: Int = 250,
    val slow: Int = 350,
    val page: Int = 500,
    val standard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f),
    val decelerate: Easing = CubicBezierEasing(0f, 0f, 0f, 1f),
    val accelerate: Easing = CubicBezierEasing(0.3f, 0f, 1f, 1f),
) {
    companion object {
        val Default: OmMotion = OmMotion()
    }
}
