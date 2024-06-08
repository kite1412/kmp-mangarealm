package api.mangadex.service

import Libs
import api.mangadex.model.request.RefreshTokenRequest
import api.mangadex.model.response.Token
import api.mangadex.util.ApiConstant
import io.github.irgaly.kottage.get
import io.github.irgaly.kottage.getOrNull
import io.github.irgaly.kottage.put
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import shared.currentTimeMillis
import util.KottageConst
import util.Log
import kotlin.time.Duration.Companion.days

class TokenHandlerImpl(private val client: HttpClient) : TokenHandler {
    private val storage = Libs.kottageStorage

    private suspend fun refreshToken(request: RefreshTokenRequest): Token? {
        return try {
            client.submitForm(
                url = ApiConstant.AUTH_ENDPOINT,
                formParameters = parameters {
                    append("grant_type", request.grantType)
                    append("refresh_token", request.refreshToken)
                    append("client_id", request.clientId)
                    append("client_secret", request.clientSecret)
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

    private suspend fun saveTokenToLocal(token: Token) {
        val expiresIn = TokenHandler.expiration(token)
        val refreshExpiresIn = storage.get<Int>(KottageConst.REFRESH_EXPIRES_IN)
        storage.put<String>(
            KottageConst.TOKEN,
            token.accessToken
        )
        storage.put<Int>(
            KottageConst.EXPIRES_IN,
            expiresIn
        )
        if (refreshExpiresIn < (currentTimeMillis + 30.days.inWholeMilliseconds)) {
            storage.put<String>(
                KottageConst.REFRESH_TOKEN,
                token.refreshToken
            )
            storage.put<Int>(
                KottageConst.REFRESH_EXPIRES_IN,
                TokenHandler.expiration(token.refreshToken)
            )
        }
    }

    // return null only if token is not exist in local storage (the least probability to happen)
    // or unable to refresh the token (problems either with connection or server).
    override suspend fun invoke(): String? {
        val token = storage.getOrNull<String>(KottageConst.TOKEN)
        if (token != null) {
            try {
                val expiredIn = storage.get<Int>(KottageConst.EXPIRES_IN)
                val currentMillis = currentTimeMillis
                if (expiredIn < currentMillis) {
                    return token
                }
                val refresh = refreshToken(RefreshTokenRequest(storage))
                if (refresh != null) {
                    saveTokenToLocal(refresh)
                    return refresh.accessToken
                }
                return null
            } catch (e: Throwable) {
                return null
            }
        }
        return null
    }
}