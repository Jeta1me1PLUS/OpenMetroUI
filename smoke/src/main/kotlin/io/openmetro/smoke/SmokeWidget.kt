package io.openmetro.smoke

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import io.openmetro.ui.core.theme.OmTileTone
import io.openmetro.ui.glance.OmValueTileWidget

/** 只为验证 omui-glance 能从中央仓库拉下来并参与编译。 */
class SmokeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            OmValueTileWidget(value = "1", label = "smoke", tone = OmTileTone.Accent)
        }
    }
}
