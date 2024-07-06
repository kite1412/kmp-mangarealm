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

public val Assets.Width: ImageVector
    get() {
        if (_width != null) {
            return _width!!
        }
        _width = Builder(name = "Width", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 1.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(22.0f, 12.0f)
                horizontalLineTo(2.0f)
                moveToRelative(20.0f, 0.0f)
                lineToRelative(-4.0f, 4.0f)
                moveToRelative(4.0f, -4.0f)
                lineToRelative(-4.0f, -4.0f)
                moveTo(2.0f, 12.0f)
                lineToRelative(4.0f, 4.0f)
                moveToRelative(-4.0f, -4.0f)
                lineToRelative(4.0f, -4.0f)
            }
        }
        .build()
        return _width!!
    }

private var _width: ImageVector? = null
