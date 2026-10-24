package io.openmetro.ui.component.tile

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.layout.OmGrid
import io.openmetro.ui.core.theme.OmTheme
import io.openmetro.ui.core.theme.OmTileColor
import io.openmetro.ui.core.theme.OmTileTone

/**
 * 磁贴：色块即内容，整块可点。
 *
 * **尺寸不由自己决定** —— 由调用方通过 `OmGrid` 的跨栏与宽高比给出：
 * ```
 * OmTile("邮件", Modifier.omGridSpan(2).aspectRatio(1f))
 * OmTile("音乐", Modifier.omGridSpan(4).aspectRatio(2f))
 * ```
 * 标题字号按实际宽度自适应（<100dp 用 18sp，<160dp 用 28sp，否则 36sp）。
 *
 * 颜色由 [tone] 声明语义，实际取色由当前 `OmTileMode` 决定：切到 Spectrum 时
 * 磁贴会自动换成色相组，调用点不用改。[color] 只在需要精确指定时使用。
 *
 * @param backTitle 非空时启用来回翻转：第一次点翻到背面，再点翻回。背面必须是信息，
 *   不能只是装饰 —— 否则用户点两次只为了看一个动画。
 * @param rotatingTitles 非空且至少两项时启用**内容轮播**（Metro Live Tile）：
 *   正面标题每隔 [rotateIntervalMillis] 换一条。与 [backTitle] 互不干扰 ——
 *   轮播只作用在正面的标题上，点击仍然翻面。
 * @param onClick 没有背面时点击的回调
 */
@Composable
fun OmTile(
    title: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    tone: OmTileTone = OmTileTone.Ink,
    color: OmTileColor? = null,
    backTitle: String? = null,
    titleStyle: TextStyle? = null,
    rotatingTitles: List<String> = emptyList(),
    rotateIntervalMillis: Long = 8000,
    onClick: (() -> Unit)? = null,
) {
    val motion = OmTheme.motion
    val hasBack = backTitle != null
    val tileColor = color ?: OmTheme.colors.tiles.resolve(tone, OmTheme.tileMode)

    // Paper 色调与页面底色的明度差很小（浅色下白对米白、深色下深灰对近黑），
    // 不描边就看不出磁贴的边界。规范里写了 1dp 描边，这里补上。
    val outlined = color == null && tone == OmTileTone.Paper

    var rotateIndex by remember(rotatingTitles) { mutableIntStateOf(0) }
    LaunchedEffect(rotatingTitles, rotateIntervalMillis) {
        if (rotatingTitles.size < 2) return@LaunchedEffect
        while (true) {
            delay(rotateIntervalMillis)
            rotateIndex = (rotateIndex + 1) % rotatingTitles.size
        }
    }
    val faceTitle = if (rotatingTitles.size >= 2) rotatingTitles[rotateIndex] else title

    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = motion.slow, easing = motion.standard),
        label = "omTileFlip",
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                // 不设 cameraDistance 时透视会强到让磁贴看起来要飞出来。
                cameraDistance = 12f * density
            }
            .background(tileColor.surface)
            .then(
                if (outlined) {
                    Modifier.border(
                        width = OmTheme.shapes.hairline,
                        color = OmTheme.colors.line,
                    )
                } else {
                    Modifier
                },
            )
            .omFocusRing()
            .omClickable(role = Role.Button) {
                if (hasBack) flipped = !flipped else onClick?.invoke()
            },
    ) {
        if (rotation <= 90f) {
            TileFace(title = faceTitle, label = label, color = tileColor, titleStyle = titleStyle)
        } else {
            // 背面再转 180°，否则文字是镜像的
            Box(Modifier.fillMaxSize().graphicsLayer { rotationY = 180f }) {
                TileFace(title = backTitle.orEmpty(), label = null, color = tileColor)
            }
        }
    }
}

@Composable
private fun TileFace(
    title: String,
    label: String?,
    color: OmTileColor,
    titleStyle: TextStyle? = null,
) {
    val typography = OmTheme.typography
    val spacing = OmTheme.spacing

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().padding(spacing.space4),
        contentAlignment = Alignment.BottomStart,
    ) {
        val style = titleStyle ?: when {
            maxWidth < SmallTileWidth -> typography.subtitle
            maxWidth < MediumTileWidth -> typography.headline
            else -> typography.displayM
        }

        Column {
            if (label != null) {
                OmText(label, style = typography.label, color = color.onSurface)
                Spacer(Modifier.height(spacing.space1))
            }
            OmText(
                text = title,
                style = style,
                color = color.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private val SmallTileWidth: Dp = 100.dp
private val MediumTileWidth: Dp = 160.dp

/**
 * 磁贴网格：只是 `OmGrid` 的语义包装。
 *
 * 单独成组件是为了以后能加「出血（bleed）」与尺寸预设，而不改 `OmGrid` 的通用行为。
 */
@Composable
fun OmTileGrid(
    modifier: Modifier = Modifier,
    columns: Int = OmTheme.grid.columns,
    gutter: Dp = OmTheme.grid.gutter,
    content: @Composable () -> Unit,
) {
    OmGrid(
        modifier = modifier,
        columns = columns,
        gutter = gutter,
        content = content,
    )
}
