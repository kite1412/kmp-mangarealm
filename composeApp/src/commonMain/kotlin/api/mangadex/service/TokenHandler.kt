package api.mangadex.service

import api.mangadex.model.response.Token
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// Handle token refresh and act as the SSoT for accessing the token.
interface TokenHandler {
    /**
     * @param failCallback invoked should attempt to retrieve the token fail, typically attempt to get a new token than refreshed one.
     */
    suspend operator fun invoke(failCallback: suspend () -> Token?): String?

    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun expiration(token: String): Long {
            val split = token.split(".")
            val payload = Base64.decode(split[1]).decodeToString()
            return payload.substringBefore(',').substringAfter(':').toLong()
        }
    }
}