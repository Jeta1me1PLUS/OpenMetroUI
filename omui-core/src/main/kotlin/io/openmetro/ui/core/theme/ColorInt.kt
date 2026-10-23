package io.openmetro.ui.core.theme

import androidx.compose.ui.graphics.Color

/**
 * 把 [Color] 转成 ARGB 整数。
 *
 * 不走 `Color.toArgb()` —— 那是 Android 实现，纯 JVM 单元测试调用会抛
 * "not mocked"。这里只读 Color 的 sRGB 分量，纯 Kotlin 可执行。
 */
internal fun Color.toArgbInt(): Int {
    fun channel(value: Float): Int = (value * 255f + 0.5f).toInt().coerceIn(0, 255)
    return (channel(alpha) shl 24) or
        (channel(red) shl 16) or
        (channel(green) shl 8) or
        channel(blue)
}
