package assets

import Assets
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Assets.Settings: ImageVector
    get() {
        if (_settings != null) {
            return _settings!!
        }
        _settings = Builder(name = "Settings", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
                viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(20.3499f, 8.9229f)
                lineTo(19.9837f, 8.7192f)
                curveTo(19.9269f, 8.6876f, 19.8989f, 8.6717f, 19.8714f, 8.6552f)
                curveTo(19.5983f, 8.4916f, 19.3682f, 8.2656f, 19.2002f, 7.9952f)
                curveTo(19.1833f, 7.968f, 19.1674f, 7.9395f, 19.1348f, 7.8831f)
                curveTo(19.1023f, 7.8268f, 19.0858f, 7.7982f, 19.0706f, 7.77f)
                curveTo(18.92f, 7.4887f, 18.8385f, 7.1751f, 18.8336f, 6.8561f)
                curveTo(18.8331f, 6.824f, 18.8332f, 6.7912f, 18.8343f, 6.726f)
                lineTo(18.8415f, 6.3008f)
                curveTo(18.8529f, 5.6203f, 18.8587f, 5.2789f, 18.763f, 4.9726f)
                curveTo(18.6781f, 4.7005f, 18.536f, 4.4499f, 18.3462f, 4.2372f)
                curveTo(18.1317f, 3.9969f, 17.8347f, 3.8253f, 17.2402f, 3.4828f)
                lineTo(16.7464f, 3.1982f)
                curveTo(16.1536f, 2.8566f, 15.8571f, 2.6857f, 15.5423f, 2.6206f)
                curveTo(15.2639f, 2.5629f, 14.9765f, 2.5656f, 14.6991f, 2.6279f)
                curveTo(14.3859f, 2.6982f, 14.0931f, 2.8735f, 13.5079f, 3.224f)
                lineTo(13.5045f, 3.2255f)
                lineTo(13.1507f, 3.4374f)
                curveTo(13.0948f, 3.4709f, 13.0665f, 3.4878f, 13.0384f, 3.5034f)
                curveTo(12.7601f, 3.6581f, 12.4495f, 3.7436f, 12.1312f, 3.7539f)
                curveTo(12.0992f, 3.7549f, 12.0665f, 3.7549f, 12.0013f, 3.7549f)
                curveTo(11.9365f, 3.7549f, 11.9024f, 3.7549f, 11.8704f, 3.7539f)
                curveTo(11.5515f, 3.7436f, 11.2402f, 3.6576f, 10.9615f, 3.5022f)
                curveTo(10.9334f, 3.4866f, 10.9056f, 3.4696f, 10.8496f, 3.4359f)
                lineTo(10.4935f, 3.2221f)
                curveTo(9.9042f, 2.8684f, 9.6091f, 2.6912f, 9.2943f, 2.6206f)
                curveTo(9.0157f, 2.5581f, 8.7274f, 2.5563f, 8.4479f, 2.6147f)
                curveTo(8.1324f, 2.6806f, 7.8358f, 2.8528f, 7.2426f, 3.197f)
                lineTo(7.2399f, 3.1982f)
                lineTo(6.7523f, 3.4812f)
                lineTo(6.7469f, 3.4845f)
                curveTo(6.159f, 3.8257f, 5.8644f, 3.9967f, 5.6517f, 4.2361f)
                curveTo(5.4629f, 4.4486f, 5.3218f, 4.6988f, 5.2374f, 4.9702f)
                curveTo(5.1419f, 5.2769f, 5.147f, 5.619f, 5.1585f, 6.3027f)
                lineTo(5.1657f, 6.7274f)
                curveTo(5.1668f, 6.7917f, 5.1686f, 6.8236f, 5.1682f, 6.8552f)
                curveTo(5.1634f, 7.175f, 5.0809f, 7.4891f, 4.9297f, 7.771f)
                curveTo(4.9148f, 7.7988f, 4.8987f, 7.8267f, 4.8665f, 7.8824f)
                curveTo(4.8344f, 7.9381f, 4.8188f, 7.9658f, 4.8021f, 7.9927f)
                curveTo(4.6334f, 8.2645f, 4.4021f, 8.4919f, 4.1273f, 8.6557f)
                curveTo(4.1002f, 8.6719f, 4.0715f, 8.6875f, 4.0152f, 8.7187f)
                lineTo(3.6537f, 8.9191f)
                curveTo(3.0521f, 9.2524f, 2.7514f, 9.4193f, 2.5326f, 9.6567f)
                curveTo(2.339f, 9.8667f, 2.1927f, 10.1158f, 2.1035f, 10.3872f)
                curveTo(2.0026f, 10.6939f, 2.0027f, 11.0378f, 2.0042f, 11.7255f)
                lineTo(2.0055f, 12.2877f)
                curveTo(2.0071f, 12.9708f, 2.0092f, 13.3122f, 2.1103f, 13.6168f)
                curveTo(2.1998f, 13.8863f, 2.3449f, 14.134f, 2.5374f, 14.3427f)
                curveTo(2.755f, 14.5787f, 3.0527f, 14.7445f, 3.6497f, 15.0766f)
                lineTo(4.0081f, 15.276f)
                curveTo(4.0691f, 15.3099f, 4.0998f, 15.3266f, 4.1292f, 15.3444f)
                curveTo(4.4015f, 15.5083f, 4.6309f, 15.735f, 4.7982f, 16.0053f)
                curveTo(4.8162f, 16.0345f, 4.8336f, 16.0648f, 4.8683f, 16.1255f)
                curveTo(4.9026f, 16.1853f, 4.9201f, 16.2152f, 4.9359f, 16.2452f)
                curveTo(5.0826f, 16.5229f, 5.1611f, 16.8315f, 5.1665f, 17.1455f)
                curveTo(5.1671f, 17.1794f, 5.1666f, 17.2137f, 5.1654f, 17.2827f)
                lineTo(5.1585f, 17.6902f)
                curveTo(5.1469f, 18.3763f, 5.1419f, 18.7197f, 5.2379f, 19.0273f)
                curveTo(5.3229f, 19.2994f, 5.4648f, 19.55f, 5.6546f, 19.7627f)
                curveTo(5.8692f, 20.0031f, 6.1666f, 20.1745f, 6.7611f, 20.5171f)
                lineTo(7.2548f, 20.8015f)
                curveTo(7.8476f, 21.1432f, 8.144f, 21.3138f, 8.4587f, 21.379f)
                curveTo(8.7371f, 21.4366f, 9.0246f, 21.4344f, 9.3021f, 21.3721f)
                curveTo(9.6157f, 21.3017f, 9.9095f, 21.1258f, 10.4964f, 20.7743f)
                lineTo(10.8502f, 20.5625f)
                curveTo(10.9062f, 20.5289f, 10.9346f, 20.5121f, 10.9626f, 20.4965f)
                curveTo(11.2409f, 20.3418f, 11.5512f, 20.2558f, 11.8695f, 20.2456f)
                curveTo(11.9015f, 20.2446f, 11.9342f, 20.2446f, 11.9994f, 20.2446f)
                curveTo(12.0648f, 20.2446f, 12.0974f, 20.2446f, 12.1295f, 20.2456f)
                curveTo(12.4484f, 20.2559f, 12.7607f, 20.3422f, 13.0394f, 20.4975f)
                curveTo(13.0639f, 20.5112f, 13.0885f, 20.526f, 13.1316f, 20.5519f)
                lineTo(13.5078f, 20.7777f)
                curveTo(14.0971f, 21.1315f, 14.3916f, 21.3081f, 14.7065f, 21.3788f)
                curveTo(14.985f, 21.4413f, 15.2736f, 21.4438f, 15.5531f, 21.3855f)
                curveTo(15.8685f, 21.3196f, 16.1657f, 21.1471f, 16.7586f, 20.803f)
                lineTo(17.2536f, 20.5157f)
                curveTo(17.8418f, 20.1743f, 18.1367f, 20.0031f, 18.3495f, 19.7636f)
                curveTo(18.5383f, 19.5512f, 18.6796f, 19.3011f, 18.764f, 19.0297f)
                curveTo(18.8588f, 18.7252f, 18.8531f, 18.3858f, 18.8417f, 17.7119f)
                lineTo(18.8343f, 17.2724f)
                curveTo(18.8332f, 17.2081f, 18.8331f, 17.1761f, 18.8336f, 17.1445f)
                curveTo(18.8383f, 16.8247f, 18.9195f, 16.5104f, 19.0706f, 16.2286f)
                curveTo(19.0856f, 16.2007f, 19.1018f, 16.1726f, 19.1338f, 16.1171f)
                curveTo(19.166f, 16.0615f, 19.1827f, 16.0337f, 19.1994f, 16.0068f)
                curveTo(19.3681f, 15.7349f, 19.5995f, 15.5074f, 19.8744f, 15.3435f)
                curveTo(19.9012f, 15.3275f, 19.9289f, 15.3122f, 19.9838f, 15.2818f)
                lineTo(19.9857f, 15.2809f)
                lineTo(20.3472f, 15.0805f)
                curveTo(20.9488f, 14.7472f, 21.2501f, 14.5801f, 21.4689f, 14.3427f)
                curveTo(21.6625f, 14.1327f, 21.8085f, 13.8839f, 21.8978f, 13.6126f)
                curveTo(21.9981f, 13.3077f, 21.9973f, 12.9658f, 21.9958f, 12.2861f)
                lineTo(21.9945f, 11.7119f)
                curveTo(21.9929f, 11.0287f, 21.9921f, 10.6874f, 21.891f, 10.3828f)
                curveTo(21.8015f, 10.1133f, 21.6555f, 9.8656f, 21.463f, 9.6568f)
                curveTo(21.2457f, 9.4211f, 20.9475f, 9.2553f, 20.3517f, 8.9238f)
                lineTo(20.3499f, 8.9229f)
                close()
            }
            path(fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                    StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(8.0003f, 12.0f)
                curveTo(8.0003f, 14.2091f, 9.7912f, 16.0f, 12.0003f, 16.0f)
                curveTo(14.2095f, 16.0f, 16.0003f, 14.2091f, 16.0003f, 12.0f)
                curveTo(16.0003f, 9.7908f, 14.2095f, 8.0f, 12.0003f, 8.0f)
                curveTo(9.7912f, 8.0f, 8.0003f, 9.7908f, 8.0003f, 12.0f)
                close()
            }
        }
        .build()
        return _settings!!
    }

private var _settings: ImageVector? = null
