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

public val Assets.List: ImageVector
    get() {
        if (_list != null) {
            return _list!!
        }
        _list = Builder(name = "List", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0f, 6.0007f)
                lineTo(21.0f, 6.0014f)
                moveTo(8.0f, 12.0007f)
                lineTo(21.0f, 12.0015f)
                moveTo(8.0f, 18.0007f)
                lineTo(21.0f, 18.0015f)
                moveTo(3.5f, 6.0f)
                horizontalLineTo(3.51f)
                moveTo(3.5f, 12.0f)
                horizontalLineTo(3.51f)
                moveTo(3.5f, 18.0f)
                horizontalLineTo(3.51f)
                moveTo(4.0f, 6.0f)
                curveTo(4.0f, 6.2761f, 3.7761f, 6.5f, 3.5f, 6.5f)
                curveTo(3.2239f, 6.5f, 3.0f, 6.2761f, 3.0f, 6.0f)
                curveTo(3.0f, 5.7239f, 3.2239f, 5.5f, 3.5f, 5.5f)
                curveTo(3.7761f, 5.5f, 4.0f, 5.7239f, 4.0f, 6.0f)
                close()
                moveTo(4.0f, 12.0f)
                curveTo(4.0f, 12.2761f, 3.7761f, 12.5f, 3.5f, 12.5f)
                curveTo(3.2239f, 12.5f, 3.0f, 12.2761f, 3.0f, 12.0f)
                curveTo(3.0f, 11.7239f, 3.2239f, 11.5f, 3.5f, 11.5f)
                curveTo(3.7761f, 11.5f, 4.0f, 11.7239f, 4.0f, 12.0f)
                close()
                moveTo(4.0f, 18.0f)
                curveTo(4.0f, 18.2761f, 3.7761f, 18.5f, 3.5f, 18.5f)
                curveTo(3.2239f, 18.5f, 3.0f, 18.2761f, 3.0f, 18.0f)
                curveTo(3.0f, 17.7239f, 3.2239f, 17.5f, 3.5f, 17.5f)
                curveTo(3.7761f, 17.5f, 4.0f, 17.7239f, 4.0f, 18.0f)
                close()
            }
        }
        .build()
        return _list!!
    }

private var _list: ImageVector? = null
