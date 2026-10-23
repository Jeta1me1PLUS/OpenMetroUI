package io.openmetro.ui.component.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.openmetro.ui.core.theme.OmTheme

/**
 * 页面容器：负责页边距、正文最大宽度（约 72 字符）与可选的整页滚动。
 *
 * 不替调用方决定滚动方式以外的布局：顶部栏、底部栏由外层骨架提供。
 *
 * @param scrollable 整页垂直滚动。内容里有 `LazyColumn` 时必须保持 false，
 *   两者嵌套会因高度约束无界而崩溃。
 * @param scrollState 需要让页头随滚动收缩时，把同一个 state 交给 `OmCollapsingTopBar`。
 * @param applyInsets 是否自动避开状态栏与导航栏。默认 true —— 之前不做这件事，
 *   每个页面都要自己写 `statusBarsPadding()`，漏一处就被状态栏压住。
 *   如果骨架的外层已经统一处理过 insets，这里传 false 以免双倍间距。
 */
@Composable
fun OmScreen(
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    scrollState: ScrollState = rememberScrollState(),
    applyInsets: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val grid = OmTheme.grid

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (applyInsets) {
                    Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = grid.maxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = grid.margin)
                .then(
                    if (scrollable) {
                        Modifier.verticalScroll(scrollState)
                    } else {
                        Modifier
                    },
                ),
            content = content,
        )
    }
}
