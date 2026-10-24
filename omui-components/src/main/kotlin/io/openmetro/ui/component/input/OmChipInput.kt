package io.openmetro.ui.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.core.theme.OmTheme

/**
 * 标签输入：已提交的值显示为可删除的标签，末尾一个输入框接收新值。
 *
 * 回车提交，重复值会被忽略。用 `FlowRow` 而不是横向滚动 ——
 * 标签多了应该换行，横滑会让用户不知道还有几个。
 */
@Composable
fun OmChipInput(
    values: List<String>,
    onValuesChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "输入后回车",
    enabled: Boolean = true,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography

    var input by remember { mutableStateOf("") }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.space2),
        verticalArrangement = Arrangement.spacedBy(spacing.space2),
    ) {
        values.forEach { value ->
            OmTag(
                text = value,
                onRemove = if (enabled) {
                    { onValuesChange(values - value) }
                } else {
                    null
                },
            )
        }

        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.widthIn(min = 120.dp),
            enabled = enabled,
            singleLine = true,
            textStyle = typography.bodyM.copy(color = colors.textPrimary),
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    val trimmed = input.trim()
                    if (trimmed.isNotEmpty() && trimmed !in values) {
                        onValuesChange(values + trimmed)
                    }
                    input = ""
                },
            ),
            decorationBox = { innerTextField ->
                if (input.isEmpty()) {
                    OmText(
                        text = placeholder,
                        style = typography.bodyM,
                        color = if (enabled) colors.textSecondary else colors.textDisabled,
                        maxLines = 1,
                    )
                }
                innerTextField()
            },
        )
    }
}
