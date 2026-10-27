package io.openmetro.ui.core.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

/**
 * 当前是否从右往左排版。
 *
 * `DrawScope` 的坐标是绝对像素，不随 `LayoutDirection` 翻转 —— 所有用 `drawBehind`
 * 画在 start 侧的装饰（竖条、色条、指示条）都必须自己判断方向，否则 RTL 下会跑到错误的一侧。
 *
 * 布局层面的镜像不用管：`Row`、`padding(start =)`、`Alignment.CenterStart` 都会自动翻转。
 */
@Composable
@ReadOnlyComposable
fun rememberIsRtl(): Boolean =
    LocalLayoutDirection.current == LayoutDirection.Rtl
