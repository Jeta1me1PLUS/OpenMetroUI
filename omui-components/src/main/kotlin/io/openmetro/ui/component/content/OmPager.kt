package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openmetro.ui.core.theme.OmTheme

/**
 * 全出血横滑轮播（分页吸附）。
 *
 * 用 foundation 自带的 `HorizontalPager`，不引 Material 的 `Pager`/`TabRow`。
 * 手势、吸附、无障碍由 foundation 处理；这里只固定视觉参数。
 *
 * 页码指示器是独立的 [OmPagerIndicator]：不是所有轮播都需要指示器，
 * 强行绑定反而限制用法。
 */
@Composable
fun OmPager(
    pageCount: Int,
    modifier: Modifier = Modifier,
    state: PagerState = rememberPagerState(pageCount = { pageCount }),
    content: @Composable (Int) -> Unit,
) {
    HorizontalPager(
        state = state,
        modifier = modifier,
    ) { page ->
        content(page)
    }
}

/**
 * 页码指示器：一排直角线段，当前页用 accent。不做圆点。
 */
@Composable
fun OmPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(pageCount) { index ->
            Box(
                Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(if (index == currentPage) colors.accent else colors.line),
            )
        }
    }
}
