package model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import api.mangadex.model.response.Data
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.attribute.ChapterAttributes

data class Chapter(
    val data: Data<ChapterAttributes>,
    var isRead: MutableState<Boolean> = mutableStateOf(false)
) {
    operator fun invoke(): Data<ChapterAttributes> = data
}

fun ListResponse<ChapterAttributes>.toChapters(): List<Chapter> =
    data.map { Chapter(it) }