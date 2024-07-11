package model.session

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.ApiConstant
import model.Manga

data class MangaSession(
    override var url: String = ApiConstant.MANGA_ENDPOINT,
    override val queries: MutableMap<String, Any> = mutableMapOf(),
    override var response: ListResponse<MangaAttributes> = ListResponse(),
    override var data: SnapshotStateMap<String, Manga> = mutableStateMapOf()
): Session<String, Manga, MangaAttributes>