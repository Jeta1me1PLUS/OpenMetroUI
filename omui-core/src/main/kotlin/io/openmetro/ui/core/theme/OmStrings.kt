package io.openmetro.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

/**
 * 组件内置的文案。
 *
 * 这些字符串出现在读屏播报（`contentDescription`）和默认占位符里，属于组件自身的一部分，
 * 不应该由每个调用点重复传。默认按系统语言在中英之间选，也可以用 `OmTheme(strings = …)` 覆盖。
 *
 * 日期相关的两个字段配合使用：[monthNames] 给出月份名，[yearMonthFormat] 负责拼接。
 * 格式串里 `%1$s` 是年份、`%2$s` 是月份名，顺序因语言而异 —— 中文是「2026 年 10 月」，
 * 英文是「October 2026」，同一条格式模板拼不出两种，所以顺序交给格式串。
 */
@Immutable
data class OmStrings(
    val back: String,
    val close: String,
    val remove: String,
    val delete: String,
    val clear: String,
    val increase: String,
    val decrease: String,
    val selectPlaceholder: String,
    val searchPlaceholder: String,
    val chipInputPlaceholder: String,
    val previousMonth: String,
    val nextMonth: String,
    val yearMonthFormat: String,
    val monthNames: List<String>,
    val weekdayLabels: List<String>,
) {
    companion object {
        val Chinese: OmStrings = OmStrings(
            back = "返回",
            close = "关闭",
            remove = "移除",
            delete = "删除",
            clear = "清除",
            increase = "增加",
            decrease = "减少",
            selectPlaceholder = "请选择",
            searchPlaceholder = "搜索",
            chipInputPlaceholder = "输入后回车",
            previousMonth = "上个月",
            nextMonth = "下个月",
            yearMonthFormat = "%1\$s 年 %2\$s 月",
            monthNames = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"),
            weekdayLabels = listOf("一", "二", "三", "四", "五", "六", "日"),
        )

        val English: OmStrings = OmStrings(
            back = "Back",
            close = "Close",
            remove = "Remove",
            delete = "Delete",
            clear = "Clear",
            increase = "Increase",
            decrease = "Decrease",
            selectPlaceholder = "Select",
            searchPlaceholder = "Search",
            chipInputPlaceholder = "Type and press enter",
            previousMonth = "Previous month",
            nextMonth = "Next month",
            yearMonthFormat = "%2\$s %1\$s",
            monthNames = listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December",
            ),
            weekdayLabels = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
        )

        /** 按系统语言挑一套；中文环境用中文，其他一律英文。 */
        fun forSystemLocale(): OmStrings =
            if (Locale.getDefault().language.startsWith("zh")) Chinese else English
    }
}

/** 当前生效的文案。`OmTheme` 会提供，脱离主题单独用组件时退回英文。 */
val LocalOmStrings = staticCompositionLocalOf { OmStrings.English }
