package io.openmetro.ui.core.theme

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * WCAG 2.1 相对亮度与对比度。
 *
 * 刻意只接受 ARGB 整数、不依赖 Compose 的 [androidx.compose.ui.graphics.Color]：
 * 这样可以由纯 JVM 单元测试直接覆盖，不需要 Robolectric 或设备。
 */
object OmContrast {

    val Black: Int = 0xFF000000.toInt()
    val White: Int = 0xFFFFFFFF.toInt()

    /** WCAG 正文最低对比度。 */
    const val AA_TEXT: Double = 4.5

    /** WCAG 大字（≥24sp，或 ≥19sp 加粗）与图形元素最低对比度。 */
    const val AA_LARGE: Double = 3.0

    fun relativeLuminance(argb: Int): Double {
        val r = linearize(((argb shr 16) and 0xFF) / 255.0)
        val g = linearize(((argb shr 8) and 0xFF) / 255.0)
        val b = linearize((argb and 0xFF) / 255.0)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    fun ratio(foreground: Int, background: Int): Double {
        val a = relativeLuminance(foreground)
        val b = relativeLuminance(background)
        return (max(a, b) + 0.05) / (min(a, b) + 0.05)
    }

    /**
     * 在给定底色上返回黑或白中对比度更高的一个。
     * 用于 accent 被宿主替换时自动推导 onAccent —— 不让用户自己填一个可能不达标的颜色。
     */
    fun bestOn(background: Int): Int =
        if (ratio(background, Black) >= ratio(background, White)) Black else White

    /**
     * [androidx.compose.ui.graphics.Color] 版本。先转 sRGB 分量再算，
     * 不走 Android 的 `toArgb()` —— 这样它在纯 JVM 单元测试里也能用。
     */
    fun ratio(
        foreground: androidx.compose.ui.graphics.Color,
        background: androidx.compose.ui.graphics.Color,
    ): Double = ratio(foreground.toArgbInt(), background.toArgbInt())

    private fun linearize(channel: Double): Double =
        if (channel <= 0.03928) channel / 12.92 else ((channel + 0.055) / 1.055).pow(2.4)
}
