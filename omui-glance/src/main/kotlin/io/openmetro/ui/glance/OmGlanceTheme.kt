package io.openmetro.ui.glance

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import io.openmetro.ui.core.theme.OmColorScheme
import io.openmetro.ui.core.theme.OmTileColor
import io.openmetro.ui.core.theme.OmTileMode
import io.openmetro.ui.core.theme.OmTileTone

/**
 * 小组件里能用的颜色。
 *
 * Glance 没有 `CompositionLocal` 的主题下发机制，也没法读 `OmTheme` ——
 * 它是另一套渲染管线。所以这里只做一件事：把 [OmColorScheme] 里的色值
 * 转成 Glance 的 `ColorProvider`，保证小组件和 App 内用的是同一份 token。
 */
@Immutable
data class OmGlanceColors(
    val background: ColorProvider,
    val surface: ColorProvider,
    val textPrimary: ColorProvider,
    val textSecondary: ColorProvider,
    val accent: ColorProvider,
    val onAccent: ColorProvider,
    val line: ColorProvider,
) {
    companion object {
        fun of(dark: Boolean): OmGlanceColors = scheme(dark).toGlance()
    }
}

/** 小组件里取一份配色方案。语义与 `OmTheme(darkTheme = …)` 一致。 */
fun scheme(dark: Boolean): OmColorScheme =
    if (dark) OmColorScheme.ink() else OmColorScheme.paper()

fun OmColorScheme.toGlance(): OmGlanceColors = OmGlanceColors(
    background = ColorProvider(background),
    surface = ColorProvider(surface),
    textPrimary = ColorProvider(textPrimary),
    textSecondary = ColorProvider(textSecondary),
    accent = ColorProvider(accent),
    onAccent = ColorProvider(onAccent),
    line = ColorProvider(line),
)

/** 解析磁贴色调，返回（底色, 前景色）。与 `OmTile` 的取色规则保持同一套。 */
fun omGlanceTile(
    dark: Boolean,
    tone: OmTileTone,
    mode: OmTileMode = OmTileMode.Monochrome,
): Pair<ColorProvider, ColorProvider> {
    val tile: OmTileColor = scheme(dark).tiles.resolve(tone, mode)
    return ColorProvider(tile.surface) to ColorProvider(tile.onSurface)
}

/**
 * 把 Compose 的 `TextStyle` 转成 Glance 的。
 *
 * Glance 只支持字重、字号、颜色这几项，字距与行高会被丢掉 —— 小组件里
 * 文字是单行的多，这个损失可以接受。
 */
fun TextStyle.toGlance(color: ColorProvider): androidx.glance.text.TextStyle =
    androidx.glance.text.TextStyle(
        color = color,
        fontSize = (fontSize.value.takeIf { it > 0f } ?: 14f).sp,
        fontWeight = fontWeight.toGlance(),
    )

private fun androidx.compose.ui.text.font.FontWeight?.toGlance(): FontWeight {
    val weight = this?.weight ?: 400
    // Glance 的 FontWeight 只有 Normal / Medium / Bold 三档，300 也落到 Normal
    return when {
        weight <= 400 -> FontWeight.Normal
        weight <= 500 -> FontWeight.Medium
        else -> FontWeight.Bold
    }
}
