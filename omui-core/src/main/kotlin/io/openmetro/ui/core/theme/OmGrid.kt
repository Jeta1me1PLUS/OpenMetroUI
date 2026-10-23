package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class OmWidthClass { Compact, Medium, Expanded, Large }

/**
 * 栏网格。组件按「跨栏数」定位，不写死宽度。
 *
 * 栏宽 = (可用宽度 − 2×页边距 − (栏数−1)×栏间距) / 栏数，运行时计算。
 */
@Immutable
data class OmGrid(
    val widthClass: OmWidthClass,
    val columns: Int,
    val margin: Dp,
    val gutter: Dp,
    /** 正文单行字符数上限（约 72 字符）对应的最大内容宽度。 */
    val maxContentWidth: Dp = DefaultMaxContentWidth,
) {
    fun contentWidth(availableWidth: Dp): Dp =
        (availableWidth - margin * 2).coerceAtLeast(0.dp)

    fun columnWidth(availableWidth: Dp): Dp {
        val inner = contentWidth(availableWidth) - gutter * (columns - 1)
        return (inner / columns).coerceAtLeast(0.dp)
    }

    /** 跨 [span] 栏的宽度，含中间的栏间距。 */
    fun spanWidth(availableWidth: Dp, span: Int): Dp {
        val safeSpan = span.coerceIn(1, columns)
        return columnWidth(availableWidth) * safeSpan + gutter * (safeSpan - 1)
    }

    companion object {
        val DefaultMaxContentWidth: Dp = 640.dp

        val Compact: OmGrid = OmGrid(OmWidthClass.Compact, columns = 4, margin = 16.dp, gutter = 16.dp)
        val Medium: OmGrid = OmGrid(OmWidthClass.Medium, columns = 8, margin = 24.dp, gutter = 16.dp)
        val Expanded: OmGrid = OmGrid(OmWidthClass.Expanded, columns = 12, margin = 32.dp, gutter = 24.dp)
        val Large: OmGrid = OmGrid(OmWidthClass.Large, columns = 12, margin = 48.dp, gutter = 24.dp)

        fun forWidth(width: Dp): OmGrid = when {
            width < 600.dp -> Compact
            width < 840.dp -> Medium
            width < 1200.dp -> Expanded
            else -> Large
        }
    }
}
