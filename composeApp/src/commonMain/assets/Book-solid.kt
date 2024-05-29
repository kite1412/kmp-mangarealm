package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Book-solid`: ImageVector
    get() {
        if (`_book-solid` != null) {
            return `_book-solid`!!
        }
        `_book-solid` = Builder(name = "Book-solid", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(4.0f, 8.0f)
                arcToRelative(4.5f, 4.5f, 0.0f, false, true, 4.5f, -4.5f)
                horizontalLineToRelative(10.0f)
                arcTo(1.5f, 1.5f, 0.0f, false, true, 20.0f, 5.0f)
                verticalLineToRelative(15.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, -1.0f, 1.0f)
                lineTo(7.5f, 21.0f)
                arcToRelative(3.5f, 3.5f, 0.0f, false, true, -3.465f, -3.0f)
                lineTo(4.0f, 18.0f)
                lineTo(4.0f, 8.0f)
                close()
                moveTo(18.5f, 15.5f)
                horizontalLineToRelative(-11.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, false, 0.0f, 4.0f)
                horizontalLineToRelative(11.0f)
                verticalLineToRelative(-4.0f)
                close()
                moveTo(8.25f, 8.0f)
                arcTo(0.75f, 0.75f, 0.0f, false, true, 9.0f, 7.25f)
                horizontalLineToRelative(7.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, 1.5f)
                lineTo(9.0f, 8.75f)
                arcTo(0.75f, 0.75f, 0.0f, false, true, 8.25f, 8.0f)
                close()
                moveTo(9.0f, 10.25f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, 1.5f)
                horizontalLineToRelative(5.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, -1.5f)
                lineTo(9.0f, 10.25f)
                close()
            }
        }
        .build()
        return `_book-solid`!!
    }

private var `_book-solid`: ImageVector? = null
