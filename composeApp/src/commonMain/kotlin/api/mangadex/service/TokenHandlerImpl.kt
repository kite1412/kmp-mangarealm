package api.mangadex.service

import Libs
import api.mangadex.model.request.RefreshTokenRequest
import api.mangadex.model.response.Token
import api.mangadex.util.AUTH_ENDPOINT
import io.github.irgaly.kottage.get
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import util.Log

class TokenHandlerImpl(private val client: HttpClient) : TokenHandler {
    private val scope = CoroutineScope(Dispatchers.Default)
    private val storage = Libs.kottageStorage

    private suspend fun refreshToken(refresh: RefreshTokenRequest): Token? {
        return try {
            client.submitForm(
                url = AUTH_ENDPOINT,
                formParameters = parameters {
                    append("grant_type", refresh.grantType)
                    append("refresh_token", refresh.refreshToken)
                    append("client_id", refresh.clientId)
                    append("client_secret", refresh.clientSecret)
                }
            ).body<Token>().also {
                Log.d("success refreshing token")
            }
        } catch (err: Throwable) {
            err.cause?.message?.let {
                Log.e(it)
            }
            null
        }
    }

    // TODO implement
    override suspend fun invoke(): Boolean {
        val expiredIn = storage.get<Int>("expiredIn")
        return false
    }
}