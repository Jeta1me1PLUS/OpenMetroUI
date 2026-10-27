package io.openmetro.ui.core.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 磁贴配色模式。默认单色相；Spectrum 为 opt-in，默认关闭。 */
enum class OmTileMode { Monochrome, Spectrum }

/**
 * 磁贴的色调语义。组件只声明「这是一块什么分量的磁贴」，具体取哪个颜色由
 * 当前 [OmTileMode] 决定 —— 这样切换 Spectrum 时不需要改任何调用点。
 */
enum class OmTileTone {
    /** 浅中性块。 */
    Paper,

    /** 次浅中性块。 */
    Quiet,

    /** 反白块，页面里的节奏锚点。 */
    Ink,

    /** 强调色块。 */
    Accent,
}

val LocalOmColors = compositionLocalOf<OmColorScheme> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmTypography = compositionLocalOf<OmTypography> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmSpacing = compositionLocalOf<OmSpacing> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmGrid = compositionLocalOf<OmGrid> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmShapes = compositionLocalOf<OmShapes> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmMotion = compositionLocalOf<OmMotion> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

val LocalOmTileMode = compositionLocalOf<OmTileMode> {
    error("未找到 OmTheme：请在内容外层包一层 OmTheme { … }")
}

/**
 * 主题入口。所有 token 通过细粒度 CompositionLocal 下发，只改颜色时不会让
 * 只读排版的组件重组。
 *
 * 刻意不开放圆角、阴影、间距的覆盖 —— 这些是硬约束，开放就等于放弃视觉语言。
 *
 * @param darkTheme 默认跟随系统。Paper（浅色）是默认主题。
 * @param accent 覆盖强调色，`onAccent` 会自动取黑/白中对比度更高者并校验。
 * @param colorScheme 直接给完整配色表时，[accent] 与 [darkTheme] 不再参与推导。
 * @param tileMode Spectrum 模式会让磁贴使用八色组，默认关闭。
 * @param grid 覆盖栏网格；默认按当前窗口宽度推导。
 * @param paintBackground 是否顺带铺一层 `background` 底色。默认 true —— 否则深色主题下
 *   会露出窗口的浅色底，表现为「切了深色但四周还是白的」。
 *   **局部嵌套主题时传 false**：这里用的是 `fillMaxSize`，在无界高度（如 `verticalScroll`
 *   内部）会抛约束异常，此时应当只把它当 token 提供者。
 */
@Composable
fun OmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: Color? = null,
    colorScheme: OmColorScheme? = null,
    typography: OmTypography = OmTypography.System,
    spacing: OmSpacing = OmSpacing.Default,
    shapes: OmShapes = OmShapes.Default,
    motion: OmMotion = OmMotion.Default,
    tileMode: OmTileMode = OmTileMode.Monochrome,
    grid: OmGrid? = null,
    strings: OmStrings? = null,
    paintBackground: Boolean = true,
    content: @Composable () -> Unit,
) {
    val resolvedAccent = accent ?: if (darkTheme) OmColorScheme.DarkAccent else OmColorScheme.DefaultAccent
    val colors = colorScheme ?: remember(darkTheme, resolvedAccent) {
        OmColorScheme.of(darkTheme, resolvedAccent)
    }

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    val widthDp: Dp = with(density) { windowInfo.containerSize.width.toDp() }
    val resolvedGrid = grid ?: remember(widthDp) {
        if (widthDp > 0.dp) OmGrid.forWidth(widthDp) else OmGrid.Compact
    }

    // 跟随系统的「动画时长缩放」：设为 0 时所有动效瞬时完成
    val context = LocalContext.current
    val animatorScale = remember(context) {
        runCatching {
            android.provider.Settings.Global.getFloat(
                context.contentResolver,
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            )
        }.getOrDefault(1f)
    }
    val effectiveMotion = remember(motion, animatorScale) { motion.scaledBy(animatorScale) }

    CompositionLocalProvider(
        LocalOmColors provides colors,
        LocalOmTypography provides typography,
        LocalOmSpacing provides spacing,
        LocalOmGrid provides resolvedGrid,
        LocalOmShapes provides shapes,
        LocalOmMotion provides effectiveMotion,
        LocalOmTileMode provides tileMode,
        LocalOmStrings provides (strings ?: remember { OmStrings.forSystemLocale() }),
    ) {
        if (paintBackground) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background),
                contentAlignment = Alignment.TopStart,
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

/** 聚合访问入口，避免调用点满屏 CompositionLocal。 */
object OmTheme {

    val colors: OmColorScheme
        @Composable @ReadOnlyComposable get() = LocalOmColors.current

    val typography: OmTypography
        @Composable @ReadOnlyComposable get() = LocalOmTypography.current

    val spacing: OmSpacing
        @Composable @ReadOnlyComposable get() = LocalOmSpacing.current

    val grid: OmGrid
        @Composable @ReadOnlyComposable get() = LocalOmGrid.current

    val shapes: OmShapes
        @Composable @ReadOnlyComposable get() = LocalOmShapes.current

    val motion: OmMotion
        @Composable @ReadOnlyComposable get() = LocalOmMotion.current

    val tileMode: OmTileMode
        @Composable @ReadOnlyComposable get() = LocalOmTileMode.current
}
