package io.openmetro.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.openmetro.ui.component.content.OmLazyList
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.component.content.OmTooltip
import io.openmetro.ui.component.feedback.OmProgressBar
import io.openmetro.ui.component.input.OmChipInput
import io.openmetro.ui.component.input.OmDatePicker
import io.openmetro.ui.component.input.OmSearchField
import io.openmetro.ui.component.input.OmSegmentedControl
import io.openmetro.ui.component.input.OmStepperInput
import io.openmetro.ui.component.input.OmDate
import io.openmetro.ui.core.theme.OmStrings
import io.openmetro.ui.core.theme.OmTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 表单控件、反馈组件与懒加载列表的行为测试。
 *
 * 与 [OmComponentBehaviorTest] 一样只断言行为，不碰视觉；文案固定中文。
 */
@RunWith(AndroidJUnit4::class)
class OmInputFeedbackTest {

    @get:Rule
    val rule = createComposeRule()

    private fun content(block: @Composable () -> Unit) {
        rule.setContent {
            OmTheme(strings = OmStrings.Chinese) { block() }
        }
    }

    @Test
    fun segmented_control_selects_index() {
        var index by mutableIntStateOf(0)
        content {
            OmSegmentedControl(
                options = listOf("Day", "Week"),
                selectedIndex = index,
                onSelect = { index = it },
            )
        }

        rule.onNodeWithText("Week").performClick()
        rule.waitForIdle()
        assertEquals(1, index)
    }

    @Test
    fun stepper_increments_and_disables_at_bounds() {
        var value by mutableIntStateOf(1)
        content {
            OmStepperInput(value = value, onValueChange = { value = it }, min = 1, max = 3)
        }

        // 已在下界，「减少」应当不可点
        rule.onNodeWithContentDescription("减少").assertIsNotEnabled()
        rule.onNodeWithContentDescription("增加").performClick()
        rule.waitForIdle()
        assertEquals(2, value)
    }

    @Test
    fun chip_input_commits_on_ime_action() {
        var values by mutableStateOf(listOf("a"))
        content { OmChipInput(values = values, onValuesChange = { values = it }) }

        rule.onNode(hasSetTextAction()).performTextInput("b")
        rule.onNode(hasSetTextAction()).performImeAction()
        rule.waitForIdle()

        assertEquals(listOf("a", "b"), values)
    }

    @Test
    fun search_field_clear_button_empties_value() {
        var query by mutableStateOf("metro")
        content { OmSearchField(value = query, onValueChange = { query = it }) }

        rule.onNodeWithContentDescription("清除").performClick()
        rule.waitForIdle()
        assertEquals("", query)
    }

    @Test
    fun progress_bar_reports_range_info() {
        content { OmProgressBar(progress = 0.5f) }

        rule.onNode(
            androidx.compose.ui.test.SemanticsMatcher.keyIsDefined(
                androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo,
            ),
        ).assertRangeInfoEquals(ProgressBarRangeInfo(0.5f, 0f..1f))
    }

    @Test
    fun date_picker_selects_day() {
        var picked: OmDate? = null
        content { OmDatePicker(selected = null, onSelect = { picked = it }) }

        rule.onNodeWithText("15").performClick()
        rule.waitForIdle()
        assertEquals(15, picked?.day)
    }

    @Test
    fun lazy_list_only_composes_visible_items() {
        content {
            OmLazyList(items = (1..200).toList()) { item ->
                OmListItem(title = "Item $item", onClick = {})
            }
        }

        rule.onNodeWithText("Item 1").assertIsDisplayed()
        // 第 200 项远在视口之外，没有被组合
        rule.onNodeWithText("Item 200").assertDoesNotExist()
    }

    @Test
    fun tooltip_shows_text() {
        content { OmTooltip("Hint") }
        rule.onNodeWithText("Hint").assertIsDisplayed()
    }

    @Test
    fun tag_without_remove_has_no_close_affordance() {
        content { OmTag(text = "Plain") }
        rule.onNodeWithContentDescription("移除").assertDoesNotExist()
        assertTrue(true)
    }
}
