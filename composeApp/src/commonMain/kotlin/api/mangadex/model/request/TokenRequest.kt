package api.mangadex.model.request

data class TokenRequest(
    val grantType: String = "password",
    val username: String,
    val password: String,
    val clientId: String,
    val clientSecret: String,
)