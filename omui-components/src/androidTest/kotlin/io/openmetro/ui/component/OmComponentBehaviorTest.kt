package io.openmetro.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.semantics.SemanticsActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.content.OmAccordion
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.content.OmSwipeToDismiss
import io.openmetro.ui.component.content.OmTable
import io.openmetro.ui.component.content.OmTableColumn
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.component.input.OmCheckbox
import io.openmetro.ui.component.input.OmDropdown
import io.openmetro.ui.component.navigation.OmPivot
import io.openmetro.ui.component.tile.OmTile
import io.openmetro.ui.core.theme.OmStrings
import io.openmetro.ui.core.theme.OmTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 组件行为测试。
 *
 * 只断言「点击有没有触发、状态有没有切换、手势有没有生效」，
 * 不碰视觉 —— 那靠 demo 截图。
 *
 * 全部显式传 `OmStrings.Chinese`，否则断言结果会随设备语言漂移。
 */
@RunWith(AndroidJUnit4::class)
class OmComponentBehaviorTest {

    @get:Rule
    val rule = createComposeRule()

    private fun content(block: @Composable () -> Unit) {
        rule.setContent {
            OmTheme(strings = OmStrings.Chinese) { block() }
        }
    }

    @Test
    fun button_click_invokes_callback() {
        var clicked = 0
        content { OmButton("Save", onClick = { clicked++ }) }

        rule.onNodeWithText("Save").performClick()
        assertEquals(1, clicked)
    }

    @Test
    fun tag_remove_button_is_clickable() {
        var removed = false
        content { OmTag(text = "Design", onRemove = { removed = true }) }

        rule.onNodeWithContentDescription("移除").performClick()
        assertTrue(removed)
    }

    @Test
    fun swipe_left_triggers_dismiss() {
        var dismissed = false
        content {
            OmSwipeToDismiss(onDismiss = { dismissed = true }) {
                OmListItem(title = "Row", onClick = {})
            }
        }

        rule.onNodeWithText("Row").performTouchInput { swipeLeft() }
        rule.waitForIdle()
        assertTrue(dismissed)
    }

    @Test
    fun pivot_selection_changes() {
        var selected by mutableStateOf(0)
        content {
            OmPivot(
                tabs = listOf("One", "Two"),
                selectedIndex = selected,
                onSelect = { selected = it },
            )
        }

        // 文字节点自身没有点击动作，要点到带 click action 的父容器
        rule.onNode(hasText("Two") and hasClickAction())
            .performSemanticsAction(SemanticsActions.OnClick)
        rule.waitForIdle()
        assertEquals(1, selected)
    }

    @Test
    fun tile_flip_reveals_back_title() {
        content {
            OmTile(title = "Front", modifier = Modifier, backTitle = "Back")
        }

        rule.onNodeWithText("Front").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun checkbox_toggles_and_reports_state() {
        var checked by mutableStateOf(false)
        content {
            OmCheckbox(checked = checked, onCheckedChange = { checked = it }, label = "Check")
        }

        rule.onNodeWithText("Check").assertIsOff()
        rule.onNodeWithText("Check").performClick()
        rule.waitForIdle()
        assertTrue(checked)
        rule.onNodeWithText("Check").assertIsOn()
    }

    @Test
    fun dropdown_opens_and_selects() {
        var selected by mutableStateOf(-1)
        content {
            OmDropdown(
                options = listOf("Paper", "Ink"),
                selectedIndex = selected,
                onSelect = { selected = it },
            )
        }

        rule.onNodeWithText("请选择").performClick()
        rule.onNodeWithText("Ink").performClick()
        assertEquals(1, selected)
    }

    @Test
    fun table_row_click_reports_index() {
        var clicked = -1
        content {
            OmTable(
                columns = listOf(OmTableColumn("Name")),
                rows = listOf(listOf("Alpha"), listOf("Beta")),
                onRowClick = { clicked = it },
            )
        }

        rule.onNodeWithText("Beta").performClick()
        assertEquals(1, clicked)
    }

    @Test
    fun accordion_expands_content() {
        content {
            OmAccordion(title = "Section") { OmText("Hidden body") }
        }

        rule.onNodeWithText("Hidden body").assertDoesNotExist()
        rule.onNodeWithText("Section").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Hidden body").assertIsDisplayed()
    }

}
