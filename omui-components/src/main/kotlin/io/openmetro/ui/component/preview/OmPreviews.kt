package io.openmetro.ui.component.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.button.OmButtonSize
import io.openmetro.ui.component.button.OmButtonVariant
import io.openmetro.ui.component.content.OmList
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.content.OmTag
import io.openmetro.ui.component.input.OmCheckbox
import io.openmetro.ui.component.input.OmTextField
import io.openmetro.ui.component.input.OmTextFieldVariant
import io.openmetro.ui.component.tile.OmTile
import io.openmetro.ui.component.tile.OmTileGrid
import io.openmetro.ui.core.layout.omGridSpan
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileTone

/**
 * 组件的 Android Studio 预览。
 *
 * 只覆盖最常用的几组，用来在改动 token 或组件时快速目视检查 ——
 * 真正的验收仍然靠 demo 在真机上看。
 */

@Preview(name = "Buttons", showBackground = true, widthDp = 360)
@Composable
private fun PreviewButtons() {
    OmTheme {
        OmScreen {
            Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OmButton("Filled", onClick = {})
                OmButton("Outlined", onClick = {}, variant = OmButtonVariant.Outlined)
                OmButton("Ink", onClick = {}, variant = OmButtonVariant.FilledInk)
                OmButton("Destructive", onClick = {}, variant = OmButtonVariant.Destructive)
                OmButton("Large", onClick = {}, size = OmButtonSize.Large, fillWidth = true)
            }
        }
    }
}

@Preview(name = "Inputs", showBackground = true, widthDp = 360)
@Composable
private fun PreviewInputs() {
    OmTheme {
        OmScreen {
            Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OmTextField(value = "", onValueChange = {}, label = "EMAIL", placeholder = "name@example.com")
                OmTextField(
                    value = "note",
                    onValueChange = {},
                    label = "NOTE",
                    variant = OmTextFieldVariant.Filled,
                    singleLine = false,
                    minLines = 3,
                )
                OmCheckbox(checked = true, onCheckedChange = {}, label = "已开启")
            }
        }
    }
}

@Preview(name = "Tiles", showBackground = true, widthDp = 360)
@Composable
private fun PreviewTiles() {
    OmTheme {
        OmScreen {
            Column(Modifier.padding(vertical = 16.dp)) {
                OmTileGrid(columns = 4, gutter = 8.dp) {
                    OmTile("收件箱", Modifier.omGridSpan(2).aspectRatio(1f), tone = OmTileTone.Accent)
                    OmTile("设计周报", Modifier.omGridSpan(2).aspectRatio(1f), tone = OmTileTone.Paper)
                    OmTile("阅读清单", Modifier.omGridSpan(4).aspectRatio(2f), tone = OmTileTone.Ink)
                }
            }
        }
    }
}

@Preview(name = "List and tags", showBackground = true, widthDp = 360)
@Composable
private fun PreviewList() {
    OmTheme {
        OmScreen {
            Column(Modifier.padding(vertical = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OmTag("全部", selected = true, onClick = {})
                    OmTag("科技", onClick = {})
                    OmTag("可删除", onRemove = {})
                }
                Spacer(Modifier.height(16.dp))
                OmList(items = listOf("收件箱" to "3 封未读", "已发送" to "今天")) { (title, subtitle) ->
                    OmListItem(title = title, subtitle = subtitle, onClick = {})
                }
                OmListItem(title = "选中项", selected = true, onClick = {})
            }
        }
    }
}

@Preview(name = "Ink theme", showBackground = true, widthDp = 360)
@Composable
private fun PreviewInk() {
    OmTheme(darkTheme = true) {
        OmScreen {
            Column(Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OmText("Ink 主题")
                OmButton("Filled", onClick = {})
                OmTile("磁贴", Modifier.fillMaxWidth().height(96.dp), tone = OmTileTone.Ink)
            }
        }
    }
}
