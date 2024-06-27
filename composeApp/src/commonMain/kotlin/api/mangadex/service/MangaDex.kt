package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.MangaStatus
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.model.response.attribute.TagAttributes
import api.mangadex.model.response.attribute.UserAttributes
import api.mangadex.util.Status

interface MangaDex {
    suspend fun login(request: TokenRequest): Token?

    suspend fun getManga(queries: String = ""): ListResponse<MangaAttributes>?

    suspend fun getMangaByStatus(status: String = Status.ALL): MangaStatus?

    suspend fun getTags(): ListResponse<TagAttributes>?

    suspend fun getLoggedInUser(): EntityResponse<UserAttributes>?
}