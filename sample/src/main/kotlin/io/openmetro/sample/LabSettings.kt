package io.openmetro.sample

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import io.openmetro.ui.core.theme.OmTileMode

/**
 * Lab 里可实时调整的全局参数。
 *
 * 提升到 MainActivity，是因为它们要作用到**整个应用**：字号缩放改的是
 * `LocalDensity.fontScale`，accent 与磁贴模式改的是 `OmTheme` 的参数。
 */
@Immutable
data class LabSettings(
    val dark: Boolean = false,
    val accent: Color? = null,
    val tileMode: OmTileMode = OmTileMode.Monochrome,
    val fontScale: Float = 1f,
    val gridOverlay: Boolean = false,
    /** 强制从右往左排版，用来检查所有装饰是否跟着镜像。 */
    val rtl: Boolean = false,
)

/** 预设强调色。刻意只给中等偏深的色相 —— 过浅的会被 `ensureVisible` 自动拉深。 */
val LabAccents: List<Pair<String, Color>> = listOf(
    "RED" to Color(0xFFDA291C),
    "COBALT" to Color(0xFF1B4FD6),
    "FOREST" to Color(0xFF0F7A4A),
    "PLUM" to Color(0xFF6A3FA0),
    "OCHRE" to Color(0xFF9A5B00),
    "INK" to Color(0xFF31312E),
)
