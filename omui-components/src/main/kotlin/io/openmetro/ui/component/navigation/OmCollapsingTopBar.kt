package io.openmetro.ui.component.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.openmetro.ui.core.theme.OmTheme

/**
 * 随滚动收缩的页头：未滚动时是大标题（48sp），滚过 [collapseThreshold] 后
 * 变成紧凑标题（22sp）。
 *
 * **只做两个状态的切换**，不做连续字号插值 —— 连续缩放的标题在滚动时会抖动，
 * 而且字号变小意味着字距、行高都要跟着重算，视觉上是一场小型地震。
 *
 * 页头本身应放在滚动容器之外（固定顶部），否则它会先被滚走，收缩效果只能看到一瞬。
 * 把同一个 [ScrollState] 同时交给滚动内容与本组件即可。
 */
@Composable
fun OmCollapsingTopBar(
    title: String,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    label: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    collapseThreshold: Int = 48,
) {
    val motion = OmTheme.motion

    // 直接在组合期读 scrollState.value 会随每一帧滚动触发重组；
    // derivedStateOf 只在「跨过阈值」这一次改变结果。
    val collapsed by remember(scrollState, collapseThreshold) {
        derivedStateOf { scrollState.value > collapseThreshold }
    }

    AnimatedContent(
        targetState = collapsed,
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(durationMillis = motion.fast)) +
                slideInVertically(tween(durationMillis = motion.fast)) { height -> -height / 4 } togetherWith
                fadeOut(tween(durationMillis = motion.instant))
        },
        label = "omTopBarCollapse",
    ) { isCollapsed ->
        OmTopBar(
            title = title,
            label = if (isCollapsed) null else label,
            large = !isCollapsed,
            onBack = onBack,
            actions = actions,
        )
    }
}
