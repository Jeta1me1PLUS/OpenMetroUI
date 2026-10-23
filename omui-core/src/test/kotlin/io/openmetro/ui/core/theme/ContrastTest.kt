package io.openmetro.ui.core.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContrastTest {

    @Test
    fun `relative luminance matches WCAG anchors`() {
        assertEquals(0.0, OmContrast.relativeLuminance(0xFF000000.toInt()), 1e-6)
        assertEquals(1.0, OmContrast.relativeLuminance(0xFFFFFFFF.toInt()), 1e-6)
    }

    @Test
    fun `ratio is symmetric and self ratio is one`() {
        val red = 0xFFDA291C.toInt()
        val white = 0xFFFFFFFF.toInt()
        assertEquals(OmContrast.ratio(red, white), OmContrast.ratio(white, red), 1e-9)
        assertEquals(1.0, OmContrast.ratio(red, red), 1e-9)
    }

    @Test
    fun `bestOn picks the higher contrast of black and white`() {
        assertEquals(OmContrast.White, OmContrast.bestOn(0xFF0C0C0B.toInt()))
        assertEquals(OmContrast.Black, OmContrast.bestOn(0xFFFAFAF8.toInt()))
        // 瑞士红配白字约 4.87:1，配黑字约 4.31:1 —— 应选白。
        assertEquals(OmContrast.White, OmContrast.bestOn(0xFFDA291C.toInt()))
    }

    @Test
    fun `paper scheme passes every contrast check`() {
        assertScheme(OmColorScheme.paper())
    }

    @Test
    fun `ink scheme passes every contrast check`() {
        assertScheme(OmColorScheme.ink())
    }

    @Test
    fun `spectrum tiles always ship eight pairs`() {
        assertEquals(8, OmColorScheme.paper().tiles.spectrum.size)
        assertEquals(8, OmColorScheme.ink().tiles.spectrum.size)
    }

    @Test
    fun `custom accents stay visible in both themes`() {
        // 含浅黄、青色这类「过浅」输入：ensureVisible 会把它们拉回 3:1 以内。
        val accents = listOf(0xFF1B4FD6, 0xFF0F7A4A, 0xFF6A3FA0, 0xFFFFD700, 0xFF00E5FF, 0xFF333333)
        accents.forEach { argb ->
            val accent = Color(argb)
            assertScheme(OmColorScheme.paper(accent))
            assertScheme(OmColorScheme.ink(accent))
        }
    }

    @Test
    fun `default accent is left untouched`() {
        assertEquals(OmColorScheme.DefaultAccent, OmColorScheme.paper().accent)
        assertEquals(OmColorScheme.DarkAccent, OmColorScheme.ink().accent)
    }

    private fun assertScheme(scheme: OmColorScheme) {
        val failures = scheme.contrastChecks().mapNotNull { check ->
            val ratio = OmContrast.ratio(check.foreground.toArgbInt(), check.background.toArgbInt())
            if (ratio + 1e-9 < check.minRatio) {
                "%s：%.2f:1 < %.1f:1".format(check.name, ratio, check.minRatio)
            } else {
                null
            }
        }
        assertTrue(
            "对比度不达标（${if (scheme.isDark) "Ink" else "Paper"}，accent=${scheme.accent}）：\n" +
                failures.joinToString("\n"),
            failures.isEmpty(),
        )
    }
}
