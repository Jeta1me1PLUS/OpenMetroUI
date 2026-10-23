package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp

/** 一条磁贴色对。`onSurface` 保证与 `surface` 达到 WCAG AA（≥4.5:1）。 */
@Immutable
data class OmTileColor(
    val surface: Color,
    val onSurface: Color,
)

/**
 * 磁贴可用的全部色对。
 *
 * [spectrum] 恒为 8 组，仅在 [OmTileMode.Spectrum] 下使用；由组件决定是否启用，
 * 而非由配色表决定——切换模式不必重建整个配色。
 */
@Immutable
data class OmTileColors(
    val paper: OmTileColor,
    val quiet: OmTileColor,
    val ink: OmTileColor,
    val accent: OmTileColor,
    val spectrum: List<OmTileColor>,
) {
    /**
     * 按色调语义取当前模式下的色对。
     *
     * Spectrum 模式下 Ink 仍取反白块：八色全是等明度的浅块，如果连锚点也变浅，
     * 整屏会失去重量。保留一块深色是这套配色能成立的前提。
     */
    fun resolve(tone: OmTileTone, mode: OmTileMode): OmTileColor = when (mode) {
        OmTileMode.Monochrome -> when (tone) {
            OmTileTone.Paper -> paper
            OmTileTone.Quiet -> quiet
            OmTileTone.Ink -> ink
            OmTileTone.Accent -> accent
        }
        OmTileMode.Spectrum -> when (tone) {
            OmTileTone.Paper -> spectrum[0]
            OmTileTone.Quiet -> spectrum[2]
            OmTileTone.Ink -> ink
            OmTileTone.Accent -> spectrum[7]
        }
    }
}

/**
 * 语义配色表。组件只允许使用这里的语义别名，不允许直接引用中性阶。
 *
 * 深色主题不是浅色的简单反相：中性阶在中段做过压缩调整，保证 `textSecondary`
 * 在两套主题下都稳定达到 4.5:1。
 */
@Immutable
data class OmColorScheme(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val line: Color,
    val lineStrong: Color,
    /** 输入框底线等「必须可辨识的控件边界」，对比度 ≥ 3:1；比 [line] 深。 */
    val fieldLine: Color,
    val accent: Color,
    val onAccent: Color,
    val accentQuiet: Color,
    val inverseSurface: Color,
    val onInverse: Color,
    val danger: Color,
    val onDanger: Color,
    val warning: Color,
    val success: Color,
    val scrim: Color,
    val tiles: OmTileColors,
) {
    companion object {

        /** 瑞士红，近似 Pantone 485。 */
        val DefaultAccent: Color = Color(0xFFDA291C)

        /** 深色主题下提亮，避免红字糊在黑底上。 */
        val DarkAccent: Color = Color(0xFFFF5A4A)

        fun paper(accent: Color = DefaultAccent): OmColorScheme = build(dark = false, accent = accent)

        fun ink(accent: Color = DarkAccent): OmColorScheme = build(dark = true, accent = accent)

        fun of(dark: Boolean, accent: Color = if (dark) DarkAccent else DefaultAccent): OmColorScheme =
            build(dark = dark, accent = accent)

        private fun build(dark: Boolean, accent: Color): OmColorScheme =
            if (dark) inkScheme(accent) else paperScheme(accent)

        /**
         * 把过浅（浅色主题）或过暗（深色主题）的 accent 拉回可辨识范围：
         * 它要作为焦点环、指示条、进度使用，必须对背景达到 3:1。
         * 逐级向黑/白混合，取第一个达标值——保证宿主传入任何色相都不会得到「看不见的强调色」。
         */
        private fun ensureVisible(accent: Color, background: Color, dark: Boolean): Color {
            if (OmContrast.ratio(accent.toArgbInt(), background.toArgbInt()) >= OmContrast.AA_LARGE) {
                return accent
            }
            val target = if (dark) Color.White else Color.Black
            for (step in 1..20) {
                val candidate = lerp(accent, target, step / 20f)
                if (OmContrast.ratio(candidate.toArgbInt(), background.toArgbInt()) >= OmContrast.AA_LARGE) {
                    return candidate
                }
            }
            return target
        }

        private fun paperScheme(accent: Color): OmColorScheme {
            val n0 = Color(0xFFFFFFFF)
            val n50 = Color(0xFFFAFAF8)
            val n100 = Color(0xFFF2F2EF)
            val n200 = Color(0xFFE4E4E0)
            val n400 = Color(0xFFB3B3AD)
            // 原 #8E8E88 对 surfaceVariant 只有 2.94:1，差一点点到 3:1；加深一档。
            val n500 = Color(0xFF85857F)
            val n600 = Color(0xFF6B6B66)
            val n800 = Color(0xFF31312E)
            val n1000 = Color(0xFF0C0C0B)

            // 用 surfaceVariant 而非 background 作为参照：accent 会画在更亮的表面与更暗的
            // 面板上，取最不利的那个背景才能保证在所有表面上都看得见。
            val resolvedAccent = ensureVisible(accent, n100, dark = false)
            val onAccent = Color(OmContrast.bestOn(resolvedAccent.toArgbInt()))

            return OmColorScheme(
                isDark = false,
                background = n50,
                surface = n0,
                surfaceVariant = n100,
                textPrimary = n1000,
                textSecondary = n600,
                textDisabled = n400,
                line = n200,
                lineStrong = n800,
                fieldLine = n500,
                accent = resolvedAccent,
                onAccent = onAccent,
                // 10% 叠加：既保留 accent 的色相，又不至于变成第二块强调色。
                accentQuiet = resolvedAccent.copy(alpha = 0.10f).compositeOver(n50),
                inverseSurface = n1000,
                onInverse = n50,
                danger = Color(0xFFA3150A),
                onDanger = Color(0xFFFFFFFF),
                warning = Color(0xFF9A5B00),
                success = Color(0xFF2E7D5B),
                scrim = Color(0x52000000),
                tiles = OmTileColors(
                    paper = OmTileColor(n0, n1000),
                    quiet = OmTileColor(n100, n1000),
                    ink = OmTileColor(n1000, n50),
                    accent = OmTileColor(resolvedAccent, onAccent),
                    spectrum = SpectrumTints.map { OmTileColor(it, n1000) },
                ),
            )
        }

        private fun inkScheme(accent: Color): OmColorScheme {
            val n0 = Color(0xFF0C0C0B)
            val n50 = Color(0xFF141413)
            val n100 = Color(0xFF1C1C1A)
            val n200 = Color(0xFF262624)
            val n300 = Color(0xFF333330)
            val n500 = Color(0xFF5C5C57)
            // 原 #7A7A74 对 surfaceVariant 只有 3.92:1；提亮到 4.71:1 留出余量。
            val n600 = Color(0xFF90908A)
            val n700 = Color(0xFF9C9C96)
            val n1000 = Color(0xFFFAFAF8)

            // Ink 里 surfaceVariant 是最亮的表面，对浅色 accent 最不利。
            val resolvedAccent = ensureVisible(accent, n200, dark = true)
            val onAccent = Color(OmContrast.bestOn(resolvedAccent.toArgbInt()))

            return OmColorScheme(
                isDark = true,
                background = n0,
                surface = n100,
                surfaceVariant = n200,
                textPrimary = n1000,
                textSecondary = n600,
                textDisabled = n500,
                line = n300,
                lineStrong = n700,
                fieldLine = n600,
                accent = resolvedAccent,
                onAccent = onAccent,
                accentQuiet = resolvedAccent.copy(alpha = 0.18f).compositeOver(n0),
                inverseSurface = n50,
                onInverse = n1000,
                danger = Color(0xFFFF6B5C),
                onDanger = Color(0xFF0C0C0B),
                warning = Color(0xFFE8A33D),
                success = Color(0xFF4FBF95),
                scrim = Color(0x8C000000),
                tiles = OmTileColors(
                    paper = OmTileColor(n100, n1000),
                    quiet = OmTileColor(n200, n1000),
                    ink = OmTileColor(n50, n1000),
                    accent = OmTileColor(resolvedAccent, onAccent),
                    spectrum = SpectrumSolids.map { OmTileColor(it, Color(0xFFFFFFFF)) },
                ),
            )
        }

        /** Spectrum 浅底（配深字）。固定明度与彩度，只变色相。 */
        private val SpectrumTints: List<Color> = listOf(
            Color(0xFFF6DDD7), // vermilion
            Color(0xFFF3E7CB), // ochre
            Color(0xFFE4E8D2), // olive
            Color(0xFFD6E7E3), // teal
            Color(0xFFDCE3F3), // cobalt
            Color(0xFFE0E4E8), // slate
            Color(0xFFEEDDE6), // plum
            Color(0xFFF7DBDD), // crimson
        )

        /** Spectrum 深底（配白字），白字对比度全部 ≥ 4.5:1。 */
        private val SpectrumSolids: List<Color> = listOf(
            Color(0xFFB33A2A),
            Color(0xFF8A6A14),
            Color(0xFF5C6A2A),
            Color(0xFF26695F),
            Color(0xFF33518F),
            Color(0xFF4A5560),
            Color(0xFF6E3B5C),
            Color(0xFF9C2E3A),
        )
    }
}
