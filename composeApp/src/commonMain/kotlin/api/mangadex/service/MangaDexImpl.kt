package api.mangadex.service

import api.mangadex.model.request.TokenRequest
import api.mangadex.model.response.ClientSecret
import api.mangadex.model.response.ListResponse
import api.mangadex.model.response.Token
import api.mangadex.model.response.attribute.ClientAttributes
import api.mangadex.util.AUTH_ENDPOINT
import api.mangadex.util.BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import util.Log

class MangaDexImpl : MangaDex {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val token = TokenHandlerImpl(client)

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

    override suspend fun getClientSecret(id: String): ClientSecret? {
        return try {
            client.get("$BASE_URL/$id/secret").body<ClientSecret>()
        } catch (e: Throwable) {
            e.cause?.message?.let {
                Log.e(it)
            }
            null
        }
    }

    override suspend fun getClients(): ListResponse<ClientAttributes, Unit>? {
        return try {
            TODO()
        } catch (e: Throwable) {
            e.cause?.message?.let {
                Log.e(it)
            }
            null
        }
    }
}