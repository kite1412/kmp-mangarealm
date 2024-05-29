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

public val Assets.`Book-open-solid`: ImageVector
    get() {
        if (`_book-open-solid` != null) {
            return `_book-open-solid`!!
        }
        `_book-open-solid` = Builder(name = "Book-open-solid", defaultWidth = 24.0.dp, defaultHeight
                = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(11.49f, 4.11f)
                arcToRelative(10.451f, 10.451f, 0.0f, false, false, -9.26f, -0.74f)
                arcToRelative(1.163f, 1.163f, 0.0f, false, false, -0.731f, 1.08f)
                verticalLineToRelative(11.66f)
                curveToRelative(0.0f, 0.78f, 0.789f, 1.314f, 1.514f, 1.024f)
                arcToRelative(8.558f, 8.558f, 0.0f, false, true, 7.582f, 0.608f)
                lineToRelative(1.135f, 0.68f)
                curveToRelative(0.087f, 0.053f, 0.18f, 0.075f, 0.269f, 0.074f)
                arcToRelative(0.503f, 0.503f, 0.0f, false, false, 0.27f, -0.073f)
                lineToRelative(1.134f, -0.681f)
                arcToRelative(8.558f, 8.558f, 0.0f, false, true, 7.582f, -0.608f)
                arcToRelative(1.104f, 1.104f, 0.0f, false, false, 1.514f, -1.025f)
                lineTo(22.499f, 4.45f)
                curveToRelative(0.0f, -0.476f, -0.29f, -0.903f, -0.731f, -1.08f)
                arcToRelative(10.451f, 10.451f, 0.0f, false, false, -9.259f, 0.742f)
                lineToRelative(-0.51f, 0.306f)
                lineToRelative(-0.51f, -0.306f)
                close()
                moveTo(12.75f, 6.5f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, -1.5f, 0.0f)
                lineTo(11.25f, 16.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 1.5f, 0.0f)
                lineTo(12.75f, 6.5f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(2.725f, 19.042f)
                arcToRelative(6.5f, 6.5f, 0.0f, false, true, 6.55f, 0.0f)
                lineToRelative(1.087f, 0.634f)
                arcToRelative(3.25f, 3.25f, 0.0f, false, false, 3.276f, 0.0f)
                lineToRelative(1.087f, -0.634f)
                arcToRelative(6.5f, 6.5f, 0.0f, false, true, 6.55f, 0.0f)
                lineToRelative(0.103f, 0.06f)
                arcToRelative(0.75f, 0.75f, 0.0f, true, true, -0.756f, 1.296f)
                lineToRelative(-0.103f, -0.06f)
                arcToRelative(5.0f, 5.0f, 0.0f, false, false, -5.038f, 0.0f)
                lineToRelative(-1.088f, 0.634f)
                arcToRelative(4.75f, 4.75f, 0.0f, false, true, -4.786f, 0.0f)
                lineToRelative(-1.088f, -0.634f)
                arcToRelative(5.0f, 5.0f, 0.0f, false, false, -5.038f, 0.0f)
                lineToRelative(-0.103f, 0.06f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, -0.756f, -1.296f)
                lineToRelative(0.103f, -0.06f)
                close()
            }
        }
        .build()
        return `_book-open-solid`!!
    }

private var `_book-open-solid`: ImageVector? = null
