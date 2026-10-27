package io.openmetro.ui.component.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.openmetro.ui.core.theme.LocalOmStrings
import io.openmetro.ui.icon.OmIcons

/**
 * 搜索框：填充变体的输入框 + 前置搜索图标 + 有内容时的清除按钮。
 *
 * 没有独立样式 —— 它就是 `OmTextField` 的一种组合。单独成组件是因为
 * 「清除按钮只在有内容时出现」这条逻辑每个页面都要写一遍。
 */
@Composable
fun OmSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    onClear: (() -> Unit)? = null,
) {
    val hasValue = value.isNotEmpty()

    OmTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder ?: LocalOmStrings.current.searchPlaceholder,
        variant = OmTextFieldVariant.Filled,
        leadingIcon = OmIcons.Search,
        trailingIcon = if (hasValue) OmIcons.Close else null,
        onTrailingIconClick = if (hasValue) {
            {
                if (onClear != null) onClear() else onValueChange("")
            }
        } else {
            null
        },
        singleLine = true,
    )
}
