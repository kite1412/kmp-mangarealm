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

public val Assets.`Magic-wand`: ImageVector
    get() {
        if (`_magic-wand` != null) {
            return `_magic-wand`!!
        }
        `_magic-wand` = Builder(name = "Magic-wand", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 32.0f, viewportHeight = 32.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.5f, 9.625f)
                lineToRelative(-0.906f, 2.906f)
                lineToRelative(-0.875f, -2.906f)
                lineToRelative(-2.906f, -0.906f)
                lineToRelative(2.906f, -0.875f)
                lineToRelative(0.875f, -2.938f)
                lineToRelative(0.906f, 2.938f)
                lineToRelative(2.906f, 0.875f)
                close()
                moveTo(14.563f, 8.031f)
                lineToRelative(-0.438f, 1.469f)
                lineToRelative(-0.5f, -1.469f)
                lineToRelative(-1.438f, -0.469f)
                lineToRelative(1.438f, -0.438f)
                lineToRelative(0.5f, -1.438f)
                lineToRelative(0.438f, 1.438f)
                lineToRelative(1.438f, 0.438f)
                close()
                moveTo(0.281f, 24.0f)
                lineToRelative(17.906f, -17.375f)
                curveToRelative(0.125f, -0.156f, 0.313f, -0.25f, 0.531f, -0.25f)
                curveToRelative(0.281f, -0.031f, 0.563f, 0.063f, 0.781f, 0.281f)
                curveToRelative(0.094f, 0.063f, 0.219f, 0.188f, 0.406f, 0.344f)
                curveToRelative(0.344f, 0.313f, 0.719f, 0.688f, 1.0f, 1.063f)
                curveToRelative(0.125f, 0.188f, 0.188f, 0.344f, 0.188f, 0.5f)
                curveToRelative(0.031f, 0.313f, -0.063f, 0.594f, -0.25f, 0.781f)
                lineToRelative(-17.906f, 17.438f)
                curveToRelative(-0.156f, 0.156f, -0.344f, 0.219f, -0.563f, 0.25f)
                curveToRelative(-0.281f, 0.031f, -0.563f, -0.063f, -0.781f, -0.281f)
                curveToRelative(-0.094f, -0.094f, -0.219f, -0.188f, -0.406f, -0.375f)
                curveToRelative(-0.344f, -0.281f, -0.719f, -0.656f, -0.969f, -1.063f)
                curveToRelative(-0.125f, -0.188f, -0.188f, -0.375f, -0.219f, -0.531f)
                curveToRelative(-0.031f, -0.313f, 0.063f, -0.563f, 0.281f, -0.781f)
                close()
                moveTo(14.656f, 11.375f)
                lineToRelative(1.313f, 1.344f)
                lineToRelative(4.156f, -4.031f)
                lineToRelative(-1.313f, -1.375f)
                close()
                moveTo(5.938f, 13.156f)
                lineToRelative(-0.406f, 1.438f)
                lineToRelative(-0.438f, -1.438f)
                lineToRelative(-1.438f, -0.469f)
                lineToRelative(1.438f, -0.438f)
                lineToRelative(0.438f, -1.469f)
                lineToRelative(0.406f, 1.469f)
                lineToRelative(1.5f, 0.438f)
                close()
                moveTo(20.5f, 12.063f)
                lineToRelative(0.469f, 1.469f)
                lineToRelative(1.438f, 0.438f)
                lineToRelative(-1.438f, 0.469f)
                lineToRelative(-0.469f, 1.438f)
                lineToRelative(-0.469f, -1.438f)
                lineToRelative(-1.438f, -0.469f)
                lineToRelative(1.438f, -0.438f)
                close()
            }
        }
        .build()
        return `_magic-wand`!!
    }

private var `_magic-wand`: ImageVector? = null
