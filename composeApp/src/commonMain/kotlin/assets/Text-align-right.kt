package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Text-align-right`: ImageVector
    get() {
        if (`_text-align-right` != null) {
            return `_text-align-right`!!
        }
        `_text-align-right` = Builder(name = "Text-align-right", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(3.0f, 6.0f)
                horizontalLineToRelative(18.0f)
                moveToRelative(-10.0f, 6.0f)
                horizontalLineToRelative(10.0f)
                moveTo(6.0f, 18.0f)
                horizontalLineToRelative(15.0f)
            }
        }
        .build()
        return `_text-align-right`!!
    }

private var `_text-align-right`: ImageVector? = null
