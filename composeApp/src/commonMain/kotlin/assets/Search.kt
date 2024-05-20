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

public val Assets.Search: ImageVector
    get() {
        if (_search != null) {
            return _search!!
        }
        _search = Builder(name = "Search", defaultWidth = 64.0.dp, defaultHeight = 64.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(15.0f, 15.0f)
                lineTo(21.0f, 21.0f)
                moveTo(10.0f, 17.0f)
                curveTo(6.134f, 17.0f, 3.0f, 13.866f, 3.0f, 10.0f)
                curveTo(3.0f, 6.134f, 6.134f, 3.0f, 10.0f, 3.0f)
                curveTo(13.866f, 3.0f, 17.0f, 6.134f, 17.0f, 10.0f)
                curveTo(17.0f, 13.866f, 13.866f, 17.0f, 10.0f, 17.0f)
                close()
            }
        }
        .build()
        return _search!!
    }

private var _search: ImageVector? = null
