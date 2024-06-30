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

public val Assets.`Book-open`: ImageVector
    get() {
        if (`_book-open` != null) {
            return `_book-open`!!
        }
        `_book-open` = Builder(name = "Book-open", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(2.0f, 6.0f)
                reflectiveCurveToRelative(1.5f, -2.0f, 5.0f, -2.0f)
                reflectiveCurveToRelative(5.0f, 2.0f, 5.0f, 2.0f)
                verticalLineToRelative(14.0f)
                reflectiveCurveToRelative(-1.5f, -1.0f, -5.0f, -1.0f)
                reflectiveCurveToRelative(-5.0f, 1.0f, -5.0f, 1.0f)
                lineTo(2.0f, 6.0f)
                close()
                moveTo(12.0f, 6.0f)
                reflectiveCurveToRelative(1.5f, -2.0f, 5.0f, -2.0f)
                reflectiveCurveToRelative(5.0f, 2.0f, 5.0f, 2.0f)
                verticalLineToRelative(14.0f)
                reflectiveCurveToRelative(-1.5f, -1.0f, -5.0f, -1.0f)
                reflectiveCurveToRelative(-5.0f, 1.0f, -5.0f, 1.0f)
                lineTo(12.0f, 6.0f)
                close()
            }
        }
        .build()
        return `_book-open`!!
    }

private var `_book-open`: ImageVector? = null
