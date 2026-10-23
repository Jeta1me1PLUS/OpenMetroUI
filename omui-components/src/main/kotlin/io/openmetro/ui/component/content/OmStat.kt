package io.openmetro.ui.component.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme

/**
 * 数据卡：一个大数字 + 一行全大写标签 + 可选的变化量。
 *
 * 数字用 `displayL`（48sp Light），因为它是这张卡唯一的主信息。
 * 不画边框、不画底色 —— 放在网格里时，留白本身就是分隔。
 *
 * @param delta 变化量，如 `+12%`；正负只影响颜色，不做箭头图标
 */
@Composable
fun OmStat(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    delta: String? = null,
    deltaPositive: Boolean = true,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    Column(modifier = modifier) {
        OmText(
            text = value,
            style = typography.displayL,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(spacing.space1))
        OmText(
            text = label,
            style = typography.caption,
            color = colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (delta != null) {
            Spacer(Modifier.height(spacing.space1))
            OmText(
                text = delta,
                style = typography.caption,
                color = if (deltaPositive) colors.accent else colors.danger,
                maxLines = 1,
            )
        }
    }
}
