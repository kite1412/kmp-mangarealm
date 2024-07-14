package model.session

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.ApiConstant
import model.Manga

data class MangaSession(
    override var url: String = ApiConstant.MANGA_ENDPOINT,
    override val queries: MutableMap<String, Any> = mutableMapOf(),
    override var response: ListResponse<MangaAttributes> = ListResponse(),
    override var data: SnapshotStateList<Manga> = mutableStateListOf(),
    override var state: MutableState<SessionState> = mutableStateOf(SessionState.IDLE)
): Session<Manga, MangaAttributes>