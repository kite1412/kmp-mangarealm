package api.mangadex.service

import api.mangadex.model.request.Queries
import api.mangadex.model.request.Status
import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.HomeUrl
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.MangaStatus
import api.mangadex.model.response.Token
import api.mangadex.model.response.UpdateStatusResponse
import api.mangadex.model.response.attribute.ChapterAttributes
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.model.response.attribute.TagAttributes
import api.mangadex.model.response.attribute.UserAttributes
import api.mangadex.util.ApiConstant
import api.mangadex.util.DataType
import api.mangadex.util.generateQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import util.Log

class MangaDexImpl(
    private val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    },
    private val token: TokenHandler = TokenHandlerImpl(client)
) : MangaDex {
    override val paging: MangaDex.Paging
        get() = Paging()

    private suspend fun HttpRequestBuilder.authHeader() {
        header("Authorization", "Bearer ${token()}")
    }

    private suspend inline fun <reified R> get(
        url: String,
        methodName: String,
        queries: Queries = "",
        auth: Boolean = false,
    ): R? {
        return try {
            client.get {
                url("$url$queries")
                if (auth) authHeader()
            }.body<R>()
        } catch (e: Exception) {
            e.message?.let {
                Log.e("($methodName) $it")
            }
            null
        }
    }

    private suspend inline fun <reified ATTR> getList(
        url: String,
        methodName: String,
        queries: Queries = "",
        auth: Boolean = false,
    ): ListResponse<ATTR>? {
        return get<ListResponse<ATTR>>(
            url, methodName, queries, auth
        ).also {
            if (it != null) {
                Log.d("GET ($methodName$queries) list length: ${it.data.size}")
            }
        }
    }

    private suspend inline fun <reified R> post(
        url: String,
        body: Any,
        methodName: String,
        auth: Boolean = true,
        headers: Map<String, String> = mapOf()
    ): R? {
        return try {
            client.post(urlString = url) {
                if (auth) authHeader()
                contentType(type = ContentType.parse("application/json"))
                if (headers.isNotEmpty()) headers {
                    headers.forEach {
                        append(it.key, it.value)
                    }
                }
                setBody(body)
            }.body<R>()
        } catch (e: Exception) {
            e.message?.let {
                Log.e("($methodName) $it")
            }
            null
        }
    }

    override suspend fun login(request: TokenRequest): Token? {
        return try {
            client.submitForm(
                url = ApiConstant.AUTH_ENDPOINT,
                formParameters = parameters {
                    append("grant_type", request.grantType)
                    append("username", request.username)
                    append("password", request.password)
                    append("client_id", request.clientId)
                    append("client_secret", request.clientSecret)
                }
            ).body<Token>().also {
                Log.d("POST (login) success retrieving token")
            }
        } catch (err: Throwable) {
            err.message?.let {
                Log.e("(login) $it")
            }
            null
        }
    }

    override suspend fun getManga(queries: Queries): ListResponse<MangaAttributes>? =
        getList(
            url = ApiConstant.MANGA_ENDPOINT,
            methodName = "getManga",
            queries = queries
        )

    override suspend fun getMangaByStatus(status: Status): MangaStatus? {
        var q = ""
        if (status.isNotEmpty()) q = "?status=$status"
        return get<MangaStatus?>(
            url = ApiConstant.MANGA_STATUS,
            methodName = "getMangaByStatus",
            queries = q,
            auth = true
        ).also {
            if (it != null) Log.d("GET (getMangaByStatus$q) list length: ${it.statuses.size}")
        }
    }

    override suspend fun getTags(): ListResponse<TagAttributes>? =
        getList(
            url = ApiConstant.TAGS_ENDPOINT,
            methodName = "getTags"
        )

    override suspend fun getLoggedInUser(): EntityResponse<UserAttributes>? =
        get<EntityResponse<UserAttributes>?>(
            url = "${ApiConstant.BASE_URL}/user/me",
            methodName = "getLoggedInUser",
            auth = true
        ).also {
            Log.d("GET (getLoggedInUser) success retrieving user information")
        }

    override suspend fun getMangaChapters(mangaId: String, queries: Queries): ListResponse<ChapterAttributes>? =
        getList(
            url = ApiConstant.mangaChapters(mangaId),
            methodName = "getMangaChapters",
            queries = queries
        )

    override suspend fun getHomeUrl(chapterId: String): HomeUrl? =
        get<HomeUrl?>(
            url = ApiConstant.chapterImagesUrl(chapterId),
            methodName = "getHomeUrl"
        ).also {
            Log.d("GET (getHomeUrl) data length: ${it?.chapter?.data?.size}, " +
                    "dataSaver length: ${it?.chapter?.dataSaver?.size}")
        }

    suspend fun updateMangaStatus(
        mangaId: String,
        status: String,
        auth: Boolean = true,
        headers: Map<String, String> = mapOf()
    ): UpdateStatusResponse? =
        post<UpdateStatusResponse>(
            url = ApiConstant.updateMangaStatus(mangaId),
            body = mapOf("status" to status.ifEmpty { null }),
            methodName = "updateMangaStatus",
            auth = auth,
            headers = headers
        )


    override suspend fun updateMangaStatus(mangaId: String, status: String): UpdateStatusResponse? =
        updateMangaStatus(mangaId, status, true)?.also {
            Log.d("POST (updateMangaStatus) success updating manga status")
        }


    private inner class Paging : MangaDex.Paging {
        private suspend fun <R> nextPage(
            prevResponse: ListResponse<R>,
            get: suspend (offset: Int) -> ListResponse<R>?
        ): ListResponse<R>? = if (nextPageExists(prevResponse)) null else
            get(prevResponse.offset + prevResponse.limit)

        private fun getMangaId(r: ListResponse<ChapterAttributes>): String? = run {
            r.data.first().relationships.forEach {
                if (it.type == DataType.MANGA) return@run it.id
            }
            null
        }

        override suspend fun manga(
            prevResponse: ListResponse<MangaAttributes>,
            queries: Queries
        ): ListResponse<MangaAttributes>? = nextPage(prevResponse) {
            getManga(generateQuery(mapOf("offset" to it), queries))
        }

        override suspend fun chapters(
            prevResponse: ListResponse<ChapterAttributes>,
            queries: Queries
        ): ListResponse<ChapterAttributes>? = nextPage(prevResponse) {
            val mangaId = getMangaId(prevResponse)
            if (mangaId == null) return@nextPage null else
                getMangaChapters(
                    mangaId = mangaId,
                    queries = generateQuery(
                        queryParams = mapOf("offset" to it),
                        otherParams = queries
                    )
                )
        }
    }
}