package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Book-outline`: ImageVector
    get() {
        if (`_book-outline` != null) {
            return `_book-outline`!!
        }
        `_book-outline` = Builder(name = "Book-outline", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(8.25f, 8.0f)
                arcTo(0.75f, 0.75f, 0.0f, false, true, 9.0f, 7.25f)
                horizontalLineToRelative(7.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, 1.5f)
                horizontalLineTo(9.0f)
                arcTo(0.75f, 0.75f, 0.0f, false, true, 8.25f, 8.0f)
                close()
                moveTo(9.0f, 10.25f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, 1.5f)
                horizontalLineToRelative(5.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, -1.5f)
                horizontalLineTo(9.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(8.5f, 3.25f)
                arcTo(4.75f, 4.75f, 0.0f, false, false, 3.75f, 8.0f)
                verticalLineToRelative(10.0f)
                arcToRelative(3.75f, 3.75f, 0.0f, false, false, 3.75f, 3.75f)
                horizontalLineToRelative(11.0f)
                arcTo(1.75f, 1.75f, 0.0f, false, false, 20.25f, 20.0f)
                lineTo(20.25f, 5.0f)
                arcToRelative(1.75f, 1.75f, 0.0f, false, false, -1.75f, -1.75f)
                horizontalLineToRelative(-10.0f)
                close()
                moveTo(18.75f, 14.25f)
                lineTo(18.75f, 5.0f)
                arcToRelative(0.25f, 0.25f, 0.0f, false, false, -0.25f, -0.25f)
                horizontalLineToRelative(-10.0f)
                arcTo(3.25f, 3.25f, 0.0f, false, false, 5.25f, 8.0f)
                verticalLineToRelative(7.0f)
                arcToRelative(3.734f, 3.734f, 0.0f, false, true, 2.25f, -0.75f)
                horizontalLineToRelative(11.25f)
                close()
                moveTo(18.75f, 15.75f)
                lineTo(7.5f, 15.75f)
                arcToRelative(2.25f, 2.25f, 0.0f, false, false, 0.0f, 4.5f)
                horizontalLineToRelative(11.0f)
                arcToRelative(0.25f, 0.25f, 0.0f, false, false, 0.25f, -0.25f)
                verticalLineToRelative(-4.25f)
                close()
            }
        }
        .build()
        return `_book-outline`!!
    }

private var `_book-outline`: ImageVector? = null
