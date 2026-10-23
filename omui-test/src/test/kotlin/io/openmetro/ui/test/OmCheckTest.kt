package io.openmetro.ui.test

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.openmetro.ui.core.theme.OmColorScheme
import io.openmetro.ui.core.theme.OmSpacing
import io.openmetro.ui.core.theme.OmTypography
import org.junit.Assert.assertTrue
import org.junit.Test

class OmCheckTest {

    // ── 对比度 ──────────────────────────────────────────────────

    @Test
    fun `default schemes pass contrast`() {
        OmCheck.requireContrast(OmColorScheme.paper())
        OmCheck.requireContrast(OmColorScheme.ink())
        assertTrue(OmCheck.contrastViolations(OmColorScheme.paper()).isEmpty())
    }

    @Test
    fun `low contrast scheme is reported`() {
        val broken = OmColorScheme.paper().let { it.copy(textSecondary = it.background) }
        val violations = OmCheck.contrastViolations(broken)
        assertTrue("应当报出 textSecondary 的对比度问题", violations.any { it.contains("textSecondary") })
    }

    @Test(expected = AssertionError::class)
    fun `requireContrast throws on broken scheme`() {
        val broken = OmColorScheme.paper().let { it.copy(textPrimary = it.background) }
        OmCheck.requireContrast(broken)
    }

    // ── 4dp 网格 ────────────────────────────────────────────────

    @Test
    fun `multiples of four pass the grid check`() {
        OmCheck.requireOnGrid(listOf(4.dp, 8.dp, 16.dp, 48.dp, 96.dp))
    }

    @Test
    fun `non multiples are reported`() {
        val violations = OmCheck.gridViolations(listOf(16.dp, 15.dp))
        assertTrue(violations.size == 1)
        assertTrue(violations.first().contains("15.0dp"))
    }

    @Test(expected = AssertionError::class)
    fun `requireOnGrid throws`() {
        OmCheck.requireOnGrid(listOf(15.dp))
    }

    // ── 触摸目标 ────────────────────────────────────────────────

    @Test
    fun `48dp touch target passes`() {
        OmCheck.requireTouchTarget(48.dp)
    }

    @Test(expected = AssertionError::class)
    fun `touch target below 48dp throws`() {
        OmCheck.requireTouchTarget(40.dp)
    }

    // ── 排版 ────────────────────────────────────────────────────

    @Test
    fun `default typography passes`() {
        OmCheck.requireTypography(OmTypography.System)
    }

    @Test
    fun `off scale font size is reported`() {
        val broken = OmTypography.System.copy(
            bodyL = OmTypography.System.bodyL.copy(fontSize = 15.sp),
        )
        assertTrue(OmCheck.typographyViolations(broken).any { it.contains("body.l") })
    }

    @Test
    fun `off grid line height is reported`() {
        val broken = OmTypography.System.copy(
            bodyL = OmTypography.System.bodyL.copy(lineHeight = 25.sp),
        )
        assertTrue(OmCheck.typographyViolations(broken).any { it.contains("行高") })
    }

    // ── 间距 ────────────────────────────────────────────────────

    @Test
    fun `default spacing passes`() {
        OmCheck.requireSpacing(OmSpacing.Default)
    }

    @Test(expected = AssertionError::class)
    fun `off grid spacing throws`() {
        OmCheck.requireSpacing(OmSpacing.Default.copy(space4 = 15.dp))
    }
}
