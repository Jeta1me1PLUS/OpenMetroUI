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
    /**
     * 按时长缩放系数生成一份副本，用于跟随系统的「动画时长缩放」设置。
     *
     * 系统把它设为 0（减弱动态效果 / 无障碍）时，所有时长归零，动画会瞬间完成。
     * 系数大于 1 时不放大 —— 这个设置的语义是「减弱」而不是「加速」。
     */
    fun scaledBy(factor: Float): OmMotion {
        if (factor >= 1f) return this
        val f = factor.coerceIn(0f, 1f)
        return OmMotion(
            instant = (instant * f).toInt(),
            fast = (fast * f).toInt(),
            normal = (normal * f).toInt(),
            slow = (slow * f).toInt(),
            page = (page * f).toInt(),
            standard = standard,
            decelerate = decelerate,
            accelerate = accelerate,
        )
    }

    companion object {
        val Default: OmMotion = OmMotion()
    }
}
