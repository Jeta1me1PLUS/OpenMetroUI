package io.openmetro.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.unit.dp

/**
 * 几何图标集。
 *
 * 规则：24dp 画布、1.5dp 描边、**方头端点（Butt）与直角拐角（Miter）**。
 * 圆头端点会把 Material 的柔和气质带回来，这里刻意不用。
 *
 * 描边色一律给黑色占位，实际渲染时由 `OmIcon` 的 tint 覆盖。
 */
object OmIcons {

    val ArrowBack: ImageVector by lazy {
        strokeIcon("Om.ArrowBack") {
            moveTo(14f, 4f)
            lineTo(6f, 12f)
            lineTo(14f, 20f)
        }
    }

    val ArrowForward: ImageVector by lazy {
        strokeIcon("Om.ArrowForward") {
            moveTo(10f, 4f)
            lineTo(18f, 12f)
            lineTo(10f, 20f)
        }
    }

    val Close: ImageVector by lazy {
        strokeIcon("Om.Close") {
            moveTo(6f, 6f)
            lineTo(18f, 18f)
            moveTo(18f, 6f)
            lineTo(6f, 18f)
        }
    }

    val Check: ImageVector by lazy {
        strokeIcon("Om.Check") {
            moveTo(4f, 13f)
            lineTo(9f, 18f)
            lineTo(20f, 6f)
        }
    }

    val Add: ImageVector by lazy {
        strokeIcon("Om.Add") {
            moveTo(12f, 4f)
            lineTo(12f, 20f)
            moveTo(4f, 12f)
            lineTo(20f, 12f)
        }
    }

    val ChevronEnd: ImageVector by lazy {
        strokeIcon("Om.ChevronEnd") {
            moveTo(9f, 4f)
            lineTo(17f, 12f)
            lineTo(9f, 20f)
        }
    }

    val Menu: ImageVector by lazy {
        strokeIcon("Om.Menu") {
            moveTo(4f, 6f)
            lineTo(20f, 6f)
            moveTo(4f, 12f)
            lineTo(20f, 12f)
            moveTo(4f, 18f)
            lineTo(20f, 18f)
        }
    }

    val Search: ImageVector by lazy {
        strokeIcon("Om.Search") {
            moveTo(4f, 10f)
            arcTo(6f, 6f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 16f, y1 = 10f)
            arcTo(6f, 6f, 0f, isMoreThanHalf = true, isPositiveArc = true, x1 = 4f, y1 = 10f)
            moveTo(14.5f, 14.5f)
            lineTo(20f, 20f)
        }
    }

    /** 三点菜单。用三个小方块而不是三个圆点 —— 直角是这套语言的一部分。 */
    val MoreVert: ImageVector by lazy {
        fillIcon("Om.MoreVert") {
            square(11f, 4f)
            square(11f, 11f)
            square(11f, 18f)
        }
    }

    /** 四宫格，用于「总览 / 规范」入口。 */
    val Grid: ImageVector by lazy {
        strokeIcon("Om.Grid") {
            squareOutline(4f, 4f, 6f)
            squareOutline(14f, 4f, 6f)
            squareOutline(4f, 14f, 6f)
            squareOutline(14f, 14f, 6f)
        }
    }

    /** 两条滑轨 + 两个滑块，用于「设置」。 */
    val Sliders: ImageVector by lazy {
        strokeIcon("Om.Sliders") {
            moveTo(4f, 8f)
            lineTo(20f, 8f)
            moveTo(9f, 5f)
            lineTo(9f, 11f)
            moveTo(4f, 16f)
            lineTo(20f, 16f)
            moveTo(15f, 13f)
            lineTo(15f, 19f)
        }
    }

    /** 烧瓶，用于「实验台」。 */
    val Beaker: ImageVector by lazy {
        strokeIcon("Om.Beaker") {
            moveTo(9f, 4f)
            lineTo(15f, 4f)
            moveTo(11f, 4f)
            lineTo(11f, 10f)
            lineTo(5f, 20f)
            lineTo(19f, 20f)
            lineTo(13f, 10f)
            lineTo(13f, 4f)
        }
    }

    /** 三行不等长横线，用于「资讯 / 列表」。 */
    val Article: ImageVector by lazy {
        strokeIcon("Om.Article") {
            moveTo(4f, 5f)
            lineTo(20f, 5f)
            moveTo(4f, 11f)
            lineTo(20f, 11f)
            moveTo(4f, 17f)
            lineTo(13f, 17f)
        }
    }
}

private fun PathBuilder.squareOutline(x: Float, y: Float, size: Float) {
    moveTo(x, y)
    lineTo(x + size, y)
    lineTo(x + size, y + size)
    lineTo(x, y + size)
    close()
}

private const val IconSize = 24f
private const val StrokeWidth = 1.5f

private fun PathBuilder.square(x: Float, y: Float, size: Float = 2f) {
    moveTo(x, y)
    lineTo(x + size, y)
    lineTo(x + size, y + size)
    lineTo(x, y + size)
    close()
}

private fun strokeIcon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = IconSize,
        viewportHeight = IconSize,
    ).apply {
        addPath(
            pathData = PathData(pathBuilder),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = StrokeWidth,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
        )
    }.build()

private fun fillIcon(name: String, pathBuilder: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = IconSize,
        viewportHeight = IconSize,
    ).apply {
        addPath(
            pathData = PathData(pathBuilder),
            fill = SolidColor(Color.Black),
        )
    }.build()
