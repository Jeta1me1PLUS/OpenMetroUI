package io.openmetro.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmDivider
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileColor
import java.util.Locale

/**
 * 把规范里的色板、字阶、间距变成能看的实物。
 * 任何 token 改动都会立刻在这里显形。
 */
@Composable
fun SpecScreen(lab: LabSettings, onLabChange: (LabSettings) -> Unit) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    OmScreen(scrollable = true) {
        Spacer(Modifier.height(spacing.space8))
        Header(
            dark = lab.dark,
            onToggleTheme = { onLabChange(lab.copy(dark = !lab.dark)) },
        )

        SpecSection("NEUTRAL / 中性阶") { NeutralRamp() }
        SpecSection("SEMANTIC / 语义色") { SemanticList() }
        SpecSection("TYPE SCALE / 字阶") { TypeScale() }
        SpecSection("SPACING / 间距") { SpacingRamp() }
        SpecSection("STROKE / 描边") { StrokeSamples() }
        SpecSection("TILE / 磁贴色") { TileSamples() }

        Spacer(Modifier.height(spacing.space16))
        OmText(
            text = "4dp 网格 · 零圆角 · 零阴影 · 单一强调色",
            style = OmTheme.typography.caption,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.space8))
    }
}

@Composable
private fun Header(dark: Boolean, onToggleTheme: () -> Unit) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    OmText("METRO × SWISS", style = OmTheme.typography.label, color = colors.textSecondary)
    Spacer(Modifier.height(spacing.space1))
    OmText("OpenMetroUI", style = OmTheme.typography.displayM)
    Spacer(Modifier.height(spacing.space2))
    OmText(
        text = "直角、无阴影、单一强调色、4dp 网格",
        style = OmTheme.typography.bodyM,
        color = colors.textSecondary,
    )
    Spacer(Modifier.height(spacing.space4))
    ThemeToggle(dark = dark, onClick = onToggleTheme)
}

@Composable
private fun ThemeToggle(dark: Boolean, onClick: () -> Unit) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                // 有意禁用默认 ripple：圆形水波是 Material 的语言。
                indication = null,
                onClick = onClick,
            )
            .background(colors.textPrimary)
            .padding(horizontal = spacing.space4, vertical = spacing.space3),
    ) {
        OmText(
            text = if (dark) "SWITCH TO PAPER" else "SWITCH TO INK",
            style = OmTheme.typography.label,
            color = colors.background,
        )
    }
}

@Composable
private fun ColumnScope.SpecSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    Spacer(Modifier.height(spacing.space8))
    OmDivider(strong = true)
    Spacer(Modifier.height(spacing.space2))
    OmText(title, style = OmTheme.typography.label, color = colors.textSecondary)
    Spacer(Modifier.height(spacing.space3))
    content()
}

@Composable
private fun ColumnScope.NeutralRamp() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
    ) {
        listOf(
            colors.background,
            colors.surfaceVariant,
            colors.line,
            colors.textDisabled,
            colors.textSecondary,
            colors.textPrimary,
        ).forEach { color ->
            Box(Modifier.weight(1f).fillMaxHeight().background(color))
        }
    }
    Spacer(Modifier.height(spacing.space2))
    OmText(
        text = "background → textPrimary",
        style = OmTheme.typography.caption,
        color = colors.textSecondary,
    )
}

@Composable
private fun ColumnScope.SemanticList() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    val rows = listOf(
        "accent" to colors.accent,
        "accentQuiet" to colors.accentQuiet,
        "danger" to colors.danger,
        "warning" to colors.warning,
        "success" to colors.success,
        "inverseSurface" to colors.inverseSurface,
        "line" to colors.line,
        "lineStrong" to colors.lineStrong,
    )

    rows.forEach { (name, color) ->
        Row(
            modifier = Modifier.padding(vertical = spacing.space1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(24.dp).background(color))
            Spacer(Modifier.width(spacing.space3))
            OmText(name, style = OmTheme.typography.bodyM)
            Spacer(Modifier.weight(1f))
            OmText(color.hex(), style = OmTheme.typography.caption, color = colors.textSecondary)
        }
    }
}

@Composable
private fun ColumnScope.TypeScale() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val type = OmTheme.typography

    val rows: List<Triple<String, Int, TextStyle>> = listOf(
        Triple("display.xl", 64, type.displayXl),
        Triple("display.l", 48, type.displayL),
        Triple("display.m", 36, type.displayM),
        Triple("headline", 28, type.headline),
        Triple("title", 22, type.title),
        Triple("subtitle", 18, type.subtitle),
        Triple("body.l", 16, type.bodyL),
        Triple("body.m", 14, type.bodyM),
        Triple("caption", 12, type.caption),
        Triple("label", 11, type.label),
    )

    rows.forEach { (token, size, style) ->
        OmText(
            text = "Aa 瑞士 Grid 42",
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        OmText(
            text = "$token · ${size}sp",
            style = OmTheme.typography.caption,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(spacing.space2))
    }
}

@Composable
private fun ColumnScope.SpacingRamp() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    val rows: List<Pair<String, Dp>> = listOf(
        "space1" to spacing.space1,
        "space2" to spacing.space2,
        "space3" to spacing.space3,
        "space4" to spacing.space4,
        "space6" to spacing.space6,
        "space8" to spacing.space8,
        "space12" to spacing.space12,
        "space16" to spacing.space16,
        "space24" to spacing.space24,
    )

    rows.forEach { (name, value) ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(value)
                    .height(16.dp)
                    .background(colors.accent),
            )
            Spacer(Modifier.width(spacing.space3))
            OmText(
                text = "$name · ${value.value.toInt()}dp",
                style = OmTheme.typography.caption,
                color = colors.textSecondary,
            )
        }
        Spacer(Modifier.height(spacing.space1))
    }
}

@Composable
private fun ColumnScope.StrokeSamples() {
    val colors = OmTheme.colors
    val shapes = OmTheme.shapes
    val spacing = OmTheme.spacing

    listOf(
        "hairline · 1dp" to shapes.hairline,
        "emphasis · 2dp" to shapes.emphasis,
        "accentBar · 3dp" to shapes.accentBar,
    ).forEach { (name, thickness) ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .width(96.dp)
                    .height(thickness)
                    .background(colors.lineStrong),
            )
            Spacer(Modifier.width(spacing.space3))
            OmText(name, style = OmTheme.typography.caption, color = colors.textSecondary)
        }
        Spacer(Modifier.height(spacing.space3))
    }
}

@Composable
private fun ColumnScope.TileSamples() {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing

    OmText("Monochrome", style = OmTheme.typography.caption, color = colors.textSecondary)
    Spacer(Modifier.height(spacing.space2))
    Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
        TileSwatch(colors.tiles.paper, Modifier.weight(1f))
        TileSwatch(colors.tiles.quiet, Modifier.weight(1f))
        TileSwatch(colors.tiles.ink, Modifier.weight(1f))
        TileSwatch(colors.tiles.accent, Modifier.weight(1f))
    }

    Spacer(Modifier.height(spacing.space4))
    OmText("Spectrum", style = OmTheme.typography.caption, color = colors.textSecondary)
    Spacer(Modifier.height(spacing.space2))
    colors.tiles.spectrum.chunked(4).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
            row.forEach { tile -> TileSwatch(tile, Modifier.weight(1f)) }
        }
        Spacer(Modifier.height(spacing.space2))
    }
}

@Composable
private fun TileSwatch(tile: OmTileColor, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.aspectRatio(1f).background(tile.surface),
        contentAlignment = Alignment.Center,
    ) {
        OmText("Aa", style = OmTheme.typography.title, color = tile.onSurface)
    }
}

private fun Color.hex(): String = String.format(Locale.ROOT, "#%06X", toArgb() and 0xFFFFFF)
