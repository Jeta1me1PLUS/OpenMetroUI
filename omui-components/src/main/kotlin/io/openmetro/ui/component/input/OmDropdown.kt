package io.openmetro.ui.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.rememberIsRtl
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 下拉选择：触发区是填充变体输入框的形态，面板贴在它下方，宽度与触发区一致。
 *
 * 面板用 `Popup` 而不是 `Dialog` —— 后者会铺满全屏并抢焦点，下拉不该有这种重量。
 * 选中项沿用列表的选中语言：`accentQuiet` 底 + 左侧 3dp 竖条。
 *
 * @param selectedIndex `-1` 表示未选择，此时显示 [placeholder]
 */
@Composable
fun OmDropdown(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "请选择",
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val rtl = rememberIsRtl()
    val shapes = OmTheme.shapes
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    var expanded by remember { mutableStateOf(false) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val hasSelection = selectedIndex in options.indices

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .background(if (enabled) colors.surfaceVariant else colors.surface)
                .border(width = shapes.hairline, color = colors.line)
                .then(
                    if (enabled) {
                        Modifier.omClickable(role = Role.Button) { expanded = true }
                    } else {
                        Modifier
                    },
                )
                .onGloballyPositioned { anchorSize = it.size }
                .padding(horizontal = spacing.space3, vertical = spacing.space2),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OmText(
                text = if (hasSelection) options[selectedIndex] else placeholder,
                style = typography.bodyL,
                color = when {
                    !enabled -> colors.textDisabled
                    hasSelection -> colors.textPrimary
                    else -> colors.textSecondary
                },
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            OmIcon(
                imageVector = OmIcons.ChevronDown,
                contentDescription = null,
                size = 20.dp,
                tint = if (enabled) colors.textSecondary else colors.textDisabled,
            )
        }

        if (expanded) {
            Popup(
                // 面板宽度跟随触发区，下方放不下时向上翻
                popupPositionProvider = remember {
                    io.openmetro.ui.component.overlay.OmAnchorPositionProvider(matchAnchorWidth = true)
                },
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Column(
                    modifier = Modifier
                        .width(with(density) { anchorSize.width.toDp() })
                        .heightIn(max = 240.dp)
                        .background(colors.surface)
                        .border(width = shapes.hairline, color = colors.lineStrong)
                        .verticalScroll(rememberScrollState()),
                ) {
                    options.forEachIndexed { index, option ->
                        val selected = index == selectedIndex

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 48.dp)
                                .background(if (selected) colors.accentQuiet else colors.surface)
                                .drawBehind {
                                    if (selected) {
                                        val barWidth = 3.dp.toPx()
                                        drawRect(
                                            color = colors.accent,
                                            topLeft = Offset(if (rtl) size.width - barWidth else 0f, 0f),
                                            size = Size(barWidth, size.height),
                                        )
                                    }
                                }
                                .omClickable {
                                    onSelect(index)
                                    expanded = false
                                }
                                .padding(horizontal = spacing.space3, vertical = spacing.space2),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            OmText(
                                text = option,
                                style = typography.bodyL,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}
