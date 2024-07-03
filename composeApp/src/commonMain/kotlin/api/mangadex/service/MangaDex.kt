package api.mangadex.service

import api.mangadex.model.request.Queries
import api.mangadex.model.request.Status
import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.MangaStatus
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.model.response.attribute.TagAttributes
import api.mangadex.model.response.attribute.UserAttributes
import api.mangadex.util.Status as S

interface MangaDex {
    val nextPage: Paging

    suspend fun login(request: TokenRequest): Token?

    suspend fun getManga(queries: Queries = ""): ListResponse<MangaAttributes>?

    suspend fun getMangaByStatus(status: Status = S.ALL): MangaStatus?

    suspend fun getTags(): ListResponse<TagAttributes>?

    suspend fun getLoggedInUser(): EntityResponse<UserAttributes>?

    suspend fun getMangaChapters(mangaId: String, queries: Queries = ""): ListResponse<ChapterAttributes>?

    // TODO add queries param
    interface Paging {
        suspend fun manga(prevResponse: ListResponse<MangaAttributes>): ListResponse<MangaAttributes>?

        suspend fun chapters(prevResponse: ListResponse<ChapterAttributes>): ListResponse<ChapterAttributes>?
    }
}