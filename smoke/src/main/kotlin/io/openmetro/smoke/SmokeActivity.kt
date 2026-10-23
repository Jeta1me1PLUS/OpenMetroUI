package io.openmetro.smoke

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.navigation.OmTopBar
import io.openmetro.ui.component.tile.OmTile
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileTone
import io.openmetro.ui.icon.OmIcons

/**
 * 只要这个 Activity 能编译并启动，就说明 dist 里的产物是完整可用的：
 * core 的 token、components 的组件、icons 的图标都能从 Maven 坐标解析到。
 */
class SmokeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmTheme {
                SmokeScreen()
            }
        }
    }
}

@Composable
private fun SmokeScreen() {
    OmScreen(scrollable = true) {
        Spacer(Modifier.height(32.dp))
        OmTopBar(title = "Consumed", label = "SMOKE / 产物验证")

        Spacer(Modifier.height(24.dp))
        OmText("这些组件来自 dist/maven 的 AAR，不是 project 依赖。")

        Spacer(Modifier.height(24.dp))
        OmButton(
            text = "按钮",
            onClick = {},
            leadingIcon = OmIcons.Check,
        )

        Spacer(Modifier.height(24.dp))
        OmTile(
            title = "磁贴",
            modifier = Modifier.height(120.dp),
            label = "TONE",
            tone = OmTileTone.Accent,
        )

        Spacer(Modifier.height(48.dp))
    }
}
