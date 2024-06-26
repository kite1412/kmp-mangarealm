package api.mangadex.service

import api.mangadex.model.response.Token
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// Handle token refresh and act as the SSoT for accessing the token.
interface TokenHandler {
    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun expiration(token: Token): Long {
            val splitted = token.accessToken.split(".")
            val payload = Base64.decode(splitted[1]).decodeToString()
            return payload.substringBefore(',').substringAfter(':').toLong()
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun expiration(token: String): Long {
            val splitted = token.split(".")
            val payload = Base64.decode(splitted[1]).decodeToString()
            return payload.substringBefore(',').substringAfter(':').toLong()
        }
    }

    suspend operator fun invoke(): String?
}