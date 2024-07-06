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

public val Assets.Height: ImageVector
    get() {
        if (_height != null) {
            return _height!!
        }
        _height = Builder(name = "Height", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 1.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.0f, 22.0f)
                verticalLineTo(2.0f)
                moveToRelative(0.0f, 20.0f)
                lineToRelative(-4.0f, -4.0f)
                moveToRelative(4.0f, 4.0f)
                lineToRelative(4.0f, -4.0f)
                moveTo(12.0f, 2.0f)
                lineTo(8.0f, 6.0f)
                moveToRelative(4.0f, -4.0f)
                lineToRelative(4.0f, 4.0f)
            }
        }
        .build()
        return _height!!
    }

private var _height: ImageVector? = null
