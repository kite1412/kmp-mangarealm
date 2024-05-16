package api.mangadex.service

// Handle token refresh and act as the SSoT for accessing the token.
interface TokenHandler {
    suspend fun invoke(): Boolean
}