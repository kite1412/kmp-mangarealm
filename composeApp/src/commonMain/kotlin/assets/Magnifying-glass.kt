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

public val Assets.`Magnifying-glass`: ImageVector
    get() {
        if (`_magnifying-glass` != null) {
            return `_magnifying-glass`!!
        }
        `_magnifying-glass` = Builder(name = "Magnifying-glass", defaultWidth = 64.0.dp,
                defaultHeight = 64.0.dp, viewportWidth = 64.0f, viewportHeight = 64.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.217f, 8.782f)
                curveToRelative(-9.044f, -9.043f, -23.708f, -9.043f, -32.752f, 0.0f)
                curveToRelative(-8.4f, 8.402f, -8.985f, 21.645f, -1.779f, 30.735f)
                lineToRelative(-1.893f, 1.893f)
                lineToRelative(-1.572f, -1.572f)
                lineTo(4.376f, 52.682f)
                curveToRelative(-0.754f, -0.479f, -1.335f, -0.684f, -1.572f, -0.445f)
                lineToRelative(-0.703f, 0.701f)
                curveTo(0.958f, 54.081f, 9.919f, 63.044f, 11.062f, 61.9f)
                lineToRelative(0.703f, -0.702f)
                curveToRelative(0.238f, -0.237f, 0.031f, -0.819f, -0.446f, -1.575f)
                lineTo(24.163f, 46.78f)
                lineToRelative(-1.573f, -1.572f)
                lineToRelative(1.894f, -1.892f)
                curveToRelative(9.091f, 7.207f, 22.331f, 6.621f, 30.734f, -1.78f)
                curveToRelative(9.042f, -9.045f, 9.044f, -23.708f, -0.001f, -32.754f)
                moveToRelative(-2.729f, 30.024f)
                curveToRelative(-7.538f, 7.536f, -19.757f, 7.538f, -27.293f, 0.0f)
                curveToRelative(-7.537f, -7.535f, -7.537f, -19.758f, 0.0f, -27.293f)
                curveToRelative(7.534f, -7.537f, 19.757f, -7.537f, 27.293f, 0.0f)
                reflectiveCurveToRelative(7.534f, 19.756f, 0.0f, 27.293f)
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.317f, 23.902f)
                arcToRelative(16.585f, 16.585f, 0.0f, false, true, -3.789f, 5.879f)
                curveToRelative(-6.513f, 6.514f, -17.076f, 6.514f, -23.592f, -0.003f)
                arcToRelative(16.61f, 16.61f, 0.0f, false, true, -4.813f, -10.243f)
                curveToRelative(-2.256f, 5.949f, -1.0f, 12.926f, 3.79f, 17.716f)
                curveToRelative(6.515f, 6.515f, 17.078f, 6.515f, 23.593f, 0.0f)
                curveToRelative(3.654f, -3.651f, 5.255f, -8.577f, 4.811f, -13.349f)
            }
        }
        .build()
        return `_magnifying-glass`!!
    }

private var `_magnifying-glass`: ImageVector? = null
