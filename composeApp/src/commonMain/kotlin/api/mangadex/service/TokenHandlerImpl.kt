package api.mangadex.service

import Libs
import api.mangadex.model.request.RefreshTokenRequest
import api.mangadex.model.response.Token
import api.mangadex.util.ApiConstant
import error.UnableRefreshTokenException
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
import util.retry
import kotlin.time.Duration.Companion.days

class TokenHandlerImpl(private val client: HttpClient) : TokenHandler {
    private val storage = Libs.kottageStorage

    private suspend fun refresh(request: RefreshTokenRequest, useReserved: Boolean = false): Token? = retry<Token?>(
        maxAttempts = 3,
        predicate = { it == null || it.error != null }
    ) {
        try {
            client.submitForm(
                url = ApiConstant.AUTH_ENDPOINT,
                formParameters = parameters {
                    append("grant_type", request.grantType)
                    append("refresh_token",
                        if (useReserved) request.reserveRefreshToken else request.refreshToken)
                    append("client_id", request.clientId)
                    append("client_secret", request.clientSecret)
                }
            ).body<Token>()
        } catch (e: Exception) {
            e.message?.let {
                Log.e("(refresh) $it")
            }
            null
        }
    }

    private suspend fun refreshToken(request: RefreshTokenRequest): Token? {
        return try {
            var useReserve = false
            var res = refresh(request)
            // TODO handle if reserved refresh token also not works, typically prompting user to re-login
            if (res == null ||res.error != null) {
                Log.w("(refreshToken) ${res?.error}")
                useReserve = true
                Log.w("(refreshToken) using reserved refresh token")
                res = refresh(request, true)
            }
            if (res == null || res.error != null) throw UnableRefreshTokenException()
            res.also {
                saveTokenToLocal(it, request.refreshToken)
                Log.d("(refreshToken) success refreshing token")
                if (useReserve) {
                    storage.put(KottageConst.REFRESH_TOKEN, request.reserveRefreshToken)
                }
            }
        } catch (err: Throwable) {
            err.cause?.message?.let {
                Log.e("(refreshToken) $it")
            }
            null
        }
    }

    private suspend fun saveTokenToLocal(token: Token, refreshToken: String? = null) {
        val refresh = refreshToken ?: storage.get<String>(KottageConst.REFRESH_TOKEN)
        val refreshExpiresIn = TokenHandler.expiration(refresh)
        storage.put(KottageConst.TOKEN, token.accessToken)
        storage.put(KottageConst.RESERVE_REFRESH_TOKEN, token.refreshToken)
        if (refreshExpiresIn < (currentTimeMillis + 30.days.inWholeMilliseconds))
            storage.put<String>(KottageConst.REFRESH_TOKEN, token.refreshToken)
    }

    private fun adjustLength(length: Int, number: Long): Long {
        var s = number.toString()
        if (s.length > length) {
            s = s.removeRange(length until s.length)
        } else if (s.length < length) {
            s = s.padEnd(length, '0')
        }
        return s.toLong()
    }

    // return null only if token is not exist in local storage (the least possibility to happen)
    // or unable to refresh the token (problems either with connection or server).
    override suspend fun invoke(): String? {
        val token = storage.getOrNull<String>(KottageConst.TOKEN)
        if (token != null) {
            try {
                val expiresIn = TokenHandler.expiration(token)
                val currentMillis = adjustLength(expiresIn.toString().length, currentTimeMillis)
                if (expiresIn > currentMillis) {
                    Log.d("(token) using current token")
                    return token
                }
                return refreshToken(RefreshTokenRequest(storage))?.accessToken
            } catch (e: Throwable) {
                e.message?.let {
                    Log.e("(token) $it")
                }
                return null
            }
        }
        return null
    }
}