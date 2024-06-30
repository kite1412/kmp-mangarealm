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

public val Assets.`List-add`: ImageVector
    get() {
        if (`_list-add` != null) {
            return `_list-add`!!
        }
        `_list-add` = Builder(name = "List-add", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(3.0f, 17.0f)
                horizontalLineTo(10.0f)
                moveTo(15.0f, 16.0f)
                horizontalLineTo(18.0f)
                moveTo(18.0f, 16.0f)
                horizontalLineTo(21.0f)
                moveTo(18.0f, 16.0f)
                verticalLineTo(19.0f)
                moveTo(18.0f, 16.0f)
                verticalLineTo(13.0f)
                moveTo(3.0f, 12.0f)
                horizontalLineTo(14.0f)
                moveTo(3.0f, 7.0f)
                horizontalLineTo(14.0f)
            }
        }
        .build()
        return `_list-add`!!
    }

private var `_list-add`: ImageVector? = null
