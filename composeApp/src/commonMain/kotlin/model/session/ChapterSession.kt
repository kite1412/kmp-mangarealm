package model.session

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.util.ApiConstant
import model.Chapter

data class ChapterSession(
    private val mangaId: String,
    override val url: String = ApiConstant.mangaChapters(mangaId),
    override val queries: MutableMap<String, Any> = mutableMapOf(),
    override var response: ListResponse<ChapterAttributes> = ListResponse(),
    override var data: SnapshotStateList<Chapter> = mutableStateListOf(),
    override var state: MutableState<SessionState> = mutableStateOf(SessionState.IDLE)
): Session<Chapter, ChapterAttributes>
