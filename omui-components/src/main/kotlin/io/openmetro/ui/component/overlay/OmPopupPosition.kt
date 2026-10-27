package io.openmetro.ui.component.overlay

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

/**
 * 贴锚点弹出，并在窗口边缘自动翻转。
 *
 * 默认放下方；下方放不下且上方有更多空间时翻到上方。水平方向按 `LayoutDirection`
 * 对齐到锚点的 start 边，同时保证不越出窗口。
 *
 * 这对下拉菜单、选择面板都是必需的：靠近屏幕底部的触发器如果硬往下弹，
 * 面板会被裁掉一半，用户看不到后面的选项。
 */
internal class OmAnchorPositionProvider(
    private val extraOffset: IntOffset = IntOffset.Zero,
    /** 面板宽度是否跟随锚点宽度（下拉选择需要，菜单不需要）。 */
    private val matchAnchorWidth: Boolean = false,
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val spaceBelow = windowSize.height - anchorBounds.bottom
        val spaceAbove = anchorBounds.top
        val popupHeight = popupContentSize.height

        // 向下弹时把偏移加到顶部，向上弹时要减掉 —— 否则面板会压住锚点
        val y = when {
            spaceBelow >= popupHeight -> anchorBounds.bottom + extraOffset.y
            spaceAbove >= popupHeight -> anchorBounds.top - popupHeight - extraOffset.y
            // 两边都放不下时，往空间更大的一侧弹
            spaceBelow >= spaceAbove -> anchorBounds.bottom + extraOffset.y
            else -> anchorBounds.top - popupHeight - extraOffset.y
        }.coerceAtLeast(0)

        val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
        val x = if (matchAnchorWidth) {
            anchorBounds.left
        } else if (layoutDirection == LayoutDirection.Rtl) {
            anchorBounds.right - popupContentSize.width
        } else {
            anchorBounds.left
        }.coerceIn(0, maxX)

        return IntOffset(x + extraOffset.x, y)
    }
}
