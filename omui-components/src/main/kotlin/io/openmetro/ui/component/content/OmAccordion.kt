package io.openmetro.ui.component.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.icon.OmIcons

/**
 * 折叠面板。
 *
 * 展开状态由组件内部持有 —— 这是少数允许的「内部状态」：它纯属交互反馈，
 * 提升到调用方没有任何收益。需要受控行为时用 `initiallyExpanded` 设定初值即可。
 *
 * 展开动画是高度变化 + 淡入（都属位移），指示器同时旋转 90°。不做弹跳。
 */
@Composable
fun OmAccordion(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = OmTheme.colors
    val spacing = OmTheme.spacing
    val typography = OmTheme.typography
    val motion = OmTheme.motion

    var expanded by remember { mutableStateOf(initiallyExpanded) }

    val indicatorRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = motion.fast, easing = motion.standard),
        label = "omAccordionIndicator",
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .omFocusRing()
                .omClickable(role = Role.Button) { expanded = !expanded }
                .heightIn(min = 48.dp)
                .padding(vertical = spacing.space3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                OmText(
                    text = title,
                    style = typography.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(spacing.space1))
                    OmText(
                        text = subtitle,
                        style = typography.bodyM,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(Modifier.width(spacing.space3))
            OmIcon(
                imageVector = OmIcons.ChevronEnd,
                contentDescription = null,
                modifier = Modifier.graphicsLayer { rotationZ = indicatorRotation },
                size = 20.dp,
                tint = colors.textSecondary,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = motion.normal, easing = motion.standard),
            ) + fadeIn(
                animationSpec = tween(durationMillis = motion.normal),
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = motion.normal, easing = motion.standard),
            ) + fadeOut(
                animationSpec = tween(durationMillis = motion.fast),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.space4),
                content = content,
            )
        }
    }
}
