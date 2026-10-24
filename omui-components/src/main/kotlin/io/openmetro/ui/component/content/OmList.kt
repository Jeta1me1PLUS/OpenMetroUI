package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmDivider
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 数据驱动的列表：负责在项之间插入 hairline 分隔线，最后一项不画。
 *
 * 需要更自由的布局时，直接用 `Column` + [OmListItem] + [OmDivider] 组合，
 * 但那时分隔线要自己管。
 */
@Composable
fun <T> OmList(
    items: List<T>,
    modifier: Modifier = Modifier,
    divided: Boolean = true,
    insetStart: Dp = 0.dp,
    itemContent: @Composable (T) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            itemContent(item)
            if (divided && index != items.lastIndex) {
                OmDivider(insetStart = insetStart)
            }
        }
    }
}

/**
 * 列表项：主文本 22sp/600 + 可选次级文本，贴齐网格、无左图标缩进。
 *
 * 选中态 = 整行 `accentQuiet` 底色 + start 侧 3dp accent 竖条。竖条用 `drawBehind`
 * 画在容器左边界**之外**（也就是页边距里），既不占布局宽度、不引起文字跳动，
 * 也不会盖住文字。因此外层容器必须留出至少 3dp 的左边距（`OmScreen` 默认有 16dp）。
 *
 * 高度由内容决定，最小 56dp；分隔线由 [OmList] 负责，本组件不画。
 */
@Composable
fun OmListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    val interaction = if (onClick != null) {
        Modifier
            .omFocusRing()
            .omClickable(enabled = enabled, role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (selected) colors.accentQuiet else Color.Transparent)
            .drawBehind {
                if (selected) {
                    // 画到容器左边界之外（页边距里）：与文字留 7dp 呼吸，
                    // 同时文字保持 flush-left 不被盖住、也不会因选中而跳动
                    val barWidth = 3.dp.toPx()
                    drawRect(
                        color = colors.accent,
                        topLeft = Offset(x = -barWidth - 7.dp.toPx(), y = 0f),
                        size = Size(width = barWidth, height = size.height),
                    )
                }
            }
            .then(interaction)
            .heightIn(min = 56.dp)
            .padding(vertical = spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(spacing.space3))
        }

        Column(Modifier.weight(1f)) {
            OmText(
                text = title,
                style = typography.title,
                color = if (enabled) colors.textPrimary else colors.textDisabled,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Spacer(Modifier.height(spacing.space1))
                OmText(
                    text = subtitle,
                    style = typography.bodyM,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (trailing != null) {
            Spacer(Modifier.width(spacing.space3))
            trailing()
        }
    }
}
