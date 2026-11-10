package io.openmetro.sample.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.core.content.edit
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.openmetro.ui.core.theme.OmTileTone
import java.util.concurrent.TimeUnit

/**
 * 小组件的配色偏好。
 *
 * 用 `SharedPreferences` 而不是 Glance 的 `stateDefinition`：
 * 配置 Activity 和 Worker 都在小组件之外执行，读写 Preferences 比维护
 * Glance 的 state 生命周期简单，也不需要关心 GlanceId。
 */
object WidgetPrefs {
    private const val FILE = "omui_widget"
    private const val KEY_TONE = "tone"

    fun tone(context: Context): OmTileTone =
        when (prefs(context).getString(KEY_TONE, null)) {
            "ink" -> OmTileTone.Ink
            "paper" -> OmTileTone.Paper
            else -> OmTileTone.Accent
        }

    fun setTone(context: Context, tone: OmTileTone) {
        prefs(context).edit { putString(KEY_TONE, tone.name.lowercase()) }
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
}

/**
 * 定时刷新小组件。
 *
 * `updatePeriodMillis` 在 provider XML 里是 0（关掉系统那套，它最短只能 30 分钟且不可靠），
 * 改用 WorkManager：最短 15 分钟，且能约束网络状态。
 *
 * 刷新走 `updateAll` —— 它是 suspend，内部按 GlanceId 逐个重绘。
 */
class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = runCatching {
        NewsWidget().updateAll(applicationContext)
    }.fold(
        onSuccess = { Result.success() },
        onFailure = { Result.retry() },
    )
}

/** 只排一次；已存在就保留，避免每次启动都重建任务。 */
fun scheduleWidgetRefresh(context: Context) {
    val request = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(15, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "omui-widget-refresh",
        ExistingPeriodicWorkPolicy.KEEP,
        request,
    )
}
