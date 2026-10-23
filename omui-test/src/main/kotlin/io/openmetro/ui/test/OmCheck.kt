package io.openmetro.ui.test

import androidx.compose.ui.unit.Dp
import io.openmetro.ui.core.theme.OmColorScheme
import io.openmetro.ui.core.theme.OmContrast
import io.openmetro.ui.core.theme.OmGrid
import io.openmetro.ui.core.theme.OmSpacing
import io.openmetro.ui.core.theme.OmTypography
import io.openmetro.ui.core.theme.contrastChecks
import java.util.Locale

/**
 * 设计约束的自动校验。
 *
 * 这里校验的都是**规范里写死、但编译器管不到**的东西：对比度、4dp 网格、
 * 触摸目标、字阶与行高。宿主换掉 accent 之后跑一遍 `requireContrast`，
 * 就能知道自己的配色是否还站得住。
 *
 * 两种 API 形态：
 * - `*Violations` 返回问题清单，适合在报告里聚合展示
 * - `require*` 直接抛 [AssertionError]，适合放进单元测试
 */
object OmCheck {

    const val GridUnit: Float = 4f
    const val MinTouchTarget: Float = 48f

    /** 字阶表允许的字号，见 docs/00-design-language.md。 */
    val AllowedFontSizes: Set<Float> = setOf(11f, 12f, 14f, 16f, 18f, 22f, 28f, 36f, 48f, 64f)

    /** 只允许三档字重。 */
    val AllowedFontWeights: Set<Int> = setOf(300, 400, 600)

    // ── 对比度 ──────────────────────────────────────────────────

    fun contrastViolations(scheme: OmColorScheme): List<String> =
        scheme.contrastChecks().mapNotNull { check ->
            val ratio = OmContrast.ratio(check.foreground, check.background)
            if (ratio + 1e-9 < check.minRatio) {
                String.format(
                    Locale.ROOT,
                    "%s：%.2f:1 < %.1f:1",
                    check.name,
                    ratio,
                    check.minRatio,
                )
            } else {
                null
            }
        }

    fun requireContrast(scheme: OmColorScheme) {
        val violations = contrastViolations(scheme)
        if (violations.isNotEmpty()) {
            val themeName = if (scheme.isDark) "Ink" else "Paper"
            throw AssertionError("对比度不达标（$themeName）：\n" + violations.joinToString("\n"))
        }
    }

    // ── 4dp 网格 ────────────────────────────────────────────────

    // Dp 是 value class，Kotlin 不允许它作为 vararg 参数，因此统一收 List。
    fun gridViolations(values: List<Dp>): List<String> =
        values.mapIndexedNotNull { index, value ->
            if (value.value % GridUnit != 0f) {
                String.format(
                    Locale.ROOT,
                    "第 %d 个尺寸 %.1fdp 不是 4dp 的倍数",
                    index + 1,
                    value.value,
                )
            } else {
                null
            }
        }

    fun requireOnGrid(values: List<Dp>) {
        val violations = gridViolations(values)
        if (violations.isNotEmpty()) {
            throw AssertionError("尺寸不符合 4dp 网格：\n" + violations.joinToString("\n"))
        }
    }

    fun gridConfigViolations(grid: OmGrid): List<String> = buildList {
        if (grid.margin.value % GridUnit != 0f) {
            add("margin ${grid.margin.value}dp 不是 4dp 的倍数")
        }
        if (grid.gutter.value % GridUnit != 0f) {
            add("gutter ${grid.gutter.value}dp 不是 4dp 的倍数")
        }
        if (grid.columns <= 0) {
            add("columns 必须为正数，当前 ${grid.columns}")
        }
    }

    // ── 触摸目标 ────────────────────────────────────────────────

    fun touchTargetViolation(size: Dp): String? =
        if (size.value < MinTouchTarget) {
            String.format(
                Locale.ROOT,
                "触摸目标 %.1fdp 小于 48dp 下限",
                size.value,
            )
        } else {
            null
        }

    fun requireTouchTarget(size: Dp) {
        touchTargetViolation(size)?.let { throw AssertionError(it) }
    }

    // ── 排版 ────────────────────────────────────────────────────

    fun typographyViolations(typography: OmTypography): List<String> {
        val styles = listOf(
            "display.xl" to typography.displayXl,
            "display.l" to typography.displayL,
            "display.m" to typography.displayM,
            "headline" to typography.headline,
            "title" to typography.title,
            "subtitle" to typography.subtitle,
            "body.l" to typography.bodyL,
            "body.m" to typography.bodyM,
            "caption" to typography.caption,
            "label" to typography.label,
        )

        return styles.flatMap { (name, style) ->
            buildList {
                val fontSize = style.fontSize.value
                if (fontSize !in AllowedFontSizes) {
                    add(String.format(Locale.ROOT, "%s 字号 %.1fsp 不在字阶表内", name, fontSize))
                }

                val lineHeight = style.lineHeight.value
                when {
                    lineHeight.isNaN() -> add("$name 未设置行高")
                    lineHeight % GridUnit != 0f -> add(
                        String.format(
                            Locale.ROOT,
                            "%s 行高 %.1fsp 不是 4sp 的倍数",
                            name,
                            lineHeight,
                        ),
                    )
                }

                val weight = style.fontWeight?.weight
                if (weight == null || weight !in AllowedFontWeights) {
                    add("$name 字重 $weight 不在 300/400/600 内")
                }
            }
        }
    }

    fun requireTypography(typography: OmTypography) {
        val violations = typographyViolations(typography)
        if (violations.isNotEmpty()) {
            throw AssertionError("排版不符合规范：\n" + violations.joinToString("\n"))
        }
    }

    // ── 间距 ────────────────────────────────────────────────────

    fun spacingViolations(spacing: OmSpacing): List<String> {
        val values = listOf(
            "space1" to spacing.space1,
            "space2" to spacing.space2,
            "space3" to spacing.space3,
            "space4" to spacing.space4,
            "space6" to spacing.space6,
            "space8" to spacing.space8,
            "space12" to spacing.space12,
            "space16" to spacing.space16,
            "space24" to spacing.space24,
        )

        return values.mapNotNull { (name, value) ->
            if (value.value % GridUnit != 0f) {
                String.format(Locale.ROOT, "%s = %.1fdp 不是 4dp 的倍数", name, value.value)
            } else {
                null
            }
        }
    }

    fun requireSpacing(spacing: OmSpacing) {
        val violations = spacingViolations(spacing)
        if (violations.isNotEmpty()) {
            throw AssertionError("间距不符合 4dp 网格：\n" + violations.joinToString("\n"))
        }
    }
}
