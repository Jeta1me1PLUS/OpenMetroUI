package io.openmetro.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.openmetro.ui.core.theme.OmTheme

enum class OmCardVariant {
    /** `surfaceVariant` 底色，最常用。 */
    Flat,

    /** 透明底 + 1dp 描边。 */
    Outlined,

    /** 反白块。内部文字需自行使用 `OmTheme.colors.onInverse`。 */
    Ink,
}

/**
 * 直角卡片容器。没有 elevation —— 层级只由底色明度与描边表达。
 *
 * 与磁贴的分工：磁贴是**可点的一等信息块**（导航/入口），卡片是**内容的容器**
 * （一段文字、一个表单）。两者都是直角块面，但磁贴强调色，卡片强调留白。
 */
@Composable
fun OmCard(
    modifier: Modifier = Modifier,
    variant: OmCardVariant = OmCardVariant.Flat,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = OmTheme.colors
    val shapes = OmTheme.shapes

    val background = when (variant) {
        OmCardVariant.Flat -> colors.surfaceVariant
        OmCardVariant.Outlined -> Color.Transparent
        OmCardVariant.Ink -> colors.inverseSurface
    }
    val border = if (variant == OmCardVariant.Outlined) {
        Modifier.border(width = shapes.hairline, color = colors.line)
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .background(background)
            .then(border)
            .padding(OmTheme.spacing.space4),
        content = content,
    )
}
