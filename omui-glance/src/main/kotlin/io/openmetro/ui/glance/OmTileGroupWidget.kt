package io.openmetro.ui.glance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.openmetro.ui.core.theme.OmTileMode
import io.openmetro.ui.core.theme.OmTileTone

/** 磁贴组里的一格。 */
@Immutable
data class OmWidgetTile(
    val value: String,
    val label: String,
    val tone: OmTileTone = OmTileTone.Ink,
)

/**
 * 磁贴组：一行并排 2–4 块，块与块之间留 4dp 缝隙。
 *
 * 缝隙露的是 launcher 的背景，而不是我们自己画的分隔线 —— 小组件没有页面底色，
 * 只有「块」本身。这与 App 内 `OmTileGrid` 的 gutter 是同一种做法。
 *
 * 超过 4 块会挤压数字，调用方应自行截断。
 */
@Composable
fun OmTileGroupWidget(
    tiles: List<OmWidgetTile>,
    modifier: GlanceModifier = GlanceModifier,
    dark: Boolean = false,
    mode: OmTileMode = OmTileMode.Monochrome,
    onClick: Action? = null,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        verticalAlignment = Alignment.Vertical.Top,
    ) {
        tiles.forEachIndexed { index, tile ->
            if (index > 0) {
                Spacer(GlanceModifier.width(4.dp))
            }

            val (surface, onSurface) = omGlanceTile(dark, tile.tone, mode)

            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .background(surface)
                    .then(
                        if (onClick != null) GlanceModifier.clickable(onClick) else GlanceModifier,
                    )
                    .padding(10.dp),
                verticalAlignment = Alignment.Vertical.Bottom,
                horizontalAlignment = Alignment.Horizontal.Start,
            ) {
                Text(
                    text = tile.value,
                    style = TextStyle(
                        color = onSurface,
                        fontSize = 24.sp,
                        fontWeight = androidx.glance.text.FontWeight.Medium,
                    ),
                    maxLines = 1,
                )
                Spacer(GlanceModifier.height(4.dp))
                Text(
                    text = tile.label.uppercase(),
                    style = TextStyle(
                        color = onSurface,
                        fontSize = 10.sp,
                        fontWeight = androidx.glance.text.FontWeight.Normal,
                    ),
                    maxLines = 2,
                )
            }
        }
    }
}

/** 供调用方按条数挑选色调，保持一组里深浅错开。 */
fun omWidgetTones(count: Int): List<OmTileTone> = when (count) {
    1 -> listOf(OmTileTone.Accent)
    2 -> listOf(OmTileTone.Accent, OmTileTone.Ink)
    3 -> listOf(OmTileTone.Accent, OmTileTone.Paper, OmTileTone.Ink)
    else -> listOf(OmTileTone.Accent, OmTileTone.Paper, OmTileTone.Quiet, OmTileTone.Ink)
}
