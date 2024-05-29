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

public val Assets.`Shelf-solid`: ImageVector
    get() {
        if (`_shelf-solid` != null) {
            return `_shelf-solid`!!
        }
        `_shelf-solid` = Builder(name = "Shelf-solid", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(18.0f, 2.0f)
                lineTo(6.0f, 2.0f)
                arcTo(2.0f, 2.0f, 0.0f, false, false, 4.0f, 4.0f)
                lineTo(4.0f, 20.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, false, 2.0f, 2.0f)
                lineTo(18.0f, 22.0f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, false, 2.0f, -2.0f)
                lineTo(20.0f, 4.0f)
                arcTo(2.0f, 2.0f, 0.0f, false, false, 18.0f, 2.0f)
                close()
                moveTo(12.553f, 5.105f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 1.342f, 0.448f)
                lineTo(14.868f, 7.5f)
                lineTo(12.632f, 7.5f)
                lineToRelative(-0.527f, -1.053f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 12.553f, 5.105f)
                close()
                moveTo(12.0f, 13.0f)
                verticalLineToRelative(1.5f)
                lineTo(10.0f, 14.5f)
                lineTo(10.0f, 13.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                close()
                moveTo(9.0f, 5.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                lineTo(11.0f, 7.5f)
                lineTo(9.0f, 7.5f)
                close()
                moveTo(6.0f, 6.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 8.0f, 6.0f)
                lineTo(8.0f, 7.5f)
                lineTo(6.0f, 7.5f)
                close()
                moveTo(6.0f, 20.0f)
                lineTo(6.0f, 19.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                verticalLineToRelative(1.0f)
                close()
                moveTo(9.0f, 20.0f)
                lineTo(9.0f, 18.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(12.0f, 20.0f)
                lineTo(12.0f, 18.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(15.0f, 14.5f)
                lineTo(13.0f, 14.5f)
                lineTo(13.0f, 12.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 2.0f, 0.0f)
                close()
                moveTo(16.632f, 14.5f)
                lineTo(16.105f, 13.447f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 1.79f, -0.894f)
                lineToRelative(0.973f, 1.947f)
                close()
            }
        }
        .build()
        return `_shelf-solid`!!
    }

private var `_shelf-solid`: ImageVector? = null
