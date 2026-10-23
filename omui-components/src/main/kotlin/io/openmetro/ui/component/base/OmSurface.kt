package io.openmetro.ui.component.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.openmetro.ui.core.theme.OmTheme

/**
 * 纯色容器。没有 elevation 参数——阴影不存在于这套语言里。
 *
 * 层级由色块明度差建立：`background` → `surface` → `surfaceVariant` 依次「抬升」。
 */
@Composable
fun OmSurface(
    modifier: Modifier = Modifier,
    color: Color = OmTheme.colors.surface,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.background(color), content = content)
}
