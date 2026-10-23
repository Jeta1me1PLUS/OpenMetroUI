package io.openmetro.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import io.openmetro.ui.component.base.OmDivider
import io.openmetro.ui.component.navigation.OmBottomBar
import io.openmetro.ui.component.navigation.OmBottomBarItem
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

enum class SamplePage(val label: String, val icon: ImageVector) {
    News("NEWS", OmIcons.Article),
    Spec("SPEC", OmIcons.Grid),
    Gallery("UI", OmIcons.Menu),
    Lab("LAB", OmIcons.Beaker),
    Settings("SET", OmIcons.Sliders),
}

/**
 * 提到顶层并缓存：如果每次重组都 `map` 一个新 List，`OmBottomBar` 的 items 参数
 * 引用就一直在变，底部栏会跟着整页切换一起重组。
 */
private val BottomBarItems: List<OmBottomBarItem> by lazy {
    SamplePage.entries.map { OmBottomBarItem(label = it.label, icon = it.icon) }
}

/**
 * demo 骨架：内容区 + 底部导航 + 可选的网格叠加。
 *
 * 用标签枚举而不是 Navigation Compose —— 五个平级页面加一层路由库只有一个好处，
 * 就是让代码看起来更像正式项目。
 */
@Composable
fun SampleApp(lab: LabSettings, onLabChange: (LabSettings) -> Unit) {
    var page by remember { mutableStateOf(SamplePage.News) }
    val colors = OmTheme.colors

    // 系统栏内边距：内容区由各页的 OmScreen 负责，底部导航自己避让手势条。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
    ) {
        Box(Modifier.weight(1f)) {
            when (page) {
                SamplePage.News -> NewsScreen()
                SamplePage.Spec -> SpecScreen(lab = lab, onLabChange = onLabChange)
                SamplePage.Gallery -> GalleryScreen()
                SamplePage.Lab -> LabScreen(lab = lab, onLabChange = onLabChange)
                SamplePage.Settings -> SettingsScreen(lab = lab, onLabChange = onLabChange)
            }

            if (lab.gridOverlay) {
                GridOverlay(Modifier.matchParentSize())
            }
        }

        OmDivider()
        OmBottomBar(
            items = BottomBarItems,
            selectedIndex = page.ordinal,
            onSelect = { index -> page = SamplePage.entries[index] },
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}
