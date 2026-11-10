package io.openmetro.sample.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import io.openmetro.sample.MainActivity
import io.openmetro.ui.core.theme.OmTileTone
import io.openmetro.ui.glance.OmGlanceColors
import io.openmetro.ui.glance.OmValueTileWidget

/**
 * demo 的桌面小组件：一块「未读消息」磁贴。
 *
 * 用 `OmValueTileWidget` 而不是 `OmTileWidget` —— 小组件的第一眼应该给数字，
 * 名称是次要信息，这与 App 内 `OmStat` 的层级判断一致。
 *
 * 点击整块打开 App。`actionStartActivity` 由 Glance 转成 PendingIntent，
 * 不需要我们自己拼 Intent 的 flag —— 这块在 launcher 进程里执行，
 * 手写 PendingIntent 很容易漏掉 `FLAG_IMMUTABLE`。
 */
class NewsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val context = LocalContext.current
            val dark = context.isNightMode()
            OmValueTileWidget(
                value = "3",
                label = "未读消息",
                // 配色由配置页写入，用户换了颜色下次刷新就生效
                tone = WidgetPrefs.tone(context),
                dark = dark,
                onClick = actionStartActivity<MainActivity>(),
            )
        }
    }
}

class NewsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NewsWidget()
}

/** 小组件的「深色」跟随系统夜间模式，而不是 App 内的手动开关 —— 桌面没有 App 的上下文。 */
@Composable
private fun Context.isNightMode(): Boolean =
    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
        Configuration.UI_MODE_NIGHT_YES

/** 供宿主参考：小组件里取配色的方式（Glance 没有 CompositionLocal 主题）。 */
@Suppress("unused")
internal fun previewColors(dark: Boolean): OmGlanceColors = OmGlanceColors.of(dark)
