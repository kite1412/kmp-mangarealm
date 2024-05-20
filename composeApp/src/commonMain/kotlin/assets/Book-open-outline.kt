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

public val Assets.`Book-open-outline`: ImageVector
    get() {
        if (`_book-open-outline` != null) {
            return `_book-open-outline`!!
        }
        `_book-open-outline` = Builder(name = "Book-open-outline", defaultWidth = 256.0.dp,
                defaultHeight = 256.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(11.602f, 18.636f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.398f, 0.11f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.398f, -0.11f)
                lineToRelative(1.135f, -0.681f)
                arcToRelative(8.308f, 8.308f, 0.0f, false, true, 7.36f, -0.59f)
                curveToRelative(0.89f, 0.356f, 1.857f, -0.3f, 1.857f, -1.257f)
                lineTo(22.75f, 4.45f)
                curveToRelative(0.0f, -0.578f, -0.352f, -1.097f, -0.889f, -1.312f)
                arcToRelative(10.701f, 10.701f, 0.0f, false, false, -9.48f, 0.76f)
                lineTo(12.0f, 4.124f)
                lineToRelative(-0.382f, -0.229f)
                arcToRelative(10.701f, 10.701f, 0.0f, false, false, -9.48f, -0.76f)
                arcTo(1.413f, 1.413f, 0.0f, false, false, 1.25f, 4.45f)
                verticalLineToRelative(11.66f)
                curveToRelative(0.0f, 0.957f, 0.967f, 1.612f, 1.857f, 1.256f)
                arcToRelative(8.308f, 8.308f, 0.0f, false, true, 7.36f, 0.59f)
                lineToRelative(1.135f, 0.681f)
                close()
                moveTo(2.75f, 4.508f)
                verticalLineToRelative(11.387f)
                arcToRelative(9.809f, 9.809f, 0.0f, false, true, 8.489f, 0.774f)
                lineToRelative(0.011f, 0.006f)
                lineTo(11.25f, 5.425f)
                lineToRelative(-0.403f, -0.242f)
                arcToRelative(9.201f, 9.201f, 0.0f, false, false, -8.097f, -0.675f)
                close()
                moveTo(12.761f, 16.668f)
                lineToRelative(-0.011f, 0.007f)
                lineTo(12.75f, 5.425f)
                lineToRelative(0.403f, -0.242f)
                arcToRelative(9.201f, 9.201f, 0.0f, false, true, 8.097f, -0.675f)
                verticalLineToRelative(11.387f)
                arcToRelative(9.809f, 9.809f, 0.0f, false, false, -8.489f, 0.774f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.275f, 19.042f)
                arcToRelative(6.5f, 6.5f, 0.0f, false, false, -6.55f, 0.0f)
                lineToRelative(-0.103f, 0.06f)
                arcToRelative(0.75f, 0.75f, 0.0f, true, false, 0.756f, 1.296f)
                lineToRelative(0.103f, -0.06f)
                arcToRelative(5.0f, 5.0f, 0.0f, false, true, 5.038f, 0.0f)
                lineToRelative(1.088f, 0.634f)
                arcToRelative(4.75f, 4.75f, 0.0f, false, false, 4.786f, 0.0f)
                lineToRelative(1.088f, -0.634f)
                arcToRelative(5.0f, 5.0f, 0.0f, false, true, 5.038f, 0.0f)
                lineToRelative(0.103f, 0.06f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.756f, -1.296f)
                lineToRelative(-0.103f, -0.06f)
                arcToRelative(6.5f, 6.5f, 0.0f, false, false, -6.55f, 0.0f)
                lineToRelative(-1.087f, 0.634f)
                arcToRelative(3.25f, 3.25f, 0.0f, false, true, -3.276f, 0.0f)
                lineToRelative(-1.087f, -0.634f)
                close()
            }
        }
        .build()
        return `_book-open-outline`!!
    }

private var `_book-open-outline`: ImageVector? = null
