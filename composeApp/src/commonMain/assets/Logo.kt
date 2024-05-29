package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
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

public val Assets.Logo: ImageVector
    get() {
        if (_logo != null) {
            return _logo!!
        }
        _logo = Builder(name = "Logo", defaultWidth = 29.207552.dp, defaultHeight = 12.732315.dp,
                viewportWidth = 29.207554f, viewportHeight = 12.732315f).apply {
            path(fill = SolidColor(Color(0xFF003987)), stroke = SolidColor(Color(0xFF003852)),
                    strokeLineWidth = 0.426875f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(18.149f, 5.9365f)
                horizontalLineToRelative(3.486f)
                lineToRelative(0.007f, -1.5672f)
                lineToRelative(-3.493f, 0.006f)
                close()
                moveTo(19.637f, 8.1306f)
                horizontalLineToRelative(-1.488f)
                verticalLineToRelative(1.8807f)
                horizontalLineToRelative(-3.493f)
                verticalLineToRelative(-7.2093f)
                quadToRelative(0.0f, -0.6269f, 1.0549f, -0.6269f)
                horizontalLineToRelative(8.3763f)
                quadToRelative(1.0479f, 0.0f, 1.0479f, 0.6269f)
                lineToRelative(-0.007f, 4.7017f)
                quadToRelative(0.0f, 0.6269f, -1.0409f, 0.6269f)
                horizontalLineToRelative(-0.6078f)
                lineToRelative(5.1487f, 4.3883f)
                horizontalLineToRelative(-3.8424f)
                close()
            }
            path(fill = SolidColor(Color(0xFFb11818)), stroke = SolidColor(Color(0xFF950000)),
                    strokeLineWidth = 0.426875f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(6.8502f, 7.4791f)
                lineToRelative(-3.1437f, -2.564f)
                verticalLineToRelative(3.1345f)
                horizontalLineToRelative(-3.493f)
                verticalLineToRelative(-7.2093f)
                quadToRelative(0.0f, -0.6269f, 1.0479f, -0.6269f)
                horizontalLineToRelative(2.0958f)
                lineToRelative(3.493f, 3.1345f)
                lineToRelative(3.493f, -3.1345f)
                horizontalLineToRelative(2.0958f)
                quadToRelative(1.0479f, 0.0f, 1.0479f, 0.6269f)
                verticalLineToRelative(7.2093f)
                horizontalLineToRelative(-3.493f)
                verticalLineToRelative(-3.1345f)
                close()
            }
        }
        .build()
        return _logo!!
    }

private var _logo: ImageVector? = null
