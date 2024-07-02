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
import api.mangadex.util.ApiConstant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
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

    override suspend fun getLoggedInUser(): EntityResponse<UserAttributes>? {
        return try {
            client.get("${ApiConstant.BASE_URL}/user/me") {
                authHeader()
            }.body<EntityResponse<UserAttributes>>()
        } catch (e: Exception) {
            e.message?.let {
                Log.e("(getLoggedInUser) $it")
            }
            null
        }
    }

    override suspend fun getMangaChapters(mangaId: String, queries: Queries): ListResponse<ChapterAttributes>? =
        getList(
            url = ApiConstant.MANGA_CHAPTERS(mangaId),
            methodName = "getMangaChapters",
            queries = queries
        )
}