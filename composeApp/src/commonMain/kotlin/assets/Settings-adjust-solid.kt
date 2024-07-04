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

public val Assets.`Settings-adjust-solid`: ImageVector
    get() {
        if (`_settings-adjust-solid` != null) {
            return `_settings-adjust-solid`!!
        }
        `_settings-adjust-solid` = Builder(name = "Settings-adjust-solid", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(13.878f, 8.75f)
                lineTo(4.0f, 8.75f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, -1.5f)
                horizontalLineToRelative(9.878f)
                arcToRelative(2.251f, 2.251f, 0.0f, false, true, 4.244f, 0.0f)
                lineTo(20.0f, 7.25f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, 1.5f)
                horizontalLineToRelative(-1.878f)
                arcToRelative(2.251f, 2.251f, 0.0f, false, true, -4.244f, 0.0f)
                close()
                moveTo(20.0f, 16.75f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, -1.5f)
                horizontalLineToRelative(-9.878f)
                arcToRelative(2.251f, 2.251f, 0.0f, false, false, -4.244f, 0.0f)
                lineTo(4.0f, 15.25f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 0.0f, 1.5f)
                horizontalLineToRelative(1.878f)
                arcToRelative(2.25f, 2.25f, 0.0f, false, false, 4.244f, 0.0f)
                lineTo(20.0f, 16.75f)
                close()
            }
        }
        .build()
        return `_settings-adjust-solid`!!
    }

private var `_settings-adjust-solid`: ImageVector? = null
