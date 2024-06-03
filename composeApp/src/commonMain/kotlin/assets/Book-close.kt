package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Book-close`: ImageVector
    get() {
        if (`_book-close` != null) {
            return `_book-close`!!
        }
        `_book-close` = Builder(name = "Book-close", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.0f, 4.222f)
                verticalLineToRelative(15.556f)
                curveTo(4.0f, 21.005f, 5.023f, 22.0f, 6.286f, 22.0f)
                horizontalLineToRelative(11.428f)
                curveTo(18.977f, 22.0f, 20.0f, 21.005f, 20.0f, 19.778f)
                lineTo(20.0f, 8.444f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, false, -2.0f, -2.0f)
                lineTo(6.286f, 6.444f)
                curveTo(5.023f, 6.444f, 4.0f, 5.45f, 4.0f, 4.222f)
                close()
                moveTo(4.0f, 4.222f)
                curveTo(4.0f, 2.995f, 5.023f, 2.0f, 6.286f, 2.0f)
                horizontalLineToRelative(9.143f)
                curveToRelative(1.262f, 0.0f, 2.285f, 0.995f, 2.285f, 2.222f)
                verticalLineToRelative(2.222f)
            }
        }
        .build()
        return `_book-close`!!
    }

private var `_book-close`: ImageVector? = null
