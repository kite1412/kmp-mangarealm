package model

import api.mangadex.model.request.Queries

data class ChapterKey(
    val manga: Manga,
    val queries: Queries
)
