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

public val Assets.`Arrow-left-solid`: ImageVector
    get() {
        if (`_arrow-left-solid` != null) {
            return `_arrow-left-solid`!!
        }
        `_arrow-left-solid` = Builder(name = "Arrow-left-solid", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(11.03f, 8.53f)
                arcToRelative(0.75f, 0.75f, 0.0f, true, false, -1.06f, -1.06f)
                lineToRelative(-4.0f, 4.0f)
                arcToRelative(0.748f, 0.748f, 0.0f, false, false, 0.0f, 1.06f)
                lineToRelative(4.0f, 4.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, true, false, 1.06f, -1.06f)
                lineToRelative(-2.72f, -2.72f)
                horizontalLineTo(18.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, -1.5f)
                horizontalLineTo(8.31f)
                lineToRelative(2.72f, -2.72f)
                close()
            }
        }
        .build()
        return `_arrow-left-solid`!!
    }

private var `_arrow-left-solid`: ImageVector? = null
