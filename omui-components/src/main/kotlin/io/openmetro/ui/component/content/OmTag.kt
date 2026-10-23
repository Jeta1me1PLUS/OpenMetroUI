package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 标签 / 筛选片：直角、28dp 高。
 *
 * 未选中是 1dp 描边，选中是 accent 实心。不做圆角胶囊 —— 胶囊是 Material 的
 * Chip 语言，和这套块面不兼容。
 *
 * @param onClick 提供时可点（筛选片），否则只作展示（标签）
 * @param onRemove 提供时尾部出现 ×，可单独删除
 */
@Composable
fun OmTag(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val shapes = OmTheme.shapes

    val background = if (selected) colors.accent else Color.Transparent
    val contentColor = if (selected) colors.onAccent else colors.textPrimary
    val border = if (selected) {
        Modifier
    } else {
        Modifier.border(width = shapes.hairline, color = colors.lineStrong)
    }

    val interaction = if (onClick != null) {
        Modifier.omFocusRing().omClickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .height(TagHeight)
            .then(border)
            .background(background)
            .then(interaction)
            .padding(horizontal = spacing.space3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmText(
            text = text,
            style = OmTheme.typography.caption,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        if (onRemove != null) {
            Spacer(Modifier.width(spacing.space1))
            // 命中区取标签的完整高度（28dp），比图标本身大四倍。
            // 仍低于 48dp 下限，这是 docs/00 里登记过的例外：标签内的次级操作。
            Box(
                modifier = Modifier
                    .size(TagHeight)
                    .omClickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                OmIcon(
                    imageVector = OmIcons.Close,
                    contentDescription = "移除",
                    size = 14.dp,
                    tint = contentColor,
                )
            }
        }
    }
}

private val TagHeight = 28.dp
