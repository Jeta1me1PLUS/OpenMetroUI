package io.openmetro.ui.glance

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.LocalSize
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.openmetro.ui.core.theme.OmTileMode
import io.openmetro.ui.core.theme.OmTileTone

/**
 * 磁贴小组件：一块纯色方块 + 左上角小标签 + 底部大标题。
 *
 * 与 App 内的 `OmTile` 共用同一套 token 与取色规则，但**不是同一个组件** ——
 * 小组件跑在 Launcher 的 RemoteViews 管线里，只能用 Glance 的有限组件集。
 * 这里刻意只保留磁贴最核心的三样：底色、标签、标题，不加图片与图标。
 *
 * 字号按宿主给的尺寸自适应：窄于 110dp 用 18sp，窄于 160dp 用 22sp，否则 28sp。
 * 小组件被拖大拖小时由 Launcher 重新测量，这个判断会跟着变。
 *
 * @param onClick 传 `actionStartActivity` 之类的动作；不传则整块不可点
 */
@Composable
fun OmTileWidget(
    title: String,
    modifier: GlanceModifier = GlanceModifier,
    label: String? = null,
    tone: OmTileTone = OmTileTone.Ink,
    dark: Boolean = false,
    mode: OmTileMode = OmTileMode.Monochrome,
    onClick: Action? = null,
) {
    val (surface, onSurface) = omGlanceTile(dark = dark, tone = tone, mode = mode)
    val size = LocalSize.current
    val titleSize = when {
        size.width < 110.dp -> 18.sp
        size.width < 160.dp -> 22.sp
        else -> 28.sp
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surface)
            .then(if (onClick != null) GlanceModifier.clickable(onClick) else GlanceModifier)
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.Bottom,
        horizontalAlignment = Alignment.Horizontal.Start,
    ) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                style = TextStyle(
                    color = onSurface,
                    fontSize = 11.sp,
                    fontWeight = androidx.glance.text.FontWeight.Normal,
                ),
                maxLines = 1,
            )
            Spacer(GlanceModifier.height(4.dp))
        }
        Text(
            text = title,
            style = TextStyle(
                color = onSurface,
                fontSize = titleSize,
                fontWeight = androidx.glance.text.FontWeight.Medium,
            ),
            maxLines = 2,
        )
    }
}

/**
 * 双数值磁贴：一个主数值 + 一行说明，用于汇率、金价、步数这类「数字比名字重要」的场景。
 *
 * 数字用 36sp，说明用 11sp —— 与 `OmStat` 的信息层级一致。
 */
@Composable
fun OmValueTileWidget(
    value: String,
    label: String,
    modifier: GlanceModifier = GlanceModifier,
    tone: OmTileTone = OmTileTone.Ink,
    dark: Boolean = false,
    mode: OmTileMode = OmTileMode.Monochrome,
    onClick: Action? = null,
) {
    val (surface, onSurface) = omGlanceTile(dark = dark, tone = tone, mode = mode)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surface)
            .then(if (onClick != null) GlanceModifier.clickable(onClick) else GlanceModifier)
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.Bottom,
        horizontalAlignment = Alignment.Horizontal.Start,
    ) {
        Text(
            text = value,
            style = TextStyle(
                color = onSurface,
                fontSize = 36.sp,
                fontWeight = androidx.glance.text.FontWeight.Medium,
            ),
            maxLines = 1,
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = TextStyle(
                color = onSurface,
                fontSize = 11.sp,
                fontWeight = androidx.glance.text.FontWeight.Normal,
            ),
            maxLines = 1,
        )
    }
}
