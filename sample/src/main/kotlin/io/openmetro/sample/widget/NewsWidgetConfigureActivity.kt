package io.openmetro.sample.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.component.button.OmButton
import io.openmetro.ui.component.button.OmButtonVariant
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileTone

/**
 * 小组件的配置页：用户把它拖到桌面后先选底色。
 *
 * 三条必须遵守的规则，漏一条小组件就不会被添加：
 * 1. `setResult` 必须带 `EXTRA_APPWIDGET_ID`；
 * 2. 必须在 `AppWidgetManager` 里查一次 id，确认它还有效（用户可能中途取消）；
 * 3. 用户主动返回时要 `RESULT_CANCELED`，否则会留下一个空壳。
 */
class NewsWidgetConfigureActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 默认返回「取消」，用户真正选完才改成 OK
        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            OmTheme {
                ConfigureScreen(
                    onPick = { tone ->
                        WidgetPrefs.setTone(this, tone)
                        complete()
                    },
                )
            }
        }
    }

    private fun complete() {
        setResult(
            RESULT_OK,
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId),
        )
        finish()
    }
}

@Composable
private fun ConfigureScreen(onPick: (OmTileTone) -> Unit) {
    OmScreen {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OmText("选择磁贴颜色", style = OmTheme.typography.displayM)
            Spacer(Modifier.height(8.dp))
            OmText(
                text = "选完之后拖到桌面即可。",
                style = OmTheme.typography.bodyM,
                color = OmTheme.colors.textSecondary,
            )
            Spacer(Modifier.height(16.dp))

            OmButton("强调色 ACCENT", { onPick(OmTileTone.Accent) }, fillWidth = true)
            OmButton(
                text = "反白 INK",
                onClick = { onPick(OmTileTone.Ink) },
                variant = OmButtonVariant.Outlined,
                fillWidth = true,
            )
            OmButton(
                text = "浅色 PAPER",
                onClick = { onPick(OmTileTone.Paper) },
                variant = OmButtonVariant.Outlined,
                fillWidth = true,
            )
        }
    }
}
