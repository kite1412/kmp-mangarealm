package model

import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes

data class Manga(
    val coverArt: Painter?,
    val data: Data<MangaAttributes>
) {
    companion object {
        fun populateDetail(painter: Painter?, data: Data<MangaAttributes>): Manga {
            return Manga(painter, data)
        }
    }
}
