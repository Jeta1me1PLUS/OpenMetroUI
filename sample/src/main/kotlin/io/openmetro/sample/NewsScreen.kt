package io.openmetro.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.content.OmList
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.navigation.OmCollapsingTopBar
import io.openmetro.ui.component.navigation.OmPivot
import io.openmetro.ui.component.tile.OmTile
import io.openmetro.ui.component.tile.OmTileGrid
import io.openmetro.ui.core.layout.omGridSpan
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileTone

private val Articles = listOf(
    Triple("网格是如何统治版面的", "从苏黎世到巴塞尔，一场关于秩序的争论", "08:20"),
    Triple("Metro 的磁贴为什么失败", "内容优先没错，错在把导航也做成了内容", "昨天"),
    Triple("直角与留白", "去掉装饰之后，剩下的才是设计", "周二"),
    Triple("一个强调色够不够", "当界面只剩一种颜色，层级该由什么承担", "周一"),
)

/**
 * Showcase 1：资讯流。
 *
 * 页头固定在顶部，随下方内容滚动从 48sp 收缩为 22sp —— 这是唯一一处
 * 「滚动位置影响视觉」的地方，用的也是两个离散状态而不是连续插值。
 */
@Composable
fun NewsScreen() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val grid = OmTheme.grid
    var tab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    // 这一页没用 OmScreen（需要固定页头 + 独立滚动），所以状态栏内边距自己处理
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.padding(horizontal = grid.margin)) {
            Spacer(Modifier.height(spacing.space2))
            OmCollapsingTopBar(
                title = "晨间简报",
                label = "TODAY / 今日",
                scrollState = scrollState,
            )
            Spacer(Modifier.height(spacing.space2))
            OmPivot(
                tabs = listOf("全部", "科技", "设计"),
                selectedIndex = tab,
                onSelect = { tab = it },
            )
            Spacer(Modifier.height(spacing.space4))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = grid.margin),
        ) {
            OmTileGrid(columns = 4, gutter = spacing.space2) {
                OmTile(
                    title = "收件箱",
                    modifier = Modifier.omGridSpan(2).aspectRatio(1f),
                    label = "3 条新消息",
                    tone = OmTileTone.Accent,
                )
                OmTile(
                    title = "设计周报",
                    modifier = Modifier.omGridSpan(2).aspectRatio(1f),
                    tone = OmTileTone.Paper,
                    backTitle = "本周 8 篇，已读 3 篇",
                )
                OmTile(
                    title = "阅读清单",
                    modifier = Modifier.omGridSpan(4).aspectRatio(2f),
                    label = "12 篇待读",
                    tone = OmTileTone.Ink,
                )
            }

            Spacer(Modifier.height(spacing.space8))
            OmText("最新", style = OmTheme.typography.headline)
            Spacer(Modifier.height(spacing.space2))

            OmList(items = Articles) { (title, subtitle, time) ->
                OmListItem(
                    title = title,
                    subtitle = subtitle,
                    trailing = {
                        OmText(
                            text = time,
                            style = OmTheme.typography.caption,
                            color = colors.textSecondary,
                        )
                    },
                    onClick = {},
                )
            }

            Spacer(Modifier.height(spacing.space12))
        }
    }
}
