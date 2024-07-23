package api.mangadex.service

import api.mangadex.model.request.CreateCustomList
import api.mangadex.model.request.Queries
import api.mangadex.model.request.Status
import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.HomeUrl
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.MangaStatus
import api.mangadex.model.response.SimpleResponse
import api.mangadex.model.response.Token
import api.mangadex.model.response.UpdateStatusResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.model.response.attribute.CustomListAttributes
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.model.response.attribute.TagAttributes
import api.mangadex.model.response.attribute.UserAttributes
import api.mangadex.util.Status as S

interface MangaDex {
    val paging: Paging

    suspend fun login(request: TokenRequest): Token?

    suspend fun getManga(queries: Queries = ""): ListResponse<MangaAttributes>?

    suspend fun getMangaByStatus(status: Status = S.NONE): MangaStatus?

    suspend fun getTags(): ListResponse<TagAttributes>?

    suspend fun getLoggedInUser(): EntityResponse<UserAttributes>?

    suspend fun getMangaChapters(mangaId: String, queries: Queries = ""): ListResponse<ChapterAttributes>?

    suspend fun getHomeUrl(chapterId: String): HomeUrl?

    suspend fun updateMangaStatus(mangaId: String, status: String): UpdateStatusResponse?

    suspend fun getUserCustomLists(queries: Queries): ListResponse<CustomListAttributes>?

    suspend fun createCustomList(request: CreateCustomList): EntityResponse<CustomListAttributes>?

    suspend fun deleteCustomList(customListId: String): Boolean

    suspend fun addMangaToCustomList(mangaId: String, customListId: String): Boolean

    suspend fun removeMangaFromCustomList(mangaId: String, customListId: String): Boolean

    suspend fun getMangaReadMarkers(mangaId: String): SimpleResponse<List<String>>?

    suspend fun updateMangaReadMarkers(mangaId: String, readIds: List<String>, unreadIds: List<String> = listOf()): Boolean

    interface Paging {
        fun <R> nextPageExists(r: ListResponse<R>): Boolean =
            r.offset < r.total

        suspend fun manga(prevResponse: ListResponse<MangaAttributes>, queries: Queries = ""): ListResponse<MangaAttributes>?

        suspend fun chapters(prevResponse: ListResponse<ChapterAttributes>, queries: Queries = ""): ListResponse<ChapterAttributes>?
    }
}