package io.openmetro.ui.component.base

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 单色几何图标。默认 24dp、随文本主色着色。
 *
 * 不用 Material 的 `Icon` 组件（那需要引入 material 依赖）；这里直接走
 * `Image` + `ColorFilter.tint`，行为等价且零额外依赖。
 */
@Composable
fun OmIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = OmTheme.colors.textPrimary,
) {
    Image(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}
