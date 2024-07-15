package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.`Heart-outline`: ImageVector
    get() {
        if (`_heart-outline` != null) {
            return `_heart-outline`!!
        }
        `_heart-outline` = Builder(name = "Heart-outline", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(3.25f, 10.03f)
                curveToRelative(0.0f, -2.7f, 2.37f, -4.78f, 5.15f, -4.78f)
                curveToRelative(1.433f, 0.0f, 2.695f, 0.672f, 3.6f, 1.542f)
                curveToRelative(0.905f, -0.87f, 2.166f, -1.542f, 3.6f, -1.542f)
                curveToRelative(2.78f, 0.0f, 5.15f, 2.08f, 5.15f, 4.78f)
                curveToRelative(0.0f, 1.85f, -0.789f, 3.476f, -1.882f, 4.852f)
                curveToRelative(-1.09f, 1.372f, -2.518f, 2.537f, -3.884f, 3.484f)
                curveToRelative(-0.523f, 0.362f, -1.05f, 0.695f, -1.534f, 0.941f)
                curveToRelative(-0.454f, 0.231f, -0.975f, 0.443f, -1.45f, 0.443f)
                reflectiveCurveToRelative(-0.996f, -0.212f, -1.45f, -0.443f)
                arcToRelative(13.795f, 13.795f, 0.0f, false, true, -1.533f, -0.941f)
                curveToRelative(-1.367f, -0.947f, -2.794f, -2.112f, -3.885f, -3.484f)
                curveTo(4.039f, 13.506f, 3.25f, 11.88f, 3.25f, 10.03f)
                close()
                moveTo(8.4f, 6.75f)
                curveToRelative(-2.08f, 0.0f, -3.65f, 1.53f, -3.65f, 3.28f)
                curveToRelative(0.0f, 1.403f, 0.596f, 2.71f, 1.556f, 3.918f)
                curveToRelative(0.962f, 1.21f, 2.257f, 2.279f, 3.565f, 3.185f)
                curveToRelative(0.495f, 0.343f, 0.96f, 0.634f, 1.36f, 0.838f)
                curveToRelative(0.428f, 0.218f, 0.676f, 0.279f, 0.769f, 0.279f)
                curveToRelative(0.093f, 0.0f, 0.341f, -0.061f, 0.77f, -0.28f)
                arcToRelative(12.35f, 12.35f, 0.0f, false, false, 1.36f, -0.837f)
                curveToRelative(1.307f, -0.906f, 2.602f, -1.974f, 3.564f, -3.185f)
                curveToRelative(0.96f, -1.208f, 1.556f, -2.515f, 1.556f, -3.918f)
                curveToRelative(0.0f, -1.75f, -1.57f, -3.28f, -3.65f, -3.28f)
                curveToRelative(-1.194f, 0.0f, -2.31f, 0.713f, -3.005f, 1.619f)
                arcToRelative(0.75f, 0.75f, 0.0f, false, true, -1.19f, 0.0f)
                curveTo(10.71f, 7.463f, 9.595f, 6.75f, 8.4f, 6.75f)
                close()
            }
        }
        .build()
        return `_heart-outline`!!
    }

private var `_heart-outline`: ImageVector? = null
