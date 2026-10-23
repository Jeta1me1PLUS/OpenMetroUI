package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 列表 - 详情双栏。
 *
 * 宽度达到 [breakpoint] 时左右并排，否则只显示列表 ——
 * 窄屏下的「详情」应当是新页面，而不是挤在同一个屏里。
 *
 * 分隔用 1dp 结构线而不是间距：两栏都是内容区，需要明确的边界。
 */
@Composable
fun OmResponsivePane(
    list: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    breakpoint: Dp = 840.dp,
    listWidth: Dp = 320.dp,
    detail: @Composable () -> Unit,
) {
    val colors = OmTheme.colors
    val hairline = OmTheme.shapes.hairline

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        if (maxWidth >= breakpoint) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.width(listWidth).fillMaxHeight()) {
                    list()
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(hairline)
                        .background(colors.line),
                )
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    detail()
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                list()
            }
        }
    }
}
