package io.openmetro.sample

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.openmetro.ui.component.base.OmDivider
import io.openmetro.ui.component.base.OmScreen
import io.openmetro.ui.component.base.OmSection
import io.openmetro.ui.component.content.OmListItem
import io.openmetro.ui.component.input.OmSwitch
import io.openmetro.ui.component.navigation.OmTopBar
import io.openmetro.ui.core.theme.OmTheme

/**
 * Showcase 2：设置页。
 *
 * 用分组列表 + 开关验证「紧凑信息密度」：每行 22sp 主文本 + 14sp 次级文本 +
 * 右侧控件，行高由内容决定。主题切换也放在这里，顺便验证两套配色的实际观感。
 */
@Composable
fun SettingsScreen(lab: LabSettings, onLabChange: (LabSettings) -> Unit) {
    val spacing = OmTheme.spacing
    val dark = lab.dark
    var push by remember { mutableStateOf(true) }
    var digest by remember { mutableStateOf(false) }

    OmScreen(scrollable = true) {
        Spacer(Modifier.height(spacing.space6))
        OmTopBar(title = "设置", large = false)

        OmSection(label = "ACCOUNT / 账户") {
            OmListItem(
                title = "hello@example.com",
                subtitle = "个人账户",
            )
            OmDivider()
            OmListItem(
                title = "退出登录",
                subtitle = "需要重新验证身份",
                onClick = {},
            )
        }

        OmSection(label = "NOTIFICATIONS / 通知") {
            OmListItem(
                title = "推送通知",
                subtitle = "仅在 Wi-Fi 下接收",
                trailing = {
                    OmSwitch(checked = push, onCheckedChange = { push = it })
                },
            )
            OmDivider()
            OmListItem(
                title = "每周摘要",
                subtitle = "周一早上发送",
                trailing = {
                    OmSwitch(checked = digest, onCheckedChange = { digest = it })
                },
            )
        }

        OmSection(label = "APPEARANCE / 外观") {
            OmListItem(
                title = "深色主题",
                subtitle = if (dark) "Ink" else "Paper",
                trailing = {
                    OmSwitch(
                        checked = dark,
                        onCheckedChange = { onLabChange(lab.copy(dark = !lab.dark)) },
                    )
                },
            )
        }

        OmSection(label = "ABOUT / 关于") {
            OmListItem(
                title = "版本",
                subtitle = BuildConfig.VERSION_NAME,
            )
            OmDivider()
            OmListItem(
                title = "设计规范",
                subtitle = "全部规则写在 README",
                onClick = {},
            )
        }

        Spacer(Modifier.height(spacing.space12))
    }
}
