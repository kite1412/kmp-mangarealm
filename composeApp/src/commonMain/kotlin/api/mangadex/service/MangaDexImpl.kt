package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.EntityResponse
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.MangaStatus
import api.mangadex.model.response.Token
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

    override suspend fun getManga(queries: String): ListResponse<MangaAttributes>? {
        return try {
            client.get("${ApiConstant.MANGA_ENDPOINT}/$queries")
                .body<ListResponse<MangaAttributes>>()
                .also {
                    Log.d("GET (getManga$queries) list length: ${it.data.size}")
                }
        } catch (e: Throwable) {
            e.message?.let {
                Log.e("(getManga) $it")
            }
            null
        }
    }

    private suspend fun HttpRequestBuilder.authHeader() {
        header("Authorization", "Bearer ${token()}")
    }

    override suspend fun getMangaByStatus(status: String): MangaStatus? {
        return try {
            var q = ""
            if (status.isNotEmpty()) {
                q = "?status=$status"
            }
            client.get {
                url("${ApiConstant.MANGA_STATUS}$q")
                authHeader()
            }
                .body<MangaStatus>()
                .also {
                    Log.d("GET (getMangaByStatus$q) status length: ${it.statuses.size} ")
                }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("(getMangaByStatus) $it")
            }
            null
        }
    }

    override suspend fun getTags(): ListResponse<TagAttributes>? {
        return try {
            client.get("${ApiConstant.MANGA_ENDPOINT}/tag")
                .body<ListResponse<TagAttributes>>()
                .also {
                    Log.d("GET (getTags) tags length: ${it.data.size}")
                }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("(getTags) $it")
            }
            null
        }
    }

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
}