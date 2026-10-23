package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** 一条需要在构建期被校验的色对。 */
@Immutable
data class ContrastCheck(
    val name: String,
    val foreground: Color,
    val background: Color,
    val minRatio: Double,
)

/**
 * 语义色对的对比度清单。由本库的单元测试与 `omui-test` 的自检工具共同使用，
 * 因此是公开 API —— 宿主换了 accent 之后可以直接跑同一份清单。
 *
 * 覆盖范围：正文对、反白对、状态色对、全部磁贴色对（含 Spectrum 八组）。
 * 禁用态刻意不在清单内 —— WCAG 允许禁用控件免除对比度要求。
 */
fun OmColorScheme.contrastChecks(): List<ContrastCheck> {
    val checks = mutableListOf<ContrastCheck>()

    fun text(name: String, foreground: Color, background: Color) {
        checks += ContrastCheck(name, foreground, background, OmContrast.AA_TEXT)
    }

    fun graphic(name: String, foreground: Color, background: Color) {
        checks += ContrastCheck(name, foreground, background, OmContrast.AA_LARGE)
    }

    text("textPrimary/background", textPrimary, background)
    text("textPrimary/surface", textPrimary, surface)
    text("textPrimary/surfaceVariant", textPrimary, surfaceVariant)
    text("textPrimary/accentQuiet", textPrimary, accentQuiet)
    text("textSecondary/background", textSecondary, background)
    text("textSecondary/surface", textSecondary, surface)
    text("textSecondary/surfaceVariant", textSecondary, surfaceVariant)
    text("onAccent/accent", onAccent, accent)
    text("onInverse/inverseSurface", onInverse, inverseSurface)
    text("onDanger/danger", onDanger, danger)
    text("warning/background", warning, background)
    text("warning/surface", warning, surface)
    text("success/background", success, background)
    text("success/surface", success, surface)

    // 非文本：控件边界、焦点环、强调线。这些不是文字，按 3:1 要求。
    graphic("fieldLine/surfaceVariant", fieldLine, surfaceVariant)
    graphic("lineStrong/background", lineStrong, background)
    graphic("accent/background", accent, background)
    graphic("accent/surface", accent, surface)

    listOf(
        "tile.paper" to tiles.paper,
        "tile.quiet" to tiles.quiet,
        "tile.ink" to tiles.ink,
        "tile.accent" to tiles.accent,
    ).forEach { (name, pair) ->
        text("$name.onSurface", pair.onSurface, pair.surface)
    }

    tiles.spectrum.forEachIndexed { index, pair ->
        text("tile.spectrum[$index].onSurface", pair.onSurface, pair.surface)
    }

    return checks
}
