package io.openmetro.ui.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.openmetro.ui.component.base.OmIcon
import io.openmetro.ui.core.interaction.omClickable
import io.openmetro.ui.core.interaction.omFocusRing
import io.openmetro.ui.core.theme.OmTheme

/**
 * 图标按钮：48dp 触摸区 + 24dp 图标，无底色、无描边。
 *
 * 页头的操作区、工具栏、列表尾随操作都用它。视觉尺寸与触摸区是两件事——
 * 图标只有 24dp，但可点区域是 48dp。
 *
 * @param tint 传 null 时跟随 `textPrimary`；选中态或强调操作传 `colors.accent`
 */
@Composable
fun OmIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    touchSize: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    tint: Color? = null,
) {
    val colors = OmTheme.colors

    val resolvedTint = when {
        !enabled -> colors.textDisabled
        tint != null -> tint
        else -> colors.textPrimary
    }

    Box(
        modifier = modifier
            .omFocusRing()
            .size(touchSize)
            .omClickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        OmIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            size = iconSize,
            tint = resolvedTint,
        )
    }
}
