package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 10 个字阶。字阶不是等比模数：UI 段约 1.2 的压缩阶梯，Display 段跃升 1.33 倍，
 * 用跨度而非均匀梯度制造层级。行高全部是 4sp 的整数倍，保证基线落在 4dp 网格上。
 *
 * 字重只用 300 / 400 / 600 三档。
 */
@Immutable
data class OmTypography(
    val displayXl: TextStyle,
    val displayL: TextStyle,
    val displayM: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val subtitle: TextStyle,
    val bodyL: TextStyle,
    val bodyM: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
) {
    /** 换成宿主提供的字体族（如 Inter + 思源黑体），字号与行高保持不变。 */
    fun withFontFamily(fontFamily: FontFamily): OmTypography = copy(
        displayXl = displayXl.copy(fontFamily = fontFamily),
        displayL = displayL.copy(fontFamily = fontFamily),
        displayM = displayM.copy(fontFamily = fontFamily),
        headline = headline.copy(fontFamily = fontFamily),
        title = title.copy(fontFamily = fontFamily),
        subtitle = subtitle.copy(fontFamily = fontFamily),
        bodyL = bodyL.copy(fontFamily = fontFamily),
        bodyM = bodyM.copy(fontFamily = fontFamily),
        caption = caption.copy(fontFamily = fontFamily),
        label = label.copy(fontFamily = fontFamily),
    )

    companion object {
        /**
         * 关闭字体额外行距。瑞士排版要求文本块高度可预测、基线可对齐，
         * 平台默认的 font padding 会让实际行高偏离 token。
         */
        private val NoFontPadding = PlatformTextStyle(includeFontPadding = false)

        private fun style(
            size: Int,
            lineHeight: Int,
            weight: FontWeight,
            tracking: Double,
        ): TextStyle = TextStyle(
            fontSize = size.sp,
            lineHeight = lineHeight.sp,
            fontWeight = weight,
            letterSpacing = tracking.sp,
            platformStyle = NoFontPadding,
        )

        /** 跟随系统字体。库不打包任何字体文件。 */
        val System: OmTypography = OmTypography(
            displayXl = style(64, 68, FontWeight.Light, -2.0),
            displayL = style(48, 52, FontWeight.Light, -1.5),
            displayM = style(36, 40, FontWeight.Light, -1.0),
            headline = style(28, 32, FontWeight.Normal, -0.5),
            title = style(22, 28, FontWeight.SemiBold, -0.25),
            subtitle = style(18, 24, FontWeight.Normal, 0.0),
            bodyL = style(16, 24, FontWeight.Normal, 0.15),
            bodyM = style(14, 20, FontWeight.Normal, 0.25),
            caption = style(12, 16, FontWeight.Normal, 0.4),
            label = style(11, 16, FontWeight.SemiBold, 1.5),
        )
    }
}
