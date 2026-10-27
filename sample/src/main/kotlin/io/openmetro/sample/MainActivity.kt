package io.openmetro.sample

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import io.openmetro.ui.core.theme.OmTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var lab by remember { mutableStateOf(LabSettings()) }

            // 手动切主题时同步系统栏图标的明暗，否则 Ink 主题下会看不到状态栏内容。
            LaunchedEffect(lab.dark) {
                enableEdgeToEdge(
                    statusBarStyle = if (lab.dark) {
                        SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    },
                )
            }

            OmTheme(
                darkTheme = lab.dark,
                accent = lab.accent,
                tileMode = lab.tileMode,
            ) {
                // 字号缩放作用到整个应用：改 fontScale，不改任何组件的字号 token。
                val density = LocalDensity.current
                CompositionLocalProvider(
                    LocalDensity provides Density(
                        density = density.density,
                        fontScale = lab.fontScale,
                    ),
                    LocalLayoutDirection provides if (lab.rtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
                ) {
                    SampleApp(lab = lab, onLabChange = { lab = it })
                }
            }
        }
    }
}
