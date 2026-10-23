package io.openmetro.ui.component.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 结构线。1dp 用于列表与表格分隔，2dp 用于区块划分。
 *
 * 不用 `BorderStroke` 或 `drawLine`：用一块有高度的色块，天然对齐到像素网格，
 * 也不会因抗锯齿变成 1.5dp 的灰线。
 */
@Composable
fun OmDivider(
    modifier: Modifier = Modifier,
    strong: Boolean = false,
    insetStart: Dp = 0.dp,
    insetEnd: Dp = 0.dp,
) {
    val shapes = OmTheme.shapes
    val colors = OmTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = insetStart, end = insetEnd)
            .height(if (strong) shapes.emphasis else shapes.hairline)
            .background(if (strong) colors.lineStrong else colors.line),
    )
}
