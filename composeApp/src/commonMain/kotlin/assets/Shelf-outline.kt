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

public val Assets.`Shelf-outline`: ImageVector
    get() {
        if (`_shelf-outline` != null) {
            return `_shelf-outline`!!
        }
        `_shelf-outline` = Builder(name = "Shelf-outline", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(6.0f, 23.0f)
                horizontalLineTo(18.0f)
                arcToRelative(3.0f, 3.0f, 0.0f, false, false, 3.0f, -3.0f)
                verticalLineTo(4.0f)
                arcToRelative(3.0f, 3.0f, 0.0f, false, false, -3.0f, -3.0f)
                horizontalLineTo(6.0f)
                arcTo(3.0f, 3.0f, 0.0f, false, false, 3.0f, 4.0f)
                verticalLineTo(20.0f)
                arcTo(3.0f, 3.0f, 0.0f, false, false, 6.0f, 23.0f)
                close()
                moveTo(19.0f, 9.5f)
                verticalLineToRelative(5.0f)
                horizontalLineToRelative(-0.132f)
                lineTo(17.9f, 12.553f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -1.79f, 0.894f)
                lineToRelative(0.527f, 1.053f)
                horizontalLineTo(15.0f)
                verticalLineTo(12.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -2.0f, 0.0f)
                verticalLineToRelative(2.5f)
                horizontalLineTo(12.0f)
                verticalLineTo(13.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -2.0f, 0.0f)
                verticalLineToRelative(1.5f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(-5.0f)
                close()
                moveTo(18.0f, 21.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(19.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -2.0f, 0.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(19.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -2.0f, 0.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(20.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, false, -2.0f, 0.0f)
                verticalLineToRelative(1.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, -1.0f, -1.0f)
                verticalLineTo(16.5f)
                horizontalLineTo(19.0f)
                verticalLineTo(20.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 18.0f, 21.0f)
                close()
                moveTo(6.0f, 3.0f)
                horizontalLineTo(18.0f)
                arcToRelative(1.0f, 1.0f, 0.0f, false, true, 1.0f, 1.0f)
                verticalLineTo(7.5f)
                horizontalLineTo(14.868f)
                lineTo(13.9f, 5.553f)
                arcToRelative(1.0f, 1.0f, 0.0f, true, false, -1.79f, 0.894f)
                lineTo(12.632f, 7.5f)
                horizontalLineTo(11.0f)
                verticalLineTo(5.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, false, 9.0f, 5.0f)
                verticalLineTo(7.5f)
                horizontalLineTo(8.0f)
                verticalLineTo(6.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, false, 6.0f, 6.0f)
                verticalLineTo(7.5f)
                horizontalLineTo(5.0f)
                verticalLineTo(4.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 6.0f, 3.0f)
                close()
            }
        }
        .build()
        return `_shelf-outline`!!
    }

private var `_shelf-outline`: ImageVector? = null
