
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import api.mangadex.model.response.Data
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.service.MangaDex
import api.mangadex.util.DataType
import api.mangadex.util.coverImageUrl
import api.mangadex.util.generateArrayQueryParam
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

class Initializer {
    var executeOnce = mutableStateOf(true)
    var latestUpdatesCovers = mutableStateListOf<Resource<Painter>>()
    private val mangaDex: MangaDex = Libs.mangaDex

    suspend fun getInitialLatestUpdates(): List<Data<MangaAttributes>> {
        return mangaDex.getManga(generateArrayQueryParam(
            name = "includes[]",
            values = listOf("manga", "cover_art")
        ))?.data ?: listOf()
    }

    @Composable
    private fun getInitialLatestUpdatesCovers(data: List<Data<MangaAttributes>>) {
        data.forEach { d ->
            d.relationships.forEach { r ->
                if (r.type == DataType.COVER_ART.name.lowercase()) {
                    latestUpdatesCovers.add(asyncPainterResource(data = coverImageUrl(
                        mangaId = d.id,
                        filename = r.attributes!!.fileName!!
                    )))
                }
            }
        }
    }
}