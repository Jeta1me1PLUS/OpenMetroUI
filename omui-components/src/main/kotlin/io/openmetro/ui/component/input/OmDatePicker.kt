package io.openmetro.ui.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmIconButton
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons
import java.util.Calendar

/** 一个没有时区、没有时间的日期。用 Calendar 而不是 java.time，避免引入 desugaring。 */
@Immutable
data class OmDate(val year: Int, val month: Int, val day: Int)

/**
 * 月历选择器。
 *
 * 7 列严格对齐栅格：星期表头、日期格、选中方块共用同一套列宽。
 * 选中是 accent 实心直角方块，今天用 2dp accent 描边 —— 两者同时出现时以实心为准。
 *
 * 月份状态由组件内部持有，`selected` 变化不会重置当前浏览的月份（用户可能只是在翻月看）。
 */
@Composable
fun OmDatePicker(
    selected: OmDate?,
    onSelect: (OmDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    val today = remember {
        val cal = Calendar.getInstance()
        OmDate(
            year = cal.get(Calendar.YEAR),
            month = cal.get(Calendar.MONTH) + 1,
            day = cal.get(Calendar.DAY_OF_MONTH),
        )
    }

    var year by remember { mutableIntStateOf(selected?.year ?: today.year) }
    var month by remember { mutableIntStateOf(selected?.month ?: today.month) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OmIconButton(
                icon = OmIcons.ChevronEnd,
                contentDescription = "上个月",
                onClick = {
                    if (month == 1) {
                        year -= 1
                        month = 12
                    } else {
                        month -= 1
                    }
                },
                modifier = Modifier.graphicsLayer { rotationZ = 180f },
            )
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                OmText(text = "$year 年 $month 月", style = typography.title)
            }
            OmIconButton(
                icon = OmIcons.ChevronEnd,
                contentDescription = "下个月",
                onClick = {
                    if (month == 12) {
                        year += 1
                        month = 1
                    } else {
                        month += 1
                    }
                },
            )
        }

        // 星期表头：周一起始，与多数中文日历一致
        Row(modifier = Modifier.fillMaxWidth()) {
            WeekdayLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f).padding(vertical = spacing.space2),
                    contentAlignment = Alignment.Center,
                ) {
                    OmText(
                        text = label,
                        style = typography.caption,
                        color = colors.textSecondary,
                        maxLines = 1,
                    )
                }
            }
        }

        val firstWeekday = remember(year, month) { firstWeekdayIndex(year, month) }
        val dayCount = remember(year, month) { daysInMonth(year, month) }
        val rowCount = (firstWeekday + dayCount + 6) / 7

        repeat(rowCount) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { column ->
                    val day = row * 7 + column - firstWeekday + 1

                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (day in 1..dayCount) {
                            val date = OmDate(year, month, day)
                            val isSelected = selected == date
                            val isToday = today == date

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(1.dp)
                                    .background(if (isSelected) colors.accent else Color.Transparent)
                                    .then(
                                        if (isToday && !isSelected) {
                                            Modifier.border(width = 2.dp, color = colors.accent)
                                        } else {
                                            Modifier
                                        },
                                    )
                                    .omClickable(role = Role.Button) { onSelect(date) },
                                contentAlignment = Alignment.Center,
                            ) {
                                OmText(
                                    text = day.toString(),
                                    style = typography.bodyM,
                                    color = if (isSelected) colors.onAccent else colors.textPrimary,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val WeekdayLabels = listOf("一", "二", "三", "四", "五", "六", "日")

/** 当月 1 号是星期几，0 = 周一。 */
private fun firstWeekdayIndex(year: Int, month: Int): Int {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, 1)
    }
    // Calendar.SUNDAY = 1 … SATURDAY = 7，转换成 0 = 周一
    return (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
}

private fun daysInMonth(year: Int, month: Int): Int {
    val cal = Calendar.getInstance().apply {
        clear()
        set(year, month - 1, 1)
    }
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}
