package api.mangadex.service

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// Handle token refresh and act as the SSoT for accessing the token.
interface TokenHandler {
    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun expiration(token: String): Long {
            val split = token.split(".")
            val payload = Base64.decode(split[1]).decodeToString()
            return payload.substringBefore(',').substringAfter(':').toLong()
        }
    }

    suspend operator fun invoke(): String?
}