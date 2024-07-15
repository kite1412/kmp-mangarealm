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

public val Assets.`Treasure-map`: ImageVector
    get() {
        if (`_treasure-map` != null) {
            return `_treasure-map`!!
        }
        `_treasure-map` = Builder(name = "Treasure-map", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(14.3675f, 2.1567f)
                curveTo(14.7781f, 2.0199f, 15.2219f, 2.0199f, 15.6325f, 2.1567f)
                lineTo(20.6325f, 3.8234f)
                curveTo(21.4491f, 4.0956f, 22.0f, 4.8599f, 22.0f, 5.7207f)
                verticalLineTo(19.6126f)
                curveTo(22.0f, 20.9777f, 20.6626f, 21.9416f, 19.3675f, 21.5099f)
                lineTo(15.0f, 20.0541f)
                lineTo(9.6325f, 21.8433f)
                curveTo(9.2219f, 21.9801f, 8.7781f, 21.9801f, 8.3675f, 21.8433f)
                lineTo(3.3675f, 20.1766f)
                curveTo(2.5509f, 19.9044f, 2.0f, 19.1401f, 2.0f, 18.2792f)
                verticalLineTo(4.3874f)
                curveTo(2.0f, 3.0223f, 3.3374f, 2.0584f, 4.6325f, 2.49f)
                lineTo(9.0f, 3.9459f)
                lineTo(14.3675f, 2.1567f)
                close()
                moveTo(15.0f, 4.0541f)
                lineTo(9.6325f, 5.8433f)
                curveTo(9.2219f, 5.9801f, 8.7781f, 5.9801f, 8.3675f, 5.8433f)
                lineTo(4.0f, 4.3874f)
                verticalLineTo(18.2792f)
                lineTo(9.0f, 19.9459f)
                lineTo(14.3675f, 18.1567f)
                curveTo(14.7781f, 18.0199f, 15.2219f, 18.0199f, 15.6325f, 18.1567f)
                lineTo(20.0f, 19.6126f)
                verticalLineTo(5.7207f)
                lineTo(15.0f, 4.0541f)
                close()
                moveTo(13.2929f, 8.2929f)
                curveTo(13.6834f, 7.9023f, 14.3166f, 7.9023f, 14.7071f, 8.2929f)
                lineTo(15.5f, 9.0858f)
                lineTo(16.2929f, 8.2929f)
                curveTo(16.6834f, 7.9023f, 17.3166f, 7.9023f, 17.7071f, 8.2929f)
                curveTo(18.0976f, 8.6834f, 18.0976f, 9.3166f, 17.7071f, 9.7071f)
                lineTo(16.9142f, 10.5f)
                lineTo(17.7071f, 11.2929f)
                curveTo(18.0976f, 11.6834f, 18.0976f, 12.3166f, 17.7071f, 12.7071f)
                curveTo(17.3166f, 13.0976f, 16.6834f, 13.0976f, 16.2929f, 12.7071f)
                lineTo(15.5f, 11.9142f)
                lineTo(14.7071f, 12.7071f)
                curveTo(14.3166f, 13.0976f, 13.6834f, 13.0976f, 13.2929f, 12.7071f)
                curveTo(12.9024f, 12.3166f, 12.9024f, 11.6834f, 13.2929f, 11.2929f)
                lineTo(14.0858f, 10.5f)
                lineTo(13.2929f, 9.7071f)
                curveTo(12.9024f, 9.3166f, 12.9024f, 8.6834f, 13.2929f, 8.2929f)
                close()
                moveTo(6.0f, 16.0f)
                curveTo(6.5523f, 16.0f, 7.0f, 15.5523f, 7.0f, 15.0f)
                curveTo(7.0f, 14.4477f, 6.5523f, 14.0f, 6.0f, 14.0f)
                curveTo(5.4477f, 14.0f, 5.0f, 14.4477f, 5.0f, 15.0f)
                curveTo(5.0f, 15.5523f, 5.4477f, 16.0f, 6.0f, 16.0f)
                close()
                moveTo(9.0f, 12.0f)
                curveTo(9.0f, 12.5523f, 8.5523f, 13.0f, 8.0f, 13.0f)
                curveTo(7.4477f, 13.0f, 7.0f, 12.5523f, 7.0f, 12.0f)
                curveTo(7.0f, 11.4477f, 7.4477f, 11.0f, 8.0f, 11.0f)
                curveTo(8.5523f, 11.0f, 9.0f, 11.4477f, 9.0f, 12.0f)
                close()
                moveTo(11.0f, 12.0f)
                curveTo(11.5523f, 12.0f, 12.0f, 11.5523f, 12.0f, 11.0f)
                curveTo(12.0f, 10.4477f, 11.5523f, 10.0f, 11.0f, 10.0f)
                curveTo(10.4477f, 10.0f, 10.0f, 10.4477f, 10.0f, 11.0f)
                curveTo(10.0f, 11.5523f, 10.4477f, 12.0f, 11.0f, 12.0f)
                close()
            }
        }
        .build()
        return `_treasure-map`!!
    }

private var `_treasure-map`: ImageVector? = null
