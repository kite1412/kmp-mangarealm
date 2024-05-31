package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.MangaAttributes
import api.mangadex.util.AUTH_ENDPOINT
import api.mangadex.util.MANGA_ENDPOINT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
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
                url = AUTH_ENDPOINT,
                formParameters = parameters {
                    append("grant_type", request.grantType)
                    append("username", request.username)
                    append("password", request.password)
                    append("client_id", request.clientId)
                    append("client_secret", request.clientSecret)
                }
            ).body<Token>().also {
                Log.d("success retrieving token")
            }
        } catch (err: Throwable) {
            err.cause?.message?.let {
                Log.e(it)
            }
            null
        }
    }

    override suspend fun getManga(queries: String): ListResponse<MangaAttributes>? {
        return try {
            client.get("$MANGA_ENDPOINT/$queries").body<ListResponse<MangaAttributes>>()
        } catch (e: Throwable) {
            e.cause?.message?.let {
                Log.e(it)
            }
            null
        }
    }
}