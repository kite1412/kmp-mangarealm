package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
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

public val Assets.Atom: ImageVector
    get() {
        if (_atom != null) {
            return _atom!!
        }
        _atom = Builder(name = "Atom", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 36.0f, viewportHeight = 36.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(24.23f, 11.71f)
                arcToRelative(39.14f, 39.14f, 0.0f, false, false, -4.57f, -3.92f)
                arcToRelative(22.86f, 22.86f, 0.0f, false, true, 3.48f, -1.72f)
                curveToRelative(0.32f, -0.12f, 0.62f, -0.21f, 0.92f, -0.3f)
                arcToRelative(2.28f, 2.28f, 0.0f, false, false, 3.81f, -0.46f)
                arcToRelative(3.31f, 3.31f, 0.0f, false, true, 1.92f, 0.84f)
                curveToRelative(1.19f, 1.19f, 1.22f, 3.59f, 0.1f, 6.58f)
                curveToRelative(0.49f, 0.65f, 0.94f, 1.31f, 1.35f, 2.0f)
                curveToRelative(0.17f, -0.4f, 0.35f, -0.79f, 0.49f, -1.18f)
                curveToRelative(1.47f, -3.85f, 1.28f, -7.0f, -0.53f, -8.78f)
                arcToRelative(5.29f, 5.29f, 0.0f, false, false, -3.33f, -1.44f)
                arcToRelative(2.29f, 2.29f, 0.0f, false, false, -4.31f, 0.54f)
                curveToRelative(-0.37f, 0.11f, -0.74f, 0.22f, -1.13f, 0.37f)
                arcToRelative(25.79f, 25.79f, 0.0f, false, false, -4.57f, 2.35f)
                arcTo(26.21f, 26.21f, 0.0f, false, false, 13.28f, 4.2f)
                curveToRelative(-3.85f, -1.46f, -7.0f, -1.28f, -8.77f, 0.53f)
                curveTo(2.85f, 6.4f, 2.58f, 9.17f, 3.68f, 12.59f)
                arcToRelative(2.28f, 2.28f, 0.0f, false, false, 1.59f, 3.67f)
                curveToRelative(0.32f, 0.61f, 0.67f, 1.22f, 1.06f, 1.82f)
                arcTo(25.54f, 25.54f, 0.0f, false, false, 4.0f, 22.66f)
                curveToRelative(-1.47f, 3.84f, -1.28f, 7.0f, 0.53f, 8.77f)
                arcToRelative(5.63f, 5.63f, 0.0f, false, false, 4.12f, 1.51f)
                arcToRelative(13.34f, 13.34f, 0.0f, false, false, 4.65f, -1.0f)
                arcToRelative(26.21f, 26.21f, 0.0f, false, false, 4.58f, -2.35f)
                arcTo(25.79f, 25.79f, 0.0f, false, false, 22.43f, 32.0f)
                arcToRelative(14.16f, 14.16f, 0.0f, false, false, 3.65f, 0.9f)
                arcTo(2.3f, 2.3f, 0.0f, false, false, 30.46f, 32.0f)
                arcToRelative(4.55f, 4.55f, 0.0f, false, false, 0.74f, -0.57f)
                curveToRelative(1.81f, -1.81f, 2.0f, -4.93f, 0.53f, -8.77f)
                arcTo(32.68f, 32.68f, 0.0f, false, false, 24.23f, 11.71f)
                close()
                moveTo(12.57f, 30.09f)
                curveToRelative(-3.0f, 1.15f, -5.45f, 1.13f, -6.65f, -0.08f)
                reflectiveCurveToRelative(-1.23f, -3.62f, -0.07f, -6.64f)
                arcToRelative(22.77f, 22.77f, 0.0f, false, true, 1.71f, -3.48f)
                arcToRelative(40.19f, 40.19f, 0.0f, false, false, 3.92f, 4.56f)
                curveToRelative(0.43f, 0.43f, 0.87f, 0.85f, 1.31f, 1.25f)
                quadToRelative(0.9f, -0.46f, 1.83f, -1.05f)
                curveToRelative(-0.58f, -0.52f, -1.16f, -1.0f, -1.72f, -1.61f)
                arcToRelative(34.0f, 34.0f, 0.0f, false, true, -5.74f, -7.47f)
                arcTo(2.29f, 2.29f, 0.0f, false, false, 5.5f, 11.69f)
                horizontalLineToRelative(0.0f)
                curveToRelative(-0.75f, -2.5f, -0.62f, -4.49f, 0.43f, -5.54f)
                arcToRelative(3.72f, 3.72f, 0.0f, false, true, 2.72f, -0.92f)
                arcToRelative(11.4f, 11.4f, 0.0f, false, true, 3.93f, 0.84f)
                arcToRelative(22.86f, 22.86f, 0.0f, false, true, 3.48f, 1.72f)
                arcToRelative(39.14f, 39.14f, 0.0f, false, false, -4.57f, 3.92f)
                curveToRelative(-0.44f, 0.44f, -0.87f, 0.9f, -1.29f, 1.36f)
                arcToRelative(20.27f, 20.27f, 0.0f, false, false, 1.0f, 1.85f)
                curveToRelative(0.54f, -0.61f, 1.09f, -1.21f, 1.68f, -1.8f)
                arcToRelative(36.33f, 36.33f, 0.0f, false, true, 5.0f, -4.17f)
                arcToRelative(36.88f, 36.88f, 0.0f, false, true, 4.95f, 4.17f)
                arcToRelative(36.26f, 36.26f, 0.0f, false, true, 4.17f, 5.0f)
                arcToRelative(37.0f, 37.0f, 0.0f, false, true, -4.17f, 5.0f)
                arcTo(30.68f, 30.68f, 0.0f, false, true, 12.57f, 30.09f)
                close()
                moveTo(29.79f, 30.0f)
                lineToRelative(-0.16f, 0.13f)
                arcToRelative(2.27f, 2.27f, 0.0f, false, false, -3.5f, 0.72f)
                arcToRelative(12.57f, 12.57f, 0.0f, false, true, -3.0f, -0.77f)
                arcToRelative(22.0f, 22.0f, 0.0f, false, true, -3.48f, -1.72f)
                arcToRelative(39.14f, 39.14f, 0.0f, false, false, 4.57f, -3.92f)
                arcToRelative(38.26f, 38.26f, 0.0f, false, false, 3.92f, -4.56f)
                arcToRelative(22.88f, 22.88f, 0.0f, false, true, 1.72f, 3.48f)
                curveTo(31.0f, 26.39f, 31.0f, 28.81f, 29.79f, 30.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(17.9929f, 18.0641f)
                moveToRelative(-3.2574f, 0.5287f)
                arcToRelative(3.3f, 3.3f, 125.78f, true, true, 6.5147f, -1.0575f)
                arcToRelative(3.3f, 3.3f, 125.78f, true, true, -6.5147f, 1.0575f)
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.0f,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(0.0f, 0.0f)
                horizontalLineToRelative(36.0f)
                verticalLineToRelative(36.0f)
                horizontalLineToRelative(-36.0f)
                close()
            }
        }
        .build()
        return _atom!!
    }

private var _atom: ImageVector? = null
