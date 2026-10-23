package io.openmetro.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmSection
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.button.OmButtonVariant
import io.openmetro.ui.component.card.OmCard
import io.openmetro.ui.component.card.OmCardVariant
import io.openmetro.ui.component.content.OmAvatar
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.component.feedback.OmProgressBar
import io.openmetro.ui.component.input.OmSlider
import io.openmetro.ui.component.input.OmSwitch
import io.openmetro.ui.component.navigation.OmTopBar
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.OmColorScheme
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileMode
import java.util.Locale

/**
 * Lab：运行时改 token，用来看「参数一动，整套界面怎么变」。
 *
 * 这里改的每一项都会作用到全部页面，不是局部预览 —— 局部预览看不出
 * 「换一个 accent 之后，磁贴、按钮、焦点环是不是还协调」。
 */
@Composable
fun LabScreen(lab: LabSettings, onLabChange: (LabSettings) -> Unit) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    val currentAccent = lab.accent ?: OmColorScheme.DefaultAccent

    OmScreen(scrollable = true) {
        Spacer(Modifier.height(spacing.space8))
        OmTopBar(title = "实验台", label = "LAB / 实时调参")
        Spacer(Modifier.height(spacing.space2))
        OmText(
            text = "这里的改动作用到全部页面",
            style = typography.bodyM,
            color = colors.textSecondary,
        )

        OmSection(label = "ACCENT / 强调色") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                LabAccents.forEach { (_, color) ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .omClickable { onLabChange(lab.copy(accent = color)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color)
                                .then(
                                    if (currentAccent == color) {
                                        Modifier.border(2.dp, colors.textPrimary)
                                    } else {
                                        Modifier.border(1.dp, colors.line)
                                    },
                                ),
                        )
                    }
                }
            }
            Spacer(Modifier.height(spacing.space2))
            OmText(
                text = currentAccent.hex(),
                style = typography.caption,
                color = colors.textSecondary,
            )
        }

        OmSection(label = "THEME / 主题") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                OmButton(
                    text = "PAPER",
                    onClick = { onLabChange(lab.copy(dark = false)) },
                    variant = if (lab.dark) OmButtonVariant.Outlined else OmButtonVariant.Filled,
                )
                OmButton(
                    text = "INK",
                    onClick = { onLabChange(lab.copy(dark = true)) },
                    variant = if (lab.dark) OmButtonVariant.Filled else OmButtonVariant.Outlined,
                )
            }
        }

        OmSection(label = "TILES / 磁贴配色") {
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                OmButton(
                    text = "MONOCHROME",
                    onClick = { onLabChange(lab.copy(tileMode = OmTileMode.Monochrome)) },
                    variant = if (lab.tileMode == OmTileMode.Monochrome) {
                        OmButtonVariant.Filled
                    } else {
                        OmButtonVariant.Outlined
                    },
                )
                OmButton(
                    text = "SPECTRUM",
                    onClick = { onLabChange(lab.copy(tileMode = OmTileMode.Spectrum)) },
                    variant = if (lab.tileMode == OmTileMode.Spectrum) {
                        OmButtonVariant.Filled
                    } else {
                        OmButtonVariant.Outlined
                    },
                )
            }
            Spacer(Modifier.height(spacing.space2))
            OmText(
                text = "Spectrum 是 opt-in：八色等明度、只变色相。切换后回到 NEWS 页看磁贴。",
                style = typography.caption,
                color = colors.textSecondary,
            )
        }

        OmSection(label = "FONT SCALE / 字号缩放") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OmSlider(
                    value = (lab.fontScale - MinFontScale) / (MaxFontScale - MinFontScale),
                    onValueChange = {
                        onLabChange(
                            lab.copy(fontScale = MinFontScale + it * (MaxFontScale - MinFontScale)),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(spacing.space4))
                OmText(
                    text = String.format(Locale.ROOT, "%.2f×", lab.fontScale),
                    style = typography.title,
                )
            }
            Spacer(Modifier.height(spacing.space2))
            OmText(
                text = "放大到 1.3 倍以上，横向并排的布局应当自动改为纵向堆叠。",
                style = typography.caption,
                color = colors.textSecondary,
            )
        }

        OmSection(label = "GRID / 网格叠加") {
            OmListItem(
                title = "显示 4dp 基线与栏网格",
                subtitle = "叠加在所有页面内容之上",
                trailing = {
                    OmSwitch(
                        checked = lab.gridOverlay,
                        onCheckedChange = { onLabChange(lab.copy(gridOverlay = it)) },
                    )
                },
            )
        }

        OmSection(label = "PREVIEW / 实时预览") {
            OmCard(variant = OmCardVariant.Flat) {
                OmText("用当前参数渲染", style = typography.title)
                Spacer(Modifier.height(spacing.space3))
                Row(horizontalArrangement = Arrangement.spacedBy(spacing.space2)) {
                    OmButton("PRIMARY", {})
                    OmButton("OUTLINE", {}, variant = OmButtonVariant.Outlined)
                }
                Spacer(Modifier.height(spacing.space4))
                OmTag("标签", selected = true)
                Spacer(Modifier.height(spacing.space4))
                OmProgressBar(progress = 0.62f)
                Spacer(Modifier.height(spacing.space4))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OmAvatar("Om", size = 32.dp)
                    Spacer(Modifier.width(spacing.space3))
                    OmText(
                        text = "头像与文本同一行",
                        style = typography.bodyM,
                        color = colors.textSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(spacing.space12))
    }
}

private const val MinFontScale = 0.85f
private const val MaxFontScale = 1.6f

private fun Color.hex(): String = String.format(Locale.ROOT, "#%06X", toArgb() and 0xFFFFFF)
