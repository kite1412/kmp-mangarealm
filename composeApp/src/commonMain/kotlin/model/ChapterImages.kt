package model

import androidx.compose.ui.graphics.painter.Painter

data class ChapterImages(
    val quality: String,
    val images: MutableList<Painter?>
) {
    operator fun invoke(): MutableList<Painter?> = images
}
