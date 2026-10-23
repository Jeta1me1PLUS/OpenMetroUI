package io.openmetro.ui.component.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.openmetro.ui.component.base.OmText
import io.openmetro.ui.core.theme.OmTheme
import java.util.Locale

/**
 * 圆形头像。圆形是语义需要（人脸、人的标识），不是装饰。
 *
 * 没有图片时用首字母 + 中性色底。字重 600、字号约为直径的 0.4 倍，
 * 不用固定字号 —— 否则 24dp 与 56dp 的头像会一个太挤一个太空。
 */
@Composable
fun OmAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
) {
    val colors = OmTheme.colors
    val initial = name.trim().firstOrNull()
        ?.let { it.uppercase(Locale.ROOT) }
        .orEmpty()

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        OmText(
            text = initial,
            style = TextStyle(
                fontSize = (size.value * 0.4f).sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = size.value.sp,
            ),
            color = colors.textPrimary,
            maxLines = 1,
        )
    }
}
