package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.Books: ImageVector
    get() {
        if (_books != null) {
            return _books!!
        }
        _books = Builder(name = "Books", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(28.9f, 9.4f)
                curveTo(28.9f, 9.4f, 28.9f, 9.4f, 28.9f, 9.4f)
                curveTo(28.9f, 9.3f, 29.0f, 9.2f, 29.0f, 9.1f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f)
                curveToRelative(0.0f, -0.1f, 0.0f, -0.2f, 0.0f, -0.3f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f)
                curveToRelative(0.0f, -0.1f, -0.1f, -0.2f, -0.1f, -0.3f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                curveToRelative(-0.1f, -0.1f, -0.1f, -0.1f, -0.2f, -0.2f)
                lineToRelative(-11.0f, -7.0f)
                curveToRelative(-0.3f, -0.2f, -0.8f, -0.2f, -1.1f, 0.0f)
                lineToRelative(-13.0f, 9.0f)
                curveToRelative(0.0f, 0.0f, -0.1f, 0.1f, -0.1f, 0.1f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, -0.1f, 0.0f)
                curveToRelative(-0.1f, 0.1f, -0.1f, 0.2f, -0.2f, 0.3f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f)
                curveTo(3.0f, 10.8f, 3.0f, 10.9f, 3.0f, 11.0f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                verticalLineToRelative(6.0f)
                verticalLineToRelative(6.0f)
                curveToRelative(0.0f, 0.3f, 0.2f, 0.7f, 0.5f, 0.8f)
                lineToRelative(11.0f, 7.0f)
                curveToRelative(0.2f, 0.1f, 0.4f, 0.2f, 0.5f, 0.2f)
                curveToRelative(0.2f, 0.0f, 0.4f, -0.1f, 0.6f, -0.2f)
                lineToRelative(13.0f, -9.0f)
                curveToRelative(0.2f, -0.2f, 0.4f, -0.4f, 0.4f, -0.7f)
                reflectiveCurveToRelative(-0.1f, -0.6f, -0.3f, -0.8f)
                curveToRelative(-0.9f, -0.9f, -1.1f, -2.2f, -0.5f, -3.4f)
                lineToRelative(0.7f, -1.5f)
                curveToRelative(0.0f, -0.1f, 0.1f, -0.2f, 0.1f, -0.3f)
                curveToRelative(0.0f, 0.0f, 0.0f, -0.1f, 0.0f, -0.1f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                curveToRelative(0.0f, -0.1f, 0.0f, -0.3f, -0.1f, -0.4f)
                curveToRelative(0.0f, 0.0f, 0.0f, -0.1f, 0.0f, -0.1f)
                curveToRelative(0.0f, -0.1f, -0.1f, -0.2f, -0.2f, -0.3f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                curveToRelative(-0.9f, -0.9f, -1.1f, -2.2f, -0.5f, -3.4f)
                lineTo(28.9f, 9.4f)
                close()
                moveTo(26.6f, 14.8f)
                lineToRelative(-11.6f, 8.0f)
                lineTo(5.0f, 16.5f)
                verticalLineToRelative(-3.6f)
                lineToRelative(9.5f, 6.0f)
                curveToRelative(0.2f, 0.1f, 0.4f, 0.2f, 0.5f, 0.2f)
                curveToRelative(0.2f, 0.0f, 0.4f, -0.1f, 0.6f, -0.2f)
                lineToRelative(10.3f, -7.1f)
                curveTo(25.8f, 12.8f, 26.0f, 13.8f, 26.6f, 14.8f)
                close()
                moveTo(15.0f, 28.8f)
                lineTo(5.0f, 22.5f)
                verticalLineToRelative(-3.6f)
                lineToRelative(9.5f, 6.0f)
                curveToRelative(0.2f, 0.1f, 0.4f, 0.2f, 0.5f, 0.2f)
                curveToRelative(0.2f, 0.0f, 0.4f, -0.1f, 0.6f, -0.2f)
                lineToRelative(10.3f, -7.1f)
                curveToRelative(-0.1f, 1.1f, 0.1f, 2.2f, 0.7f, 3.1f)
                lineTo(15.0f, 28.8f)
                close()
            }
        }
        .build()
        return _books!!
    }

private var _books: ImageVector? = null
