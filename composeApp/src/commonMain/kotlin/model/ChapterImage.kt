package model

import androidx.compose.ui.graphics.painter.Painter

data class ChapterImage(
    var baseUrl: String,
    var hash: String,
    var quality: String,
    var fileUrl: String,
    var painter: Painter? = null
)
