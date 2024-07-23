package model

import api.mangadex.model.request.Queries

data class ChapterKey(
    val mangaId: String,
    val queries: Queries
)