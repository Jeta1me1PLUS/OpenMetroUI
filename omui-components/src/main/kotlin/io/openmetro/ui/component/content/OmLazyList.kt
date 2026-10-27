package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmDivider

/**
 * 懒加载列表：只在需要时组合可见项。
 *
 * 与 [OmList] 的分工很明确 —— 十几行的设置页用 [OmList] 就够，几百上千条的数据流用这个。
 * [OmList] 内部是 `Column` + `forEach`，条目再多也会全部组合，长列表下会明显掉帧。
 *
 * **不能放进 `OmScreen(scrollable = true)`**：`LazyColumn` 需要无界高度，
 * 外面再套一层 `verticalScroll` 会直接崩（`Infinity constraints`）。
 * 用这个组件时，页面容器应当不滚动，由它自己负责。
 *
 * 条目之间会自动插入 hairline，最后一行不画，与 [OmList] 一致。
 *
 * 需要稳定的 key 来做动画或保持状态时，直接用 `LazyColumn` + `items(key = …)` ——
 * 这里默认按位置索引，数据在中间插入时状态会跟着位置走。
 */
@Composable
fun <T> OmLazyList(
    items: List<T>,
    modifier: Modifier = Modifier,
    divided: Boolean = true,
    insetStart: Dp = 0.dp,
    itemContent: @Composable (T) -> Unit,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        itemsIndexed(items) { index, item ->
            itemContent(item)
            if (divided && index != items.lastIndex) {
                OmDivider(insetStart = insetStart)
            }
        }
    }
}
