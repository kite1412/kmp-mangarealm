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

public val Assets.Flower: ImageVector
    get() {
        if (_flower != null) {
            return _flower!!
        }
        _flower = Builder(name = "Flower", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(19.68f, 6.88f)
                arcToRelative(4.4f, 4.4f, 0.0f, false, false, -3.31f, -0.32f)
                arcToRelative(4.37f, 4.37f, 0.0f, false, false, -8.73f, 0.0f)
                arcToRelative(4.48f, 4.48f, 0.0f, false, false, -3.31f, 0.29f)
                arcToRelative(4.37f, 4.37f, 0.0f, false, false, 0.61f, 8.0f)
                arcToRelative(4.4f, 4.4f, 0.0f, false, false, -0.8f, 2.5f)
                arcToRelative(5.0f, 5.0f, 0.0f, false, false, 0.07f, 0.75f)
                arcTo(4.34f, 4.34f, 0.0f, false, false, 8.5f, 21.73f)
                arcToRelative(4.68f, 4.68f, 0.0f, false, false, 0.64f, 0.0f)
                arcTo(4.42f, 4.42f, 0.0f, false, false, 12.0f, 20.0f)
                arcToRelative(4.42f, 4.42f, 0.0f, false, false, 2.86f, 1.69f)
                arcToRelative(4.68f, 4.68f, 0.0f, false, false, 0.64f, 0.0f)
                arcToRelative(4.36f, 4.36f, 0.0f, false, false, 3.56f, -6.87f)
                arcToRelative(4.36f, 4.36f, 0.0f, false, false, 0.62f, -8.0f)
                close()
                moveTo(10.34f, 4.94f)
                arcToRelative(2.4f, 2.4f, 0.0f, false, true, 3.32f, 0.0f)
                arcToRelative(2.43f, 2.43f, 0.0f, false, true, 0.52f, 2.66f)
                lineToRelative(-0.26f, 0.59f)
                lineToRelative(-0.66f, 0.58f)
                arcTo(4.07f, 4.07f, 0.0f, false, false, 12.0f, 8.55f)
                arcToRelative(4.0f, 4.0f, 0.0f, false, false, -1.61f, 0.34f)
                lineTo(9.83f, 7.6f)
                arcTo(2.39f, 2.39f, 0.0f, false, true, 10.34f, 4.94f)
                close()
                moveTo(4.24f, 11.78f)
                arcTo(2.37f, 2.37f, 0.0f, false, true, 7.94f, 9.0f)
                lineToRelative(0.49f, 0.43f)
                lineToRelative(0.35f, 0.8f)
                arcTo(3.92f, 3.92f, 0.0f, false, false, 8.0f, 12.55f)
                arcTo(2.85f, 2.85f, 0.0f, false, false, 8.0f, 13.0f)
                lineToRelative(-0.55f, 0.0f)
                horizontalLineToRelative(0.0f)
                lineToRelative(-0.84f, 0.08f)
                arcTo(2.37f, 2.37f, 0.0f, false, true, 4.24f, 11.78f)
                close()
                moveTo(10.84f, 17.86f)
                arcToRelative(2.38f, 2.38f, 0.0f, false, true, -4.66f, -0.08f)
                arcToRelative(3.07f, 3.07f, 0.0f, false, true, 0.0f, -0.42f)
                arcToRelative(2.33f, 2.33f, 0.0f, false, true, 1.17f, -2.0f)
                lineTo(7.86f, 15.0f)
                lineToRelative(0.91f, -0.1f)
                arcToRelative(4.0f, 4.0f, 0.0f, false, false, 2.38f, 1.57f)
                close()
                moveTo(12.0f, 14.55f)
                arcToRelative(2.0f, 2.0f, 0.0f, true, true, 2.0f, -2.0f)
                arcTo(2.0f, 2.0f, 0.0f, false, true, 12.0f, 14.55f)
                close()
                moveTo(17.82f, 17.77f)
                arcToRelative(2.36f, 2.36f, 0.0f, false, true, -2.68f, 1.94f)
                arcToRelative(2.39f, 2.39f, 0.0f, false, true, -2.0f, -1.85f)
                lineToRelative(-0.14f, -0.6f)
                lineToRelative(0.21f, -0.92f)
                arcToRelative(4.0f, 4.0f, 0.0f, false, false, 2.2f, -1.76f)
                lineToRelative(0.5f, 0.3f)
                lineToRelative(0.09f, 0.0f)
                lineToRelative(0.66f, 0.39f)
                arcTo(2.38f, 2.38f, 0.0f, false, true, 17.82f, 17.77f)
                close()
                moveTo(19.76f, 11.77f)
                arcToRelative(2.39f, 2.39f, 0.0f, false, true, -2.13f, 1.33f)
                horizontalLineToRelative(-0.24f)
                lineTo(16.75f, 13.0f)
                lineTo(16.0f, 12.59f)
                verticalLineToRelative(0.0f)
                arcToRelative(4.0f, 4.0f, 0.0f, false, false, -1.0f, -2.64f)
                lineToRelative(0.43f, -0.37f)
                lineToRelative(0.0f, 0.0f)
                lineTo(16.06f, 9.0f)
                arcToRelative(2.37f, 2.37f, 0.0f, false, true, 3.7f, 2.82f)
                close()
            }
        }
        .build()
        return _flower!!
    }

private var _flower: ImageVector? = null
