package api.mangadex.model.request

data class RefreshTokenRequest(
    val grantType: String = "refresh_token",
    val refreshToken: String,
    val clientId: String,
    val clientSecret: String
)