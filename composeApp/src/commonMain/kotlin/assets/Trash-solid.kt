package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Trash-solid`: ImageVector
    get() {
        if (`_trash-solid` != null) {
            return `_trash-solid`!!
        }
        `_trash-solid` = Builder(name = "Trash-solid", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.25f, 3.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.75f, -0.75f)
                horizontalLineToRelative(4.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.75f, 0.75f)
                verticalLineToRelative(0.75f)
                horizontalLineTo(19.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, 1.5f)
                horizontalLineTo(5.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, 0.0f, -1.5f)
                horizontalLineToRelative(4.25f)
                verticalLineTo(3.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(6.24f, 7.945f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, 0.497f, -0.445f)
                horizontalLineToRelative(10.526f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, 0.497f, 0.445f)
                lineToRelative(0.2f, 1.801f)
                arcToRelative(44.213f, 44.213f, 0.0f, false, true, 0.0f, 9.771f)
                lineToRelative(-0.02f, 0.177f)
                arcToRelative(2.603f, 2.603f, 0.0f, false, true, -2.226f, 2.29f)
                arcToRelative(26.788f, 26.788f, 0.0f, false, true, -7.428f, 0.0f)
                arcToRelative(2.603f, 2.603f, 0.0f, false, true, -2.227f, -2.29f)
                lineToRelative(-0.02f, -0.177f)
                arcToRelative(44.239f, 44.239f, 0.0f, false, true, 0.0f, -9.77f)
                lineToRelative(0.2f, -1.802f)
                close()
                moveTo(10.75f, 11.4f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, -1.5f, 0.0f)
                verticalLineToRelative(7.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 1.5f, 0.0f)
                verticalLineToRelative(-7.0f)
                close()
                moveTo(14.75f, 11.4f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, -1.5f, 0.0f)
                verticalLineToRelative(7.0f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, false, 1.5f, 0.0f)
                verticalLineToRelative(-7.0f)
                close()
            }
        }
        .build()
        return `_trash-solid`!!
    }

private var `_trash-solid`: ImageVector? = null
