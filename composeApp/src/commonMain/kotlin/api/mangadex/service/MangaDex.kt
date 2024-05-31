package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.MangaAttributes

interface MangaDex {
    suspend fun login(request: TokenRequest): Token?

    suspend fun getManga(queries: String = ""): ListResponse<MangaAttributes>?
}